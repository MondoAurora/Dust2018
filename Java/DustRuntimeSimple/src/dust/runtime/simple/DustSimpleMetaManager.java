package dust.runtime.simple;

import dust.pub.DustRuntimeComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleMetaManager implements DustSimpleRuntimeComponents, DustRuntimeComponents.DustMetaManager, DustRuntimeComponents.DustShutdownAware {
	
	private DustUtilsFactory<String, SimpleType> factType = new DustUtilsFactory<String, SimpleType>(true) {
		@Override
		protected SimpleType create(String key, Object... hints) {
			return new SimpleType(key);
		}
	};
	
	SimpleType getType(String idType) {
		return factType.get(idType);
	}


	@Override
	public void init(DustConfig config) throws Exception {
	}

	@Override
	public void shutdown() throws Exception {
		factType.clear();
	}

	@Override
	public SimpleField getAttrDef(String idType, String id) {
		return getType(idType).get(id);
	}

	@Override
	public DustLinkDef getLinkDef(String idType, String id) {
		return null;
	}

	@Override
	public DustMsgDef getMsgDef(String idService, String id) {
		return null;
	}
}
