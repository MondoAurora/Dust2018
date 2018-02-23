package dust.runtime.simple;

import dust.pub.DustRuntimeComponents;
import dust.pub.DustUtilsDev;

public class DustSimpleIdManager implements DustSimpleRuntimeComponents, DustRuntimeComponents.DustIdManager {
	
	DustUtilsFactory<String, SimpleType> factType = new DustUtilsFactory<String, SimpleType>(true) {
		@Override
		protected SimpleType create(String key, Object... hints) {
			return new SimpleType(key);
		}
	};

	@Override
	public void init(DustConfig config) throws Exception {
		DustUtilsDev.dump("Initializing Identifier manager", this.getClass());

	}

	@Override
	public DustField getField(String idType, String idField) {
		return factType.get(idType).get(idField);
	}

}
