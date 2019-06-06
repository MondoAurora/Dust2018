package dust.mj02.dust.knowledge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dust.mj02.dust.DustComponents.DataCommand;
import dust.mj02.dust.DustComponents.DustEntity;
import dust.mj02.dust.DustComponents.DustEntityKey;
import dust.mj02.dust.DustComponents.DustRef;
import dust.mj02.dust.DustComponents.EntityResolver;
import dust.mj02.dust.DustComponents.RefKey;
import dust.mj02.dust.DustComponents.RefProcessor;
import dust.mj02.dust.knowledge.DustMetaComponents.DustMetaLinkDefTypeValues;
import dust.mj02.dust.knowledge.DustMetaComponents.DustMetaLinks;
import dust.utils.DustUtilsJava;

@SuppressWarnings("unchecked")
class DustDataRef implements DustRef {
    private final DustProcSession session;

    final DustDataEntity linkDef;
    final DustDataEntity source;
//    final DustDataEntity target;
    DustDataEntity target;
    final Object key;

    DustDataRef reverse;

    DustMetaLinkDefTypeValues lt;
    Object container;

    public DustDataRef(DustProcSession session, DustDataEntity linkDef, DustDataEntity source, DustDataEntity target, Object key, DustDataRef orig) {
        this.session = session;

        this.linkDef = linkDef;
        this.source = source;
        this.target = target;
        this.key = key;

        initContainer(orig);

        switch (lt) {
        case LinkDefArray:
            List<DustDataRef> l = (List<DustDataRef>) container;
            if (null == key) {
                l.add(this);
            } else {
                l.add((int) key, this);
            }
            break;
        case LinkDefSet:
            ((Set<DustDataRef>) container).add(this);
            break;
        case LinkDefMap:
            DustDataRef old = ((Map<Object, DustDataRef>) container).put(key, this);
            if (null != old) {
                session.refs.remove(old);
            }
            break;
        case LinkDefSingle:
            break;
        }
    }

    DustDataRef(DustProcSession ctx, DustEntityKey linkDef, DustDataEntity source, DustDataEntity target) {
        this.session = ctx;

        this.linkDef = (DustDataEntity) EntityResolver.getEntity(linkDef);
        this.source = source;
        this.target = target;
        this.reverse = null;
        this.key = null;
    }

    void initContainer(DustDataRef orig) {
        Object o = linkDef.get(EntityResolver.getEntity(DustMetaLinks.LinkDefType));
        DustDataRef refLDT;
        if (o instanceof DustDataRef) {
            refLDT = (DustDataRef) o;
        } else {
            refLDT = null;
        }
        lt = (null == refLDT) ? DustMetaLinkDefTypeValues.LinkDefSingle : EntityResolver.getKey(refLDT.target);

        if ((null == orig) || (null == orig.container)) {
            switch (lt) {
            case LinkDefArray:
                container = new ArrayList<DustDataRef>();
                break;
            case LinkDefSet:
                container = new HashSet<DustDataRef>();
                break;
            case LinkDefMap:
                container = new HashMap<Object, DustDataRef>();
                break;
            case LinkDefSingle:
                container = null;
                return;
            }

            if (null != orig) {
                if (DustMetaLinkDefTypeValues.LinkDefMap == lt) {
                    ((HashMap<Object, DustDataRef>) container).put(orig.key, orig);
                } else {
                    if (orig.target == target) {
                        session.refs.remove(orig);
                    } else {
                        ((Collection<DustDataRef>) container).add(orig);
                    }
                }
                orig.container = container;
                orig.lt = lt;
            }
        } else {
            container = orig.container;
        }
    }

    @Override
    public boolean contains(DustEntity entity) {
        switch (lt) {
        case LinkDefArray:
        case LinkDefSet:
            for (Object r : (Collection<?>) container) {
                if (((DustDataRef) r).target == entity) {
                    return true;
                }
                ;
            }
            break;
        case LinkDefMap:
            for (Object r : ((Map<Object, DustDataRef>) container).values()) {
                if (((DustDataRef) r).target == entity) {
                    return true;
                }
                ;
            }
            break;
        case LinkDefSingle:
            return target == entity;
        }

        return false;
    }

    @Override
    public void processAll(RefProcessor proc) {
        switch (lt) {
        case LinkDefArray:
        case LinkDefSet:
            for (Object r : ((Collection<?>) container).toArray()) {
                proc.processRef((DustDataRef) r);
            }
            break;
        case LinkDefMap:
            for (Object r : ((Map<Object, DustDataRef>) container).values().toArray()) {
                proc.processRef((DustDataRef) r);
            }
            break;
        case LinkDefSingle:
            proc.processRef(this);
            return;
        }
    }

    public DustDataRef select(DustEntity target) {
        switch (lt) {
        case LinkDefArray:
        case LinkDefSet:
            for (Object r : ((Collection<?>) container).toArray()) {
                DustDataRef ref = (DustDataRef) r;
                if (ref.target == target) {
                    return ref;
                }
                ;
            }
            return null;
        case LinkDefMap:
            for (Object r : ((Map<Object, DustDataRef>) container).values().toArray()) {
                DustDataRef ref = (DustDataRef) r;
                if (ref.target == target) {
                    return ref;
                }
                ;
            }
            return null;
        case LinkDefSingle:
            return this;
        }

        return null;
    }

    boolean removeByTarget(DustDataEntity target) {
        DustDataRef toDel = select(target);

        if (null != toDel) {
            toDel.remove(false, true);
            return true;
        }

        return false;
    }

    void remove(boolean all, boolean handleReverse) {
        boolean clear = true;

        switch (lt) {
        case LinkDefArray:
        case LinkDefSet:
            Collection<DustDataRef> coll = (Collection<DustDataRef>) container;
            if (all) {
                for (Object r : coll.toArray()) {
                    ((DustDataRef) r).remove(false, handleReverse);
                }
            } else {
                coll.remove(this);
                clear = coll.isEmpty();
                if (!clear && (this == source.get(linkDef))) {
                    source.put(linkDef, coll.iterator().next());
                }
            }
            break;
        case LinkDefMap:
            Map<Object, DustDataRef> map = (Map<Object, DustDataRef>) container;
            if (all) {
                for (DustDataRef r : map.values()) {
                    r.remove(false, handleReverse);
                }
            } else {
                map.remove(key);
                clear = map.isEmpty();
                if (!map.isEmpty() && (this == source.get(linkDef))) {
                    source.put(linkDef, map.entrySet().iterator().next());
                }
            }
            break;
        case LinkDefSingle:
            break;
        }

        if (clear) {
            source.content.remove(linkDef);
            // source.put(linkDef, null);
        }

        session.refs.remove(this);
        session.notifyListeners(DataCommand.removeRef, source, linkDef, null, this);

        if (handleReverse && (null != reverse)) {
            reverse.remove(false, false);
        }
    }

    @Override
    public <InfoType> InfoType get(RefKey ref) {
        switch (ref) {
        case key:
            return (InfoType) key;
        case linkDef:
            return (InfoType) linkDef;
        case source:
            return (InfoType) source;
        case target:
            return (InfoType) target;
        }
        return null;
    }
    
    @Override
    public int count() {
        if ( null == container ) {
            return ((null == lt) || (DustMetaLinkDefTypeValues.LinkDefSingle == lt)) ? 1 : 0;
        } else {
            return (DustMetaLinkDefTypeValues.LinkDefMap == lt) ? ((Map<?,?>)container).size() : ((Collection<?>)container).size();
        }
    }
    
    @Override
    public void hackUpdate(DustEntity entity) {
        if ( lt == DustMetaLinkDefTypeValues.LinkDefSingle ) {
            target = (DustDataEntity) entity;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = null;

        switch (lt) {
        case LinkDefSingle:
            sb = new StringBuilder(DustUtilsJava.toString(target));
            break;
        case LinkDefArray:
        case LinkDefSet:
            if (null != container) {
                for (DustDataRef sr : (Iterable<DustDataRef>) container) {
                    sb = DustUtilsJava.sbAppend(sb, ", ", false, sr.target);
                }
            }
            break;
        case LinkDefMap:
            if (null != container) {
                for (Entry<Object, DustDataRef> e : ((Map<Object, DustDataRef>) container).entrySet()) {
                    sb = DustUtilsJava.sbAppend(sb, ", ", false, e.getKey() + "=" + e.getValue().target);
                }
            }
            break;
        }

        return sb.insert(0, lt.sepStart).append(lt.sepEnd).toString();
    }
}
