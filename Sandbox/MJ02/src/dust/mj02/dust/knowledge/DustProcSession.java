package dust.mj02.dust.knowledge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.Dust.DustContext;
import dust.mj02.dust.DustUtils;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;
import dust.utils.DustUtilsMuteManager;

@SuppressWarnings("unchecked")
public class DustProcSession implements DustKernelImplComponents, Dust.DustContext {

    DustContext ctxParent;

    DustUtilsFactory<Object, DustDataEntity> entities = new DustUtilsFactory<Object, DustDataEntity>(false) {
        @Override
        protected DustDataEntity create(Object key, Object... hints) {
            DustDataEntity se = new DustDataEntity(DustProcSession.this, false);
            return se;
        }
    };
    Set<DustDataEntity> allEntities = new HashSet<>();
    Set<DustDataRef> refs = new HashSet<>();

    EnumMap<ContextRef, DustDataEntity> mapCtxEntities = new EnumMap<>(ContextRef.class);

    DustProcAccessControl accCtrl = new DustProcAccessControl(this);
    DustProcBinaryConnector binConn = new DustProcBinaryConnector(this);
    DustDataEntity ctxSelf = new DustDataEntity(this, true);

    public DustProcSession(DustContext ctxParent) {
        this.ctxParent = ctxParent;
        mapCtxEntities.put(ContextRef.session, ctxSelf);

        allEntities.add(ctxSelf);
    }

    private DustDataEntity optResolveCtxEntity(Object e) {
        if (null == e) {
            return null;
        } else if (e instanceof DustDataEntity) {
            return (DustDataEntity) e;
        } else if (e instanceof DustEntityKey) {
            return (DustDataEntity) EntityResolver.getEntity(e);
        } else if (e instanceof ContextRef) {
            ContextRef cr = (ContextRef) e;
            DustDataEntity se = mapCtxEntities.get(cr);
            return se;
        }

        return null;
    }

    @Override
    public DustDataEntity ctxGetEntity(Object globalId) {
        return (null == globalId) ? new DustDataEntity(this, false) : entities.get(globalId);
    }

    @Override
    public <RetType> RetType ctxAccessEntity(DataCommand cmd, DustEntity e, DustEntity key, Object val, Object hint) {
        DustDataEntity se = optResolveCtxEntity(e);
        DustDataEntity sKey = optResolveCtxEntity(key);

        Object retVal = (null == se) ? null : (null == key) ? se : se.get(key);

        if (!accCtrl.isAccessAllowed(mapCtxEntities.get(ContextRef.self), se, sKey, cmd)) {
            throw new DustException("Access denied");
        }

        Throwable err = null;

        switch (cmd) {
        case getEntity:
            try {
                callEntry(se, null);
                retVal = invokeEntity(se, sKey, val, (EntityProcessor) hint);
            } catch (Throwable t) {
                err = t;
            } finally {
                callExit(se, null, err);
            }

            break;
        case cloneEntity:
            retVal = cloneEntity(se, new HashMap<>());
            break;
        case dropEntity:
            if (val instanceof Collection<?>) {
                for (DustDataEntity sde : ((Collection<DustDataEntity>) val)) {
                    dropEntity(sde);
                }
            } else {
                dropEntity(se);
            }
            break;
        case tempSend:
            DustDataEntity msg = (DustDataEntity) key;
            try {
                callEntry(se, msg);
                retVal = binConn.send(se, msg);
            } catch (Throwable t) {
                err = t;
            } finally {
                callExit(se, msg, err);
            }
            break;
        case getValue:
            // nothing, retVal already set
            break;
        case setValue:
            retVal = se.put((DustDataEntity) key, val);

            if (!DustUtilsJava.isEqual(retVal, val)) {
                collectChange(cmd, se, key, val, retVal);
            }
            break;
        case processContent:
            ContentProcessor cp = (ContentProcessor) val;
            for (Map.Entry<DustDataEntity, Object> ee : se.content.entrySet()) {
                cp.processContent(se, ee.getKey(), ee.getValue());
            }
            break;
        case processRef:
            if (null != retVal) {
                ((DustDataRef) retVal).processAll((RefProcessor) val);
            }
            break;
        default:
            Object resolvedVal = optResolveCtxEntity(val);
            retVal = changeRef(true, cmd, se, (DustDataEntity) key, (DustDataRef) retVal, (null == resolvedVal) ? val : resolvedVal, hint);
            break;
        }
        return (RetType) retVal;
    }

    private DustDataEntity invokeEntity(DustDataEntity type, DustDataEntity owner, Object id, EntityProcessor initializer) {
        String gid = (String) id;
        DustDataEntity ce = (EntityResolver.getEntity(DustDataTypes.Message) == type) ? new DustDataEntity(this, true) : ctxGetEntity(gid);

        if (ce.justCreated) {
            // if ( null != gid ) {
            // DustUtils.accessEntity(DataCommand.setValue, ce,
            // DustCommAtts.PersistentEntityId, gid);
            // }
            if (null != type) {
                ctxAccessEntity(DataCommand.setRef, ce, EntityResolver.getEntity(DustDataLinks.EntityPrimaryType), type, null);
            }
            if (null != owner) {
                ctxAccessEntity(DataCommand.setRef, ce, EntityResolver.getEntity(DustGenericLinks.ConnectedOwner), owner, null);
            }

            if (null != initializer) {
                initializer.processEntity(ce);
            }

            if (null != type) {
                DustDataRef r = type.get(DustMetaLinks.TypeLinkedServices);
                if (null != r) {
                    ctxAccessEntity(DataCommand.setRef, ce, EntityResolver.getEntity(DustDataLinks.EntityServices), r.target, null);
                }
            }

            ce.justCreated = false;
            collectChange(DataCommand.getEntity, ce, null, id, null);
        }
        return ce;
    }

    private DustDataEntity cloneEntity(DustDataEntity source, Map<DustDataEntity, DustDataEntity> clones) {
        DustDataEntity pt = source.getSingleRef(DustDataLinks.EntityPrimaryType);
        DustDataEntity ret = (EntityResolver.getEntity(DustDataTypes.Message) == pt) ? new DustDataEntity(this, true)
                : invokeEntity(pt, null, null, null);
        clones.put(source, ret);

        for (Map.Entry<DustDataEntity, Object> se : source.content.entrySet()) {
            DustDataEntity key = se.getKey();
            if (DustUtils.tag(key, TagCommand.test, DustMetaTags.NotCloned)) {
                continue;
            }
            Object val = se.getValue();

            if (val instanceof DustDataRef) {
                DustDataRef rr = (DustDataRef) val;

                if ((null != rr.reverse) && (rr.reverse.lt == DustMetaLinkDefTypeValues.LinkDefSingle)) {
                    DustUtilsDev.dump("In clone, skipping", key, "because the reverse link is single.");
                    continue;
                }
                rr.processAll(new RefProcessor() {
                    DustDataRef lastRef = null;

                    @Override
                    public void processRef(DustRef ref) {
                        DustDataRef actRef = (DustDataRef) ref;
                        DustDataEntity dt = clones.get(actRef.target);

                        if (null == dt) {
                            dt = (source == actRef.target.getSingleRef(DustGenericLinks.ConnectedOwner)) ? cloneEntity(actRef.target, clones)
                                    : actRef.target;
                        }
                        lastRef = changeRef(true, DataCommand.setRef, ret, key, lastRef, dt, actRef.key);
                    }
                });
            } else {
                ret.put(key, val);
            }
        }

        return ret;
    }

    private void dropEntity(DustDataEntity entity) {
        entities.drop(entity);
        allEntities.remove(entity);
        collectChange(DataCommand.dropEntity, entity, null, null, entity);

        Set<DustDataRef> toDel = new HashSet<>();
        for (DustDataRef sr : refs) {
            if ((entity == sr.target) || (entity == sr.source)) {
                toDel.add(sr);
            }
        }
        for (DustDataRef sr : toDel) {
            if (refs.contains(sr)) {
                sr.remove((entity == sr.source), true);
            }
        }
    }

    public DustDataRef changeRef(boolean handleReverse, DataCommand cmd, DustDataEntity se, DustDataEntity key, DustDataRef actRef, Object val,
            Object collId) {
        DustDataRef sr = null;
        ArrayList<DustDataRef> al;

        switch (cmd) {
        case removeRef:
            if (null != actRef) {
                Collection<DustDataEntity> mdls = null;

                if (val instanceof Collection<?>) {
                    mdls = (Collection<DustDataEntity>) val;
                    for (DustDataEntity sde : mdls) {
                        actRef.removeByTarget(sde);
                    }
                } else {
                    actRef.removeByTarget((DustDataEntity) val);
                }

                if (EntityResolver.getEntity(DustDataLinks.EntityModels) == key) {
                    if (null == mdls) {
                        mdls = new HashSet<>();
                        mdls.add((DustDataEntity) val);
                    }

                    Map<DustDataEntity, Object> toDel = new HashMap<>();

                    for (Map.Entry<DustDataEntity, Object> ee : se.content.entrySet()) {
                        DustDataEntity eKey = (DustDataEntity) ee.getKey();
                        Object eval = ee.getValue();

                        boolean isRef = (eval instanceof DustDataRef);
                        DustDataEntity pM = eKey.getSingleRef(isRef ? DustMetaLinks.LinkDefParent : DustMetaLinks.AttDefParent);

                        if (mdls.contains(pM)) {
                            toDel.put(eKey, eval);
                        }
                    }
                    for (Map.Entry<DustDataEntity, Object> ee : toDel.entrySet()) {
                        DustDataEntity eKey = ee.getKey();
                        Object eval = se.content.get(eKey);

                        boolean isRef = (eval instanceof DustDataRef);
                        if (isRef) {
                            ((DustDataRef) eval).remove(true, true);
                        } else {
                            collectChange(DataCommand.setValue, se, eKey, null, eval);
                            se.content.remove(eKey);
                        }
                    }
                }
            }
            break;
        case setRef:
            DustDataEntity eTarget = optResolveCtxEntity(val);

            if (null != actRef) {
                switch (actRef.lt) {
                case LinkDefArray:
                    if (collId instanceof Integer) {
                        al = (ArrayList<DustDataRef>) actRef.container;
                        int idx = (int) collId;
                        if (al.size() > idx) {
                            DustDataRef er = al.get(idx);
                            if (er.target == eTarget) {
                                return er;
                            }
                        }
                    }
                    break;
                case LinkDefMap:
                    DustDataRef mr = ((Map<Object, DustDataRef>) actRef.container).get(eTarget);
                    if (null != mr) {
                        return mr;
                    }
                    break;
                case LinkDefSet:
                    for (DustDataRef er : ((Set<DustDataRef>) actRef.container)) {
                        if (er.target == eTarget) {
                            return er;
                        }
                    }
                    break;
                case LinkDefSingle:
                    if ( actRef.target == eTarget ) {
                        return actRef;
                    }
                    break;
                default:
                    break;
                }
            }

            sr = new DustDataRef(this, (DustDataEntity) key, se, (DustDataEntity) eTarget, collId, actRef);

            if ((null != actRef) && (DustMetaLinkDefTypeValues.LinkDefSingle == sr.lt)) {
                if (DustUtilsJava.isEqual(eTarget, actRef.target)) {
                    return actRef;
                }

                actRef.remove(false, true);
                se.put(key, sr);
                collectChange(cmd, se, key, sr, actRef);
            } else {
                if (null == actRef) {
                    se.put(key, sr);
                }
                collectChange(cmd, se, key, sr, actRef);
            }

            refs.add(sr);

            if (EntityResolver.getEntity(DustDataLinks.EntityModels) == key) {
                DustDataRef r = eTarget.get(DustGenericLinks.ConnectedRequires);
                if (null != r) {
                    r.processAll(new RefProcessor() {
                        @Override
                        public void processRef(DustRef ref) {
                            ctxAccessEntity(DataCommand.setRef, se, key, ref.get(RefKey.target), null);
                        }
                    });
                }
            }

            if (handleReverse) {
                DustDataRef rr = sr.linkDef.get(DustMetaLinks.LinkDefReverse);
                if (null != rr) {
                    DustDataEntity revLink = rr.target;
                    DustDataRef rev = changeRef(false, cmd, eTarget, revLink, eTarget.get(revLink), se, collId);

                    sr.reverse = rev;
                    rev.reverse = sr;
                }
            }

            break;
        case clearRefs:
            if (null != actRef) {
                actRef.remove(true, true);
            }

            break;
        case updateRef:
            ArrayList<DustDataRef> rl = (ArrayList<DustDataRef>) actRef.container;
            DustDataRef rToMove = null;
            for (DustDataRef r : rl) {
                if (val == r.target) {
                    rToMove = r;
                    break;
                }
            }
            if (null != rToMove) {
                rl.remove(rToMove);
                rl.add((int) collId, rToMove);
            }
            break;
        default:
            throw new DustException("Should not get here!");
        }

        return sr;
    }

    private void callEntry(DustDataEntity target, DustDataEntity msg) {
        long depth = ctxSelf.get(DustProcAtts.SessionCallDepth, 0L);

        if (0 == depth) {
            DustDataEntity currStmt = new DustDataEntity(this, true);
            ctxSelf.putLocalRef(DustProcLinks.SessionCurrentStatement, currStmt);
        }

        ctxSelf.put(DustProcAtts.SessionCallDepth, ++depth);
    }

    private void callExit(DustDataEntity target, DustDataEntity msg, Throwable err) {
        long depth = ctxSelf.get(DustProcAtts.SessionCallDepth, 0L);

        try {
            if (null == err) {
                if (2 > depth) {
                    notifyChangeStatement();
                }
            } else {
                Dust.wrapAndRethrowException("Error in message processing", err);
            }
        } finally {
            ctxSelf.put(DustProcAtts.SessionCallDepth, --depth);
            if (2 > depth) {
                ctxSelf.put(DustProcLinks.SessionCurrentStatement, null);
            }
        }
    }

    void notifyChangeItem(DustDataRef listeners, DustDataEntity msg) {
        if (null != listeners) {
            DustEntity cmd = msg.getSingleRef(DustCommLinks.ChangeItemCmd);
            DustDataEntity entity = msg.getSingleRef(DustCommLinks.ChangeItemEntity);
            DustEntity key = msg.getSingleRef(DustCommLinks.ChangeItemKey);

            msg.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.ListenerProcessChange);

            listeners.processAll(new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    DustDataEntity listener = ((DustDataRef) ref).target;

                    if (DustUtilsJava.isEqualLenient(cmd, listener.getSingleRef(DustCommLinks.ChangeItemCmd))
                            && DustUtilsJava.isEqualLenient(entity, listener.getSingleRef(DustCommLinks.ChangeItemEntity))
                            && DustUtilsJava.isEqualLenient(key, listener.getSingleRef(DustCommLinks.ChangeItemKey))) {

                        binConn.send(listener, msg);
                    }
                }
            });
        }
    }
    
    boolean an = false;

    void notifyChangeAgents(DustDataEntity stmt) {
        if (an) {
            return;
        }

        try {
            an = true;
            DustDataRef agents = ctxSelf.get(DustProcLinks.SessionChangeAgents);

            if (null != agents) {
                // DustUtilsDev.dump("Notifying agents...");

                stmt.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.AgentProcessStatement);

                agents.processAll(new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        DustDataEntity agent = ((DustDataRef) ref).target;
                        binConn.send(agent, stmt);
                    }
                });
            }
        } finally {
            an = false;
        }
    }

    void notifyChangeStatement() {
        DustDataEntity stmt = ctxSelf.getSingleRef(DustProcLinks.SessionCurrentStatement);
        if (null == stmt) {
            return;
        }

        DustDataRef listeners = ctxSelf.get(DustProcLinks.SessionChangeListeners);

        if (Boolean.TRUE.equals(ctxSelf.get(DustProcAtts.SessionChangeMute))) {
            return;
        }

        if (null != listeners) {
            // DustUtilsDev.dump("Notifying listeners...");

            try {
                DustUtilsMuteManager.mute(DustUtilsMuteManager.MutableModule.GUI, true);

                DustDataRef changes = stmt.get(DustCollectionLinks.SequenceMembers);

                if (null != changes) {
                    changes.processAll(new RefProcessor() {
                        @Override
                        public void processRef(DustRef ref) {
                            DustDataEntity msg = ref.get(RefKey.target);
                            notifyChangeItem(listeners, msg);
                        }
                    });
                }
            } finally {
                DustUtilsMuteManager.mute(DustUtilsMuteManager.MutableModule.GUI, false);
            }
        }

        notifyChangeAgents(stmt);
    }

    void collectChange(DataCommand cmd, DustDataEntity entity, DustEntity key, Object newVal, Object oldVal) {
        entity.resetToString();

        if ((DataCommand.setRef == cmd) && (null != newVal) && (key == EntityResolver.getEntity(DustDataLinks.EntityServices))) {
            DustDataEntity svc = ((DustDataRef) newVal).target;
            binConn.instSvc(entity, svc);
            if (DustUtils.isTrue(svc, DustProcAtts.BinaryAutoInit)) {
                DustDataEntity init = new DustDataEntity(this, true);

                init.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.ActiveInit);

                binConn.send(entity, init);
            }
        }

        DustDataEntity chg = new DustDataEntity(DustProcSession.this, true);

        chg.putLocalRef(DustCommLinks.ChangeItemCmd, cmd);
        chg.putLocalRef(DustCommLinks.ChangeItemEntity, entity);
        chg.putLocalRef(DustCommLinks.ChangeItemKey, (DustDataEntity) key);

        chg.put(DustCommAtts.ChangeItemOldValue, oldVal);
        chg.put(DustCommAtts.ChangeItemNewValue, newVal);

        DustDataEntity currStmt = ctxSelf.getSingleRef(DustProcLinks.SessionCurrentStatement);

        if (null != currStmt) {
            // local hack to avoid circular call
            DustDataEntity lm = (DustDataEntity) EntityResolver.getEntity(DustCollectionLinks.SequenceMembers);
            DustDataRef origRef = currStmt.get(lm);
            if (null == origRef) {
                currStmt.putLocalRef(DustCollectionLinks.SequenceMembers, chg);
            } else {
                new DustDataRef(this, lm, currStmt, chg, null, origRef);
            }
        } else {
            DustDataRef listeners = ctxSelf.get(DustProcLinks.SessionChangeListeners);
            notifyChangeItem(listeners, chg);
            currStmt = new DustDataEntity(DustProcSession.this, true);
            currStmt.putLocalRef(DustCollectionLinks.SequenceMembers, chg);
            notifyChangeAgents(currStmt);
        }

        DustEntity eEchg = EntityResolver.getEntity(DustDataTags.EntityChanged);
        boolean ect = ((oldVal instanceof DustRef) && (eEchg == ((DustRef) oldVal).get(RefKey.target)))
                || ((newVal instanceof DustRef) && (eEchg == ((DustRef) newVal).get(RefKey.target)));

        if (!ect) {
            DustUtils.tag(entity, TagCommand.set, DustDataTags.EntityChanged);
        }
    }

    @Override
    public void ctxProcessEntities(EntityProcessor proc) {
        for (Object e : allEntities.toArray()) {
            proc.processEntity((DustEntity) e);
        }
    }

    @Override
    public void ctxProcessRefs(RefProcessor proc, DustEntity source, DustEntity linkDef, DustEntity target) {
        source = optResolveCtxEntity(source);
        target = optResolveCtxEntity(target);

        for (Object o : refs.toArray()) {
            DustDataRef ref = (DustDataRef) o;

            if (DustUtilsJava.isEqualLenient(ref.source, source) && DustUtilsJava.isEqualLenient(ref.linkDef, linkDef)
                    && DustUtilsJava.isEqualLenient(ref.target, target)) {
                proc.processRef(ref);
            }
        }
    }
}
