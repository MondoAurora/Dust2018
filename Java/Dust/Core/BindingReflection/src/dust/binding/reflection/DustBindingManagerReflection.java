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
			DustLinkRuntimeBindingManager.LogicAssignments.link().process(eSelf, new DustRefVisitor() {
				@Override
				public boolean dustRefVisit(DustEntity entity) throws Exception {
					DustEntity refSvc = DustLinkRuntimeBindingLogicAssignment.Service.link().get(entity, false, null);
					String className = DustAttributeRuntimeBindingLogicAssignment.javaClass.attribute().getValue(entity);
					put(refSvc, Class.forName(className));
					return true;
				}
			});

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
			DustLinkKnowledgeInfoEntity.Services.link().process(owner, new DustRefVisitor() {
				@Override
				public boolean dustRefVisit(DustEntity entity) throws Exception {
					Class<?> svcClass = factBindings.get(entity);
					if (null != svcClass) {
						Set<DustEntity> ext = new HashSet<>();
						DustUtils.loadRecursive(entity, DustLinkToolsGenericConnected.Extends.entity(), ext);
						Object logic = DustUtils.instantiate(svcClass);
						for (DustEntity svc : ext) {
							put(svc, logic);
						}
					}
					return true;
				}
			});

			return peek(key);
		}

		@Override
		public StringBuilder toStringBuilder(StringBuilder target) {
			boolean empty = true;
			for (Map.Entry<DustEntity, Object> e : content.entrySet()) {
				if (empty) {
					empty = false;
					target = DustUtilsJava.sbApend(target, "", true, "{");
				} else {
					target.append(", ");
				}
				String key = DustAttributeToolsGenericIdentified.idLocal.attribute().getValue(e.getKey());
				Object val = e.getValue();
				target = DustUtilsJava.sbApend(target, "", true, "\"", key, "\": \"", val.getClass().getName(), ":",
						val.hashCode(), "\"");
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
			return (null == params) ? m.invoke(target) : m.invoke(target, params);
		}
	}

	DustUtilsFactory<DustEntity, MethodInfo> factMethods = new DustUtilsFactory<DustEntity, MethodInfo>(false) {
		@Override
		protected MethodInfo create(DustEntity key, Object... hints) {
			String name = DustAttributeToolsGenericIdentified.idLocal.attribute().getValue(key);

			// ugly name magic for now...
			// name = name.replace("Command", "").replace(":", "");
			// name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

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
			DustException.wrapException(ex, DustConstRuntimeBinding.ErrorMethodAccess.entity(), name);
			return null;
		}
	};

	@Override
	public void sendMessage(DustEntity target, DustEntity msg) throws Exception {
		DustEntity cmd = DustLinkKnowledgeProcMessage.Command.link().get(msg, false, null);
		DustEntity eSvc = DustLinkToolsGenericConnected.Owner.link().get(cmd, false, null);

		BinFactory factImpl = DustUtils.getAttrValueSafe(target, DustAttributeKnowledgeInfoEntity.svcImpl.entity(),
				cBinFact, target);
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
	public void launch() throws Exception {
	}
	
	@Override
	public void shutdown() throws Exception {
	}

}
