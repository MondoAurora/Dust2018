package dust.binding.reflection;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.runtime.binding.DustRuntimeBindingComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;
import dust.pub.Dust;
import dust.pub.DustComponents;
import dust.pub.DustException;
import dust.pub.DustUtils;
import dust.pub.boot.DustBootComponents;
import dust.pub.boot.DustBootComponents.DustConfig;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustBindingManagerReflection implements DustBootComponents.DustBindingManager,
		DustBootComponents.DustConfigurable, DustComponents, DustToolsGenericComponents, DustKnowledgeInfoComponents,
		DustKnowledgeMetaComponents, DustKnowledgeProcComponents, DustRuntimeBindingComponents {

	DustEntity eSelf;

	DustUtilsFactory<DustEntity, Class<?>> factBindings = new DustUtilsFactory<DustEntity, Class<?>>(false) {
		@Override
		protected Class<?> create(DustEntity key, Object... hints) {
			Dust.processRefs(new DustKnowledgeProcVisitor() {
				@Override
				public DustConstKnowledgeProcVisitorResponse dustKnowledgeProcVisitorVisit(DustEntity entity)
						throws Exception {
					DustEntity refSvc = Dust.getRefEntity(entity, false, DustLinkRuntimeBindingLogicAssignment.Service,
							null);
					String className = Dust.getAttrValue(entity, DustAttributeRuntimeBindingLogicAssignment.javaClass);
					put(refSvc, Class.forName(className));
					return null;
				}
			}, eSelf, DustLinkRuntimeBindingManager.LogicAssignments);

			return peek(key);
		}
	};

	class BinFactory extends DustUtilsFactory<DustEntity, Object> {
		DustEntity owner;

		public BinFactory(DustEntity owner) {
			super(false);
			this.owner = owner;
		}

		@Override
		protected Object create(DustEntity key, Object... hints) {
			Dust.processRefs(new DustKnowledgeProcVisitor() {
				@Override
				public DustConstKnowledgeProcVisitorResponse dustKnowledgeProcVisitorVisit(DustEntity entity)
						throws Exception {
					Class<?> svcClass = factBindings.get(entity);
					if (null != svcClass) {
						Set<DustEntity> ext = new HashSet<>();
						DustUtils.loadRecursive(entity,
								DustToolsGenericComponents.DustLinkToolsGenericConnected.Extends, ext);
						Object logic = DustUtils.instantiate(svcClass);
						for (DustEntity svc : ext) {
							put(svc, logic);
						}
					}
					return null;
				}
			}, owner, DustLinkKnowledgeInfoEntity.Services);

			return peek(key);
		}
		
		@Override
		public StringBuilder toStringBuilder(StringBuilder target) {
			boolean empty = true;
			for ( Map.Entry<DustEntity, Object> e : content.entrySet() ) {
				if ( empty ) {
					empty = false;
					target = DustUtilsJava.sbApend(target, "", true, "{");
				} else {
					target.append(", ");
				}
				String key = Dust.getAttrValue(e.getKey(), DustAttributeToolsGenericIdentified.idLocal);
				Object val = e.getValue();
				target = DustUtilsJava.sbApend(target, "", true, "\"", key, "\": \"", val.getClass().getName(), ":", val.hashCode(), "\"");
			}
			
			return empty ? DustUtilsJava.sbApend(target, "", true, "{}") : target.append(" }");
		}
	};

	private Creator<BinFactory> cBinFact = new Creator<DustBindingManagerReflection.BinFactory>() {
		@Override
		public BinFactory create(Object... params) {
			return new BinFactory((DustEntity) params[0]);
		}
	};

	class MethodInfo {
		Method m;
		Object[] params;

		MethodInfo(Method m) {
			this.m = m;
			int pl = m.getParameterTypes().length;
			params = (0 == pl) ? null : new Object[pl];
		}

		Object invoke(Object target) throws Exception {
			return ( null == params ) ? m.invoke(target) : m.invoke(target, params);
		}
	}

	DustUtilsFactory<DustEntity, MethodInfo> factMethods = new DustUtilsFactory<DustEntity, MethodInfo>(false) {
		@Override
		protected MethodInfo create(DustEntity key, Object... hints) {
			String name = Dust.getAttrValue(key, DustAttributeToolsGenericIdentified.idLocal);

			// ugly name magic for now...
//			name = name.replace("Command", "").replace(":", "");
//			name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

			Throwable ex = null;
			try {
				for (Method m : hints[0].getClass().getMethods()) {
					if (name.equals(m.getName())) {
						return new MethodInfo(m);
					}
				}
			} catch (Throwable e) {
				ex = e;
			}
			DustException.wrapException(ex, DustStatusRuntimeBinding.ErrorMethodAccess, name);
			return null;
		}
	};

	@Override
	public void sendMessage(DustEntity target, DustEntity msg) throws Exception {
		DustEntity cmd = Dust.getRefEntity(msg, false, DustLinkKnowledgeProcMessage.Command, null);
		DustEntity eSvc = Dust.getRefEntity(cmd, false,	DustToolsGenericComponents.DustLinkToolsGenericConnected.Owner, null);

		BinFactory factImpl = DustUtils.getAttrValueSafe(target, DustAttributeKnowledgeInfoEntity.svcImpl, cBinFact,
				target);
		Object binOb = factImpl.get(eSvc);
		factMethods.get(cmd, binOb).invoke(binOb);
	}

	@Override
	public void setEntity(DustEntity entity) {
		eSelf = entity;
	}

	@Override
	public void init(DustConfig config) throws Exception {
	}

	@Override
	public void shutdown() throws Exception {
	}

}
