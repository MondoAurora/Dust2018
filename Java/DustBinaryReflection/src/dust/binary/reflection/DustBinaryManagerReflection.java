package dust.binary.reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dust.gen.dust.base.DustBaseServices;
import dust.gen.dust.binary.DustBinaryComponents;
import dust.gen.dust.utils.DustUtilsComponents;
import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.DustPubComponents;
import dust.pub.DustUtils;
import dust.pub.DustUtilsDev;
import dust.pub.boot.DustBootComponents;
import dust.pub.boot.DustBootComponents.DustConfig;
import dust.utils.DustUtilsFactory;

public class DustBinaryManagerReflection implements DustBootComponents.DustBinaryManager,
		DustBootComponents.DustConfigurable, DustPubComponents, DustUtilsComponents, DustBaseServices {


	private Creator<Class<?>> cBinClass = new Creator<Class<?>>() {
		@Override
		public Class<?> create(Object... params) {
			DustEntity eSvc = (DustEntity) params[0];
			String id = Dust.getAttrValue(eSvc, DustUtilsComponents.DustAttributeUtilsIdentified.idLocal);
			DustUtilsDev.dump("Should create class for id", id);
			Map<DustEntity, Class<?>> resp = new HashMap<>();
			
			Dust.processRefs(new DustBaseVisitor() {
				@Override
				public DustBaseVisitorResponse dustDustBaseVisitorVisit(DustEntity entity) throws Exception {
					DustEntity refSvc = Dust.getRefEntity(entity, false, DustBinaryComponents.DustLinkBinaryLogicAssignment.Service, null);
					
					if ( refSvc == eSvc ) {
						String className = Dust.getAttrValue(entity, DustBinaryComponents.DustAttributeBinaryLogicAssignment.javaClass);
						resp.put(refSvc, Class.forName(className));
					}
					return null;
				}
			}, eSelf, DustBinaryComponents.DustLinkBinaryManager.LogicAssignments);

			return resp.get(eSvc);
		}
	};

	
	class BinFactory extends DustUtilsFactory<DustEntity, Object> {
		
		DustUtilsFactory<DustEntity, Object> factSvcImpl = new DustUtilsFactory<DustEntity, Object>(false) {
			@Override
			protected Object create(DustEntity key, Object... hints) {
				Class<?> logic = DustUtils.getAttrValueSafe(key, DustAttributeBaseService.binClass, cBinClass, key);
				return DustUtils.instantiate(logic);
			}
		};
		
		public BinFactory() {
			super(false);
		}

		@Override
		protected Object create(DustEntity key, Object... hints) {
			DustEntity eSvc = Dust.getRefEntity(key, false, DustUtilsComponents.DustLinkUtilsOwned.Owner, null);
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

	private Creator<BinFactory> cBinFact = new Creator<DustBinaryManagerReflection.BinFactory>() {
		@Override
		public BinFactory create(Object... params) {
			return new BinFactory();
		}
	};

	DustUtilsFactory<DustEntity, Method> factMethods = new DustUtilsFactory<DustEntity, Method>(false) {
		@Override
		protected Method create(DustEntity key, Object... hints) {
			String name = Dust.getAttrValue(key, DustAttributeUtilsIdentified.idLocal);
			
			// ugly name magic for now...			
			name = name.replace("DustCommand", "").replace(":", "");
			name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
			try {
				return hints[0].getClass().getMethod(name);
			} catch (Exception e) {
				DustException.wrapException(e);
				return null;
			}
		}
	};

	DustUtilsFactory<DustEntity, Class> factClasses = new DustUtilsFactory<DustEntity, Class>(false) {
		@Override
		protected Class create(DustEntity key, Object... hints) {
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
		DustEntity target = Dust.getRefEntity(msg, false, DustLinkBaseMessage.Target, null);
		DustEntity cmd = Dust.getRefEntity(msg, false, DustLinkBaseMessage.Command, null);

		BinFactory factImpl = DustUtils.getAttrValueSafe(target, DustAttributeBaseEntity.svcImpl, cBinFact);

		Object binOb = factImpl.get(cmd);
		factMethods.get(cmd, binOb).invoke(binOb);

		DustUtilsDev.dump("In runtime send message :-)");
	}

}
