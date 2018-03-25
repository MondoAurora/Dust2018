package dust.binding.reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dust.gen.dust.core.binding.DustCoreBindingComponents;
import dust.gen.dust.core.data.DustCoreDataServices;
import dust.gen.dust.core.exec.DustCoreExecServices;
import dust.gen.dust.core.meta.DustCoreMetaServices;
import dust.gen.dust.tools.generic.DustToolsGenericComponents;
import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.DustPubComponents;
import dust.pub.DustUtils;
import dust.pub.DustUtilsDev;
import dust.pub.boot.DustBootComponents;
import dust.pub.boot.DustBootComponents.DustConfig;
import dust.utils.DustUtilsFactory;

public class DustBindingManagerReflection implements DustBootComponents.DustBindingManager,
		DustBootComponents.DustConfigurable, DustPubComponents, DustToolsGenericComponents, DustCoreDataServices, DustCoreMetaServices, DustCoreExecServices {


	private Creator<Class<?>> cBinClass = new Creator<Class<?>>() {
		@Override
		public Class<?> create(Object... params) {
			DustEntity eSvc = (DustEntity) params[0];
			String id = Dust.getAttrValue(eSvc, DustToolsGenericComponents.DustAttributeToolsGenericIdentified.idLocal);
			DustUtilsDev.dump("Should create class for id", id);
			Map<DustEntity, Class<?>> resp = new HashMap<>();
			
			Dust.processRefs(new DustCoreExecVisitor() {
				@Override
				public DustConstCoreExecVisitorResponse dustDustCoreExecVisitorVisit(DustEntity entity) throws Exception {
					DustEntity refSvc = Dust.getRefEntity(entity, false, DustCoreBindingComponents.DustLinkCoreBindingLogicAssignment.Service, null);
					
					if ( refSvc == eSvc ) {
						String className = Dust.getAttrValue(entity, DustCoreBindingComponents.DustAttributeCoreBindingLogicAssignment.javaClass);
						resp.put(refSvc, Class.forName(className));
					}
					return null;
				}
			}, eSelf, DustCoreBindingComponents.DustLinkCoreBindingManager.LogicAssignments);

			return resp.get(eSvc);
		}
	};

	
	class BinFactory extends DustUtilsFactory<DustEntity, Object> {
		
		DustUtilsFactory<DustEntity, Object> factSvcImpl = new DustUtilsFactory<DustEntity, Object>(false) {
			@Override
			protected Object create(DustEntity key, Object... hints) {
				Class<?> logic = DustUtils.getAttrValueSafe(key, DustAttributeCoreMetaService.boundClass, cBinClass, key);
				return DustUtils.instantiate(logic);
			}
		};
		
		public BinFactory() {
			super(false);
		}

		@Override
		protected Object create(DustEntity key, Object... hints) {
			DustEntity eSvc = Dust.getRefEntity(key, false, DustToolsGenericComponents.DustLinkToolsGenericConnected.Owner, null);
			return factSvcImpl.get(eSvc);
//			String svcId = Dust.getAttrValue(key, DustUtilsComponents.DustAttributeUtilsIdentified.idCombined);
//			try {
//				DustService svc = (DustService) DustUtils.fromEnumId(svcId);
//				return factSvcImpl.get(svc);
//			} catch (ClassNotFoundException e) {
//				DustException.wrapException(e);
//			}
//
//			return null;
		}
	};

	private Creator<BinFactory> cBinFact = new Creator<DustBindingManagerReflection.BinFactory>() {
		@Override
		public BinFactory create(Object... params) {
			return new BinFactory();
		}
	};

	DustUtilsFactory<DustEntity, Method> factMethods = new DustUtilsFactory<DustEntity, Method>(false) {
		@Override
		protected Method create(DustEntity key, Object... hints) {
			String name = Dust.getAttrValue(key, DustAttributeToolsGenericIdentified.idLocal);
			
			// ugly name magic for now...			
			name = name.replace("Command", "").replace(":", "");
			name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
			try {
				return hints[0].getClass().getMethod(name);
			} catch (Exception e) {
				DustException.wrapException(e, DustCoreBindingComponents.DustStatusCoreBinding.ErrorMethodAccess, name);
				return null;
			}
		}
	};

	DustUtilsFactory<DustEntity, Class<?>> factClasses = new DustUtilsFactory<DustEntity, Class<?>>(false) {
		@Override
		protected Class<?> create(DustEntity key, Object... hints) {
			return null;
		}
	};

	private DustEntity eSelf;

	@Override
	public void init(DustConfig config) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEntity(DustEntity entity) {
		eSelf = entity;
	}

	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage(DustEntity msg) throws Exception {
		DustEntity target = Dust.getRefEntity(msg, false, DustLinkCoreExecMessage.Target, null);
		DustEntity cmd = Dust.getRefEntity(msg, false, DustLinkCoreExecMessage.Command, null);

		BinFactory factImpl = DustUtils.getAttrValueSafe(target, DustAttributeCoreDataEntity.svcImpl, cBinFact);

		Object binOb = factImpl.get(cmd);
		factMethods.get(cmd, binOb).invoke(binOb);
	}

}
