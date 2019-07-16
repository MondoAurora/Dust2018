package dust.mj02.dust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.knowledge.DustDataComponents;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.mj02.dust.text.DustTextComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustUtils implements DustComponents, DustKernelComponents {

    public static <RetVal> RetVal getMsgVal(DustEntityKey key, boolean resolveRef) {
        return getCtxVal(ContextRef.msg, key, resolveRef);
    }

    public static <RetVal> RetVal getCtxVal(ContextRef ctxRef, DustEntityKey key, boolean resolveRef) {
        Object ret = Dust.accessEntity(DataCommand.getValue, ctxRef, EntityResolver.getEntity(key), null, null);
        if (resolveRef && (ret instanceof DustRef)) {
            ret = ((DustRef) ret).get(RefKey.target);
        }
        return (RetVal) ret;
    }

    public static <RetVal> RetVal optResolve(Object ob) {
        return (RetVal) ((ob instanceof DustEntityKey) ? EntityResolver.getEntity(ob) : ob);
    }

    public static DustEntity toEntity(Object ob) {
        if ((null == ob) || (ob instanceof DustEntity)) {
            return (DustEntity) ob;
        } else if (ob instanceof DustRef) {
            return ((DustRef) ob).get(RefKey.target);
        } else {
            return (DustEntity) ((ob instanceof DustEntityKey) ? EntityResolver.getEntity(ob) : ob);
        }
    }

    public static <RetVal> RetVal getByPath(Object ob, Object... path) {
        for (Object key : path) {
            if (ob instanceof DustRef) {
                Object r = ((DustRef) ob).getByKey(key);
                if (null != r) {
                    ob = r;
                    continue;
                }
            }

            DustEntity ee = toEntity(ob);
            ob = DustUtils.accessEntity(DataCommand.getValue, ee, key);

            if (null == ob) {
                return null;
            }
        }

        if (ob instanceof DustRef) {
            ob = toEntity(ob);
        }

        return (RetVal) ob;
    }

    public static boolean hasRef(DustEntity entity, DustEntityKey key, Object val) {
        DustEntity eLink = EntityResolver.getEntity(key);
        DustRef ref = Dust.accessEntity(DataCommand.getValue, entity, eLink, null, null);
        return (null == ref) ? false : ref.contains(optResolve(val));
    }

    public static boolean tag(DustEntity entity, TagCommand tcmd, Object tag) {
        DustEntity eLinkTags = EntityResolver.getEntity(DustDataComponents.DustDataLinks.EntityTags);
        DustRef ref = Dust.accessEntity(DataCommand.getValue, entity, eLinkTags, null, null);
        boolean set = (null == ref) ? false : ref.contains(optResolve(tag));

        switch (tcmd) {
        case clear:
            if (set) {
                Dust.accessEntity(DataCommand.removeRef, entity, eLinkTags, optResolve(tag), null);
            }
            return set;
        case set:
            if (!set) {
                Dust.accessEntity(DataCommand.setRef, entity, eLinkTags, optResolve(tag), null);
            }
            return !set;
        case test:
            return set;
        }

        return set;
    }

    public static <RetVal> RetVal accessEntity(DataCommand cmd, Object... parameters) {
        DustEntity e = (parameters.length > 0) ? optResolve(parameters[0]) : null;
        DustEntity key = (parameters.length > 1) ? optResolve(parameters[1]) : null;
        Object val = (parameters.length > 2) ? optResolve(parameters[2]) : null;
        Object collId = (parameters.length > 3) ? parameters[3] : null;

        Object ret = Dust.accessEntity(cmd, e, key, val, collId);

        return (RetVal) ret;
    }

    public static boolean isTrue(Object entity, Object att) {
        return Boolean.TRUE.equals(accessEntity(DataCommand.getValue, entity, att));
    }

    public static <RetType> RetType getSafe(Object entity, Object att, RetType defVal) {
        RetType val = accessEntity(DataCommand.getValue, entity, att);
        return (null == val) ? defVal : val;
    }

    public static int getInt(Object entity, Object att, int defVal) {
        Object val = accessEntity(DataCommand.getValue, entity, att);

        if (val instanceof Number) {
            return ((Number) val).intValue();
        } else if (val instanceof String) {
            return DustUtilsJava.toIntSafe((String) val, defVal);
        }

        return defVal;
    }

    public static <RetVal> RetVal getBinary(Object entity, Object service) {
        Map bo = DustUtils.accessEntity(DataCommand.getValue, entity, DustDataAtts.EntityBinaries);
        if (null != bo) {
            DustEntity svc = optResolve(service);
            return (RetVal) bo.get(svc);
        }

        return null;
    }

    public static <RetVal> RetVal getColl(DustMetaLinkDefTypeValues ldt) {
        Object coll = null;

        if (null != ldt) {
            switch (ldt) {
            case LinkDefArray:
                coll = new ArrayList<String>();
                break;
            case LinkDefMap:
                coll = new HashMap<String, String>();
                break;
            case LinkDefSet:
                coll = new HashSet<String>();
                break;
            case LinkDefSingle:
                break;
            }
        }

        return (RetVal) coll;
    }

    public static void registerService(Class<?> implClass, boolean autoInit, DustEntityKey svc, DustEntityKey... implServices) {
        String cName = implClass.getName();
        DustEntity ba = Dust.getEntity("BinaryAssignment: " + cName);

        DustUtils.accessEntity(DataCommand.setValue, ba, DustProcAtts.BinaryObjectName, cName, null);
        DustUtils.accessEntity(DataCommand.setRef, ba, DustProcLinks.BinaryImplementedServices, svc, null);
        DustUtils.accessEntity(DataCommand.setRef, ContextRef.session, DustProcLinks.SessionBinaryAssignments, ba, null);

        if (autoInit) {
            DustUtils.accessEntity(DataCommand.setValue, svc, DustProcAtts.BinaryAutoInit, true);
        }

        for (DustEntityKey impl : implServices) {
            DustUtils.accessEntity(DataCommand.setRef, svc, DustGenericLinks.ConnectedExtends, impl);
        }
    }

    private static DustUtilsFactory<DustEntity, DustMetaLinkDefTypeValues> factLinkTypes = new DustUtilsFactory<DustEntity, DustMetaLinkDefTypeValues>(
            false) {
        @Override
        protected DustMetaLinkDefTypeValues create(DustEntity key, Object... hints) {
            if (EntityResolver.getEntity(DustMetaTypes.LinkDef) != DustUtils.getByPath(key, DustDataLinks.EntityPrimaryType)) {
                return null;
            }

            DustMetaLinkDefTypeValues ldt = EntityResolver.getKey(DustUtils.getByPath(key, DustMetaLinks.LinkDefType));
            return (null == ldt) ? DustMetaLinkDefTypeValues.LinkDefSingle : ldt;
        }
    };

    public static DustMetaLinkDefTypeValues getLinkType(DustRef ref) {
        return getLinkType((DustEntity) ref.get(RefKey.linkDef));
    };

    public static DustMetaLinkDefTypeValues getLinkType(DustEntity eLinkType) {
        return factLinkTypes.get(eLinkType);
    }

    public static boolean isMultiLink(DustEntity eLinkType) {
        return DustMetaLinkDefTypeValues.LinkDefSingle != factLinkTypes.get(eLinkType);
    }

    public static class RefPathResolver implements RefProcessor {
        Object item;

        public <RetType> RetType resolve(boolean resolveRef) {
            DustEntity root = getCtxVal(ContextRef.self, DustGenericComponents.DustGenericLinks.ContextAwareEntity, true);

            if (null == root) {
                root = getCtxVal(ContextRef.msg, DustGenericComponents.DustGenericLinks.ContextAwareEntity, true);
            }

            return resolve(root, resolveRef);
        }

        public <RetType> RetType resolve(Object root, boolean resolveRef) {
            return resolve(root, DustGenericComponents.DustGenericLinks.ReferencePath, resolveRef);
        }

        public <RetType> RetType resolve(Object root, Object keyPath, boolean resolveRef) {
            this.item = toEntity(root);

            accessEntity(DataCommand.processRef, ContextRef.self, keyPath, this);

            if (resolveRef) {
                item = toEntity(item);
            }

            return (RetType) item;
        }

        @Override
        public void processRef(DustRef ref) {
            if (null != item) {
                DustEntity key = ref.get(RefKey.target);
                if (item instanceof DustRef) {
                    DustEntity eInMap = ((DustRef) item).getByKey(key);
                    if (null != eInMap) {
                        item = eInMap;
                        return;
                    }
                }
                // else {
                DustEntity e = toEntity(item);
                item = accessEntity(DataCommand.getValue, e, key);
                // }
            }
        }
    }

    public static class TagWatcher {
        private Object[] flags;

        private DustUtilsFactory<DustEntity, Set<Object>> factFlags = new DustUtilsFactory<DustEntity, Set<Object>>(false) {
            @Override
            protected Set<Object> create(DustEntity key, Object... hints) {
                Set<Object> ret = new HashSet<>();

                for (Object f : flags) {
                    if (DustUtils.tag(key, TagCommand.test, f)) {
                        ret.add(f);
                    }
                }

                return ret;
            }
        };

        public TagWatcher(Object... flags) {
            super();
            this.flags = Arrays.copyOf(flags, flags.length);
        }

        public boolean hasFlag(DustEntity e, Object... fl) {
            Set<Object> ret = factFlags.get(e);

            for (Object f : fl) {
                if (ret.contains(f)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static class AttConverter {
        private static final Object NOTSET = new Object();
        private static final DustEntity E_VAR_VALUE = EntityResolver.getEntity(DustDataAtts.VariantValue);
        private static final DustEntity E_VAR_TYPE = EntityResolver.getEntity(DustDataLinks.VariantValueType);

        private final DustMetaAttDefTypeValues attType;

        private static DustUtilsFactory<DustEntity, AttConverter> attTypeInfo = new DustUtilsFactory<DustEntity, AttConverter>(false) {
            @Override
            protected AttConverter create(DustEntity key, Object... hints) {
                return new AttConverter(key);
            }
        };

        private AttConverter(DustEntity key) {
            DustEntity at = getByPath(key, DustMetaLinks.AttDefType);

            attType = (null == at) ? DustMetaAttDefTypeValues.AttDefIdentifier : EntityResolver.getKey(at);
        }

        // private String valToString(Object val) {
        // return valToString(attType, val);
        // }

        public static String valToString(DustMetaAttDefTypeValues attType, Object val) {
            if (val instanceof String) {
                return (String) val;
            }
            switch (attType) {
            case AttDefBool:
                return Boolean.toString((boolean) val);
            case AttDefDouble:
                return Double.toString((double) val);
            case AttDefIdentifier:
                return (String) val;
            case AttDefLong:
                return Long.toString((long) val);
            case AttDefRaw:
                return "{raw object " + val.getClass().getSimpleName() + val.hashCode() + "}";
            }
            return "";
        }

        // private Object stringToOb(String str) {
        // return stringToOb(attType, str);
        // }

        public static Object stringToOb(DustMetaAttDefTypeValues attType, String str) {
            if (DustUtilsJava.isEmpty(str)) {
                return NOTSET;
            }

            switch (attType) {
            case AttDefBool:
                return Boolean.parseBoolean(str);
            case AttDefDouble:
                return Double.parseDouble(str);
            case AttDefIdentifier:
                return str;
            case AttDefLong:
                return Long.parseLong(str);
            case AttDefRaw:
                return NOTSET;
            }

            return "";
        }

        public static DustMetaAttDefTypeValues getAttType(DustEntity att) {
            return attTypeInfo.get(att).attType;
        }

        public static Class<?> getAttClass(DustEntity att) {
            switch (attTypeInfo.get(att).attType) {
            case AttDefBool:
                return Boolean.class;
            case AttDefDouble:
                return Double.class;
            case AttDefIdentifier:
                return String.class;
            case AttDefLong:
                return Integer.class;
            case AttDefRaw:
                return String.class;
            }
            return String.class;
        }

        public static boolean isEditable(DustEntity att) {
            return attTypeInfo.get(att).attType != DustMetaAttDefTypeValues.AttDefRaw;
        }

        public static String getAttAsString(DustEntity e, DustEntity att) {
            Object val = accessEntity(DataCommand.getValue, e, att);

            DustMetaAttDefTypeValues attType = getAttTypeVal(e, att);

            return ((null == val) || DustUtilsJava.isEmpty(DustUtilsJava.toString(val))) ? "" : AttConverter.valToString(attType, val);
        }

        public static DustMetaAttDefTypeValues getAttTypeVal(DustEntity e, DustEntity att) {
            DustMetaAttDefTypeValues attType;

            if (E_VAR_VALUE == att) {
                DustRef rAT = accessEntity(DataCommand.getValue, e, E_VAR_TYPE);
                attType = (null == rAT) ? DustMetaAttDefTypeValues.AttDefIdentifier : EntityResolver.getKey(rAT.get(RefKey.target));
            } else {
                attType = attTypeInfo.get(att).attType;
            }

            return attType;
        }

        public static <RetType> RetType setAttFromString(DustEntity e, DustEntity att, String str) {
            DustMetaAttDefTypeValues attType = getAttTypeVal(e, att);

            // if ( E_VAR_VALUE == att ) {
            // DustEntity eAT = accessEntity(DataCommand.getValue, e, E_VAR_TYPE);
            // attType = (null == eAT) ? DustMetaAttDefTypeValues.AttDefIdentifier :
            // EntityResolver.getKey(eAT);
            // } else {
            // attType = attTypeInfo.get(att).attType;
            // }

            Object val = DustUtilsJava.isEmpty(str) ? NOTSET : AttConverter.stringToOb(attType, str);
            // Object val = DustUtilsJava.isEmpty(str) ? NOTSET :
            // attTypeInfo.get(att).stringToOb(str);
            return (RetType) accessEntity(DataCommand.setValue, e, att, (NOTSET == val) ? null : val);
        }
    }

    private static final DustUtilsFactory<DustEntity, DustEntity> FMT_PRIM_TYPE = new DustUtilsFactory<DustEntity, DustEntity>(false) {
        @Override
        protected DustEntity create(DustEntity key, Object... hints) {
            DustEntity fmtRoot = getByPath(key, DustTextComponents.DustTextLinks.TextRendererRoot);

            return fmtRoot;
        }
    };

    public static String formatEntity(DustEntity e) {
        DustEntity ePT = getByPath(e, DustDataComponents.DustDataLinks.EntityPrimaryType);

        String id = accessEntity(DataCommand.getValue, e, DustGenericAtts.IdentifiedIdLocal);
        String type = (null == ePT) ? "?" : (ePT == e) ? id : accessEntity(DataCommand.getValue, ePT, DustGenericAtts.IdentifiedIdLocal);
        String txt = type + ": " + id;

        DustEntity fmtRoot = FMT_PRIM_TYPE.get(ePT);

        if (null != fmtRoot) {
            try {
                DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, true);
                DustEntity eFmtMsg = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);
                DustUtils.accessEntity(DataCommand.setRef, eFmtMsg, DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);
                accessEntity(DataCommand.setRef, fmtRoot, DustGenericComponents.DustGenericLinks.ContextAwareEntity, e);

                accessEntity(DataCommand.tempSend, ePT, eFmtMsg);

                txt = DustUtils.accessEntity(DataCommand.getValue, eFmtMsg, DustDataAtts.MessageReturn);
                accessEntity(DataCommand.removeRef, fmtRoot, DustGenericComponents.DustGenericLinks.ContextAwareEntity);
            } catch (Throwable t) {
                DustUtilsDev.dump("temp swallow exception in formatting");
            } finally {
                DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, false);
            }
        }

        return txt;
    }

    public static abstract class LazyMsgContainer {
        private DustEntity msg = null;

        public DustEntity getMsg() {
            if (null == msg) {
                msg = createMsg();
            }
            return msg;
        }

        protected abstract DustEntity createMsg();
    }

}
