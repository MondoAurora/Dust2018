package dust.runtime.simple;

import dust.pub.DustRuntimeComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleMetaManager implements DustSimpleRuntimeComponents, DustRuntimeComponents.DustMetaManager, DustRuntimeComponents.DustShutdownAware {
	
	private DustUtilsFactory<DustEntity, SimpleType> factType = new DustUtilsFactory<DustEntity, SimpleType>(true) {
		@Override
		protected SimpleType create(DustEntity key, Object... hints) {
			return new SimpleType(key);
		}
	};
	
	SimpleType getType(DustEntity eType) {
		return factType.get(eType);
	}


	@Override
	public void init(DustConfig config) throws Exception {
	}

	@Override
	public void shutdown() throws Exception {
		factType.clear();
	}
	
	@Override
	public void registerUnit(Class<? extends Enum<?>> types, Class<? extends Enum<?>> services) {
		// TODO Auto-generated method stub		
	}

//	@Override
//	public SimpleField getAttrDef(DustEntity eType, String id) {
//		return getType(eType).get(id);
//	}
//
//	@Override
//	public DustLinkDef getLinkDef(DustEntity eType, String id) {
//		return null;
//	}
//
//	@Override
//	public DustMsgDef getMsgDef(DustEntity eService, String id) {
//		return null;
//	}
}
