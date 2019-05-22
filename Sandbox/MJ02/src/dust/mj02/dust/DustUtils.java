package dust.mj02.dust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.knowledge.DustDataComponents;
import dust.mj02.dust.knowledge.DustDataComponents.DustDataAtts;
import dust.mj02.dust.knowledge.DustDataComponents.DustDataLinks;
import dust.mj02.dust.knowledge.DustMetaComponents.DustMetaLinkDefTypeValues;
import dust.mj02.dust.knowledge.DustMetaComponents.DustMetaLinks;
import dust.mj02.dust.knowledge.DustMetaComponents.DustMetaTypes;
import dust.mj02.dust.knowledge.DustProcComponents.DustProcAtts;
import dust.mj02.dust.knowledge.DustProcComponents.DustProcLinks;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.mj02.dust.tools.DustGenericComponents.DustGenericLinks;
import dust.utils.DustUtilsFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustUtils implements DustComponents {

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
			DustEntity ee = toEntity(ob);
			ob = DustUtils.toEntity(DustUtils.accessEntity(DataCommand.getValue, ee, key));

			if (null == ob) {
				return null;
			}
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
			if ( set ) {
				Dust.accessEntity(DataCommand.removeRef, entity, eLinkTags, optResolve(tag), null);
			}
			return set;
		case set:
			if ( !set ) {
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

	public static void registerService(Class<?> implClass, boolean autoInit, DustEntityKey svc,
			DustEntityKey... implServices) {
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
            
            return resolve(root, resolveRef);
        }
        
        public <RetType> RetType resolve(Object root, boolean resolveRef) {
            return resolve(root, DustGenericComponents.DustGenericLinks.ReferencePath, resolveRef);
        }
        
        public <RetType> RetType resolve(Object root, Object keyPath, boolean resolveRef) {
            this.item = toEntity(root);
            
            accessEntity(DataCommand.processRef, ContextRef.self, keyPath, this);
            
            if ( resolveRef ) {
                item = toEntity(item);
            }
            
            return (RetType) item;
        }

        @Override
        public void processRef(DustRef ref) {
            if ( null != item ) {
                DustEntity e = toEntity(item);
                DustEntity key = ref.get(RefKey.target);
                
                item = accessEntity(DataCommand.getValue, e, key);
            }
        }
    }
    
    public static class TagWatcher {
	    private Object[] flags;
	    
	    private DustUtilsFactory<DustEntity, Set<Object>> factFlags = new DustUtilsFactory<DustEntity, Set<Object>>(false) {
	        @Override
	        protected Set<Object> create(DustEntity key, Object... hints) {
	            Set<Object> ret = new HashSet<>();
	            
	            for ( Object f : flags ) {
	                if (DustUtils.tag(key, TagCommand.test, f) ) {
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
	        
            for ( Object f : fl ) {
                if (ret.contains(f)) {
                    return true;
                }
            }

	        return false;
	    }
	    
	}
}
