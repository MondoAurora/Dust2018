package dust.mj02.dust.knowledge;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dust.mj02.dust.DustComponents.DataCommand;
import dust.mj02.dust.DustComponents.DustEntity;
import dust.mj02.dust.DustComponents.DustEntityKey;
import dust.mj02.dust.DustComponents.EntityResolver;
import dust.mj02.dust.knowledge.DustDataComponents.DustDataLinks;
import dust.mj02.dust.knowledge.DustMetaComponents.DustMetaLinks;
import dust.mj02.dust.tools.DustGenericComponents.DustGenericAtts;
import dust.utils.DustUtilsFactory;

@SuppressWarnings("unchecked")
class DustDataEntity implements DustEntity {
    private final DustProcSession session;

    Map<DustEntity, Object> content = new HashMap<>();
    DustEntity ePT;
    DustUtilsFactory<DustDataEntity, Method> factMethods;
    boolean justCreated = true;
    boolean internal;

    public DustDataEntity(DustProcSession session, boolean internal) {
        this.internal = internal;
        this.session = session;

        if (!internal) {
            session.allEntities.add(this);
        }
    }

    public <RetType> RetType put(DustEntity key, Object value) {
        RetType orig = (RetType) content.put(key, value);

        if (!internal) {
            if (EntityResolver.getEntity(DustDataLinks.EntityPrimaryType) == key) {
                ePT = ((DustDataRef) value).target;
                session.ctxAccessEntity(DataCommand.setRef, this, EntityResolver.getEntity(DustDataLinks.EntityModels), ePT, null);
            }

            if (null == orig) {
                DustDataEntity keyModel = ((DustDataEntity) key).getFirstRef(DustMetaLinks.LinkDefParent, DustMetaLinks.AttDefParent);// ,
                                                                                                                                      // DustGenericLinks.ConnectedOwner);
                if (null != keyModel) {
                    session.ctxAccessEntity(DataCommand.setRef, this, EntityResolver.getEntity(DustDataLinks.EntityModels), keyModel, null);
                }
            }
        }

        return orig;
    }

    public <RetType> RetType get(DustEntity key) {
        return (RetType) content.get(key);
    }

    public <RetType> RetType get(DustEntityKey key) {
        return (RetType) content.get(EntityResolver.getEntity(key));
    }

    public DustDataEntity getSingleRef(DustEntityKey key) {
        DustDataRef r = get(EntityResolver.getEntity(key));
        return (null == r) ? null : r.target;
    }

    public DustDataEntity getSingleRefByPath(DustEntityKey... keys) {
        DustDataEntity e = this;
        for (DustEntityKey key : keys) {
            DustDataRef r = e.get(EntityResolver.getEntity(key));
            if (null == r) {
                return null;
            } else {
                e = r.target;
            }
        }
        return e;
    }

    public DustDataEntity getFirstRef(DustEntityKey... keys) {
        DustDataEntity ret = null;
        for (DustEntityKey k : keys) {
            ret = getSingleRef(k);
            if (null != ret) {
                break;
            }
        }
        return ret;
    }

    public <RetType> RetType put(DustEntityKey key, Object value) {
        return (RetType) put(EntityResolver.getEntity(key), value);
    }

    @Override
    public String toString() {
        String id = get(EntityResolver.getEntity(DustGenericAtts.IdentifiedIdLocal));

        String type = (null == ePT) ? "?"
                : (ePT == this) ? id : ((DustDataEntity) ePT).get(EntityResolver.getEntity(DustGenericAtts.IdentifiedIdLocal));
        return type + ": " + id;
    }

    public void putLocalRef(DustEntityKey link, DustEntityKey target) {
        put(link, new DustDataRef(session, link, this, (DustDataEntity) EntityResolver.getEntity(target)));
    }

    public void putLocalRef(DustEntityKey link, DustDataEntity target) {
        put(link, new DustDataRef(session, link, this, target));
    }
}