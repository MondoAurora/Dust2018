package dust.runtime.simple;

import dust.pub.DustRuntimeComponents;
import dust.pub.DustUtilsDev;
import dust.utils.DustUtilsFactory;

public class DustSimpleIdManager implements DustSimpleRuntimeComponents, DustRuntimeComponents.DustIdManager, DustRuntimeComponents.DustShutdownAware {
	
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
		DustUtilsDev.dump("Initializing Identifier manager", this.getClass());

	}

	@Override
	public SimpleField getField(String idType, String idField) {
		return getType(idType).get(idField);
	}

	@Override
	public void shutdown() throws Exception {
		DustUtilsDev.dump("Shut down Identifier manager");
		factType.clear();
	}
}
