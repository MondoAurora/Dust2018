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
		} else {
			ContextRef cr = (ContextRef) e;
			DustDataEntity se = mapCtxEntities.get(cr);
			return se;
		}
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
		
        if ( !accCtrl.isAccessAllowed(mapCtxEntities.get(ContextRef.self), se, sKey, cmd)) {
            throw new DustException("Access denied");
        }
        
		switch (cmd) {
		case getEntity:
			retVal = invokeEntity(se, sKey, val, (EntityProcessor) hint);
			break;
		case cloneEntity:
			retVal = cloneEntity(se);
			break;
		case dropEntity:
			if ( val instanceof Collection<?> ) {
				for ( DustDataEntity sde : ((Collection<DustDataEntity>)val) ) {
					dropEntity(sde);
				}
			} else {
				dropEntity(se);
			}
			break;
		case tempSend:
			binConn.send(se, (DustDataEntity) key);
			break;
		case getValue:
			// nothing, retVal already set
			break;
		case setValue:
			retVal = se.put(key, val);

			if (!DustUtilsJava.isEqual(retVal, val)) {
				notifyListeners(cmd, se, key, val, retVal);
			}
			break;
		case processContent:
			ContentProcessor cp = (ContentProcessor) val;
			for ( Map.Entry<DustEntity, Object> ee : se.content.entrySet() ) {
				cp.processContent(se, ee.getKey(), ee.getValue());
			}
			break;
		case processRef:
			if (null != retVal) {
				((DustDataRef) retVal).processAll((RefProcessor) val);
			}
			break;
		default:
			retVal = changeRef(true, cmd, se, key, (DustDataRef) retVal, optResolveCtxEntity(val), hint);
			break;
		}
		return (RetType) retVal;
	}

	private DustDataEntity invokeEntity(DustDataEntity type, DustDataEntity owner, Object id, EntityProcessor initializer) {
		String gid = (String) id;
		DustDataEntity ce = ctxGetEntity(gid);

		if (ce.justCreated) {
//			if ( null != gid ) {
//				DustUtils.accessEntity(DataCommand.setValue, ce, DustCommAtts.PersistentEntityId, gid);
//			}
			if (null != type) {
				ctxAccessEntity(DataCommand.setRef, ce, EntityResolver.getEntity(DustDataLinks.EntityPrimaryType), type,
						null);
			}
			if (null != owner) {
				ctxAccessEntity(DataCommand.setRef, ce, EntityResolver.getEntity(DustGenericLinks.ConnectedOwner), owner, null);
			}
			
			if (null != initializer) {
				initializer.processEntity(ce);
			}
			
			if (null != type) {
				DustDataRef r = type.get(DustMetaLinks.TypeLinkedServices);
				if ( null != r) {
					ctxAccessEntity(DataCommand.setRef, ce, EntityResolver.getEntity(DustDataLinks.EntityServices), r.target, null);
				}
			}
			
			ce.justCreated = false;
			notifyListeners(DataCommand.getEntity, ce, null, id, null);
		}
		return ce;
	}

	private DustDataEntity cloneEntity(DustDataEntity source) {
		DustDataRef refPt = source.get(DustDataLinks.EntityPrimaryType);
		DustDataEntity ret = invokeEntity((null == refPt) ? null : refPt.get(RefKey.target), null, null, null);
		
		for ( Map.Entry<DustEntity, Object> se : source.content.entrySet() ) {
			DustEntity key = se.getKey();
			if ( DustUtils.tag(key, TagCommand.test, DustMetaTags.NotCloned)) {
				continue;
			}
			Object val = se.getValue();
			
			if ( val instanceof DustDataRef ) {
				DustDataRef rr = (DustDataRef) val;
				
				if ( (null != rr.reverse ) && (rr.reverse.lt == DustMetaLinkDefTypeValues.LinkDefSingle)) {
					DustUtilsDev.dump("In clone, skipping", key, "because the reverse link is single.");
					continue;
				}
				rr.processAll(new RefProcessor() {
					DustDataRef lastRef = null;
					
					@Override
					public void processRef(DustRef ref) {
						lastRef = changeRef(true, DataCommand.setRef, ret, key, lastRef, rr.target, rr.key);
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
		notifyListeners(DataCommand.dropEntity, entity, null, null, entity);
		
		Set<DustDataRef> toDel = new HashSet<>();
		for ( DustDataRef sr : refs ) {
			if ( (entity == sr.target) || (entity == sr.source) ) {
				toDel.add(sr);
			}
		}
		for ( DustDataRef sr : toDel ) {
			if ( refs.contains(sr) ) {
				sr.remove((entity == sr.source), true);
			}
		}
	}

	public DustDataRef changeRef(boolean handleReverse, DataCommand cmd, DustDataEntity se, DustEntity key,
			DustDataRef actRef, Object val, Object collId) {
		DustDataRef sr = null;
		 ArrayList<DustDataRef> al;

        switch (cmd) {
		case removeRef:
			if (null != actRef) {
				Collection<DustDataEntity> mdls = null;

				if ( val instanceof Collection<?>) {
					mdls = (Collection<DustDataEntity>) val;
					for ( DustDataEntity sde : mdls ) {
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
					
					for (Map.Entry<DustEntity, Object> ee : se.content.entrySet()) {
						DustDataEntity eKey = (DustDataEntity) ee.getKey();
						Object eval = ee.getValue();

						boolean isRef = (eval instanceof DustDataRef);
						DustDataEntity pM = eKey.getSingleRef(
								isRef ? DustMetaLinks.LinkDefParent : DustMetaLinks.AttDefParent);

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
							notifyListeners(DataCommand.setValue, se, eKey, null, eval);
							se.content.remove(eKey);
						}
					}
				}
			}
			break;
		case setRef:
			DustDataEntity eTarget = optResolveCtxEntity(val);

            if (null != actRef) {
                if (DustMetaLinkDefTypeValues.LinkDefSet == actRef.lt) {
                    for (DustDataRef er : ((Set<DustDataRef>) actRef.container)) {
                        if (er.target == eTarget) {
                            return er;
                        }
                    }
                }
                if ((DustMetaLinkDefTypeValues.LinkDefArray == actRef.lt) && (collId instanceof Integer)) {
                    al = (ArrayList<DustDataRef>) actRef.container;
                    int idx = (int) collId;
                    if (al.size() > idx) {
                        DustDataRef er = al.get(idx);
                        if (er.target == eTarget) {
                            return er;
                        }
                    }
                }
            }
            
			sr = new DustDataRef(this, (DustDataEntity) key, se, (DustDataEntity) eTarget, collId, actRef);

			if ((null != actRef) && (DustMetaLinkDefTypeValues.LinkDefSingle == sr.lt)) {
				if (DustUtilsJava.isEqual(eTarget, actRef.target)) {
					return actRef;
				}

				actRef.remove(false, true);
				se.put(key, sr);
				notifyListeners(cmd, se, key, sr, actRef);
			} else {
				if (null == actRef) {
					se.put(key, sr);
				}
				notifyListeners(cmd, se, key, sr, actRef);
			}

			refs.add(sr);
			
			if ( EntityResolver.getEntity(DustDataLinks.EntityModels) == key ) {
				DustDataRef r = eTarget.get(DustGenericLinks.ConnectedRequires);
				if ( null != r ) {
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
		    for ( DustDataRef r : rl ) {
		        if ( val == r.target ) {
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

	void notifyListeners(DataCommand cmd, DustDataEntity entity, DustEntity key, 
			Object newVal, Object oldVal) {
		DustDataRef listeners = ctxSelf.get(DustProcLinks.SessionChangeListeners);

		if ((DataCommand.setRef == cmd) && (null != newVal)
				&& (key == EntityResolver.getEntity(DustDataLinks.EntityServices))) {
			DustDataEntity svc = ((DustDataRef) newVal).target;
			binConn.instSvc(entity, svc);
			if (DustUtils.isTrue(svc, DustProcAtts.BinaryAutoInit)) {
				DustDataEntity init = new DustDataEntity(this, true);

				init.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.ActiveInit);

				binConn.send(entity, init);
			}
		}

		if (null != listeners) {
			listeners.processAll(new RefProcessor() {
			    
			    LazyMsgContainer lmc = new LazyMsgContainer() {
                    @Override
                    protected DustDataEntity createMsg() {
                        DustDataEntity chg = new DustDataEntity(DustProcSession.this, true);

                        chg.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.ListenerProcessChange);

                        chg.putLocalRef(DustProcLinks.ChangeCmd, cmd);
                        chg.putLocalRef(DustProcLinks.ChangeEntity, entity);
                        chg.putLocalRef(DustProcLinks.ChangeKey, (DustDataEntity) key);

                        chg.put(DustProcAtts.ChangeOldValue, oldVal);
                        chg.put(DustProcAtts.ChangeNewValue, newVal);
                        
                        return chg;
                    }};

				@Override
				public void processRef(DustRef ref) {
					DustDataEntity listener = ((DustDataRef) ref).target;

					if (DustUtilsJava.isEqualLenient(cmd, listener.getSingleRef(DustProcLinks.ChangeCmd))
							&& DustUtilsJava.isEqualLenient(entity, listener.getSingleRef(DustProcLinks.ChangeEntity))
							&& DustUtilsJava.isEqualLenient(key, listener.getSingleRef(DustProcLinks.ChangeKey))) {

						binConn.send(listener, lmc.getMsg());
					}
				}
			});
		}
		
		DustEntity eEchg = EntityResolver.getEntity(DustDataTags.EntityChanged);
		boolean ect = ((oldVal instanceof DustRef) && (eEchg == ((DustRef)oldVal).get(RefKey.target)))
				|| ((newVal instanceof DustRef) && (eEchg == ((DustRef)newVal).get(RefKey.target)));
		
		if ( !ect ) {
			DustUtils.tag(entity, TagCommand.set, DustDataTags.EntityChanged);
		}
	}

	@Override
	public void ctxProcessEntities(EntityProcessor proc) {
		for (Object e : allEntities.toArray() ) {
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
