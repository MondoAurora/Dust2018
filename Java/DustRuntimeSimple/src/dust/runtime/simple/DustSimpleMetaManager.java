package dust.runtime.simple;

import dust.pub.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleMetaManager implements DustSimpleRuntimeComponents, DustBootComponents.DustMetaManager, DustBootComponents.DustShutdownAware {
	
	private DustUtilsFactory<DustBaseEntity, SimpleType> factType = new DustUtilsFactory<DustBaseEntity, SimpleType>(true) {
		@Override
		protected SimpleType create(DustBaseEntity key, Object... hints) {
			return new SimpleType(key);
		}
	};
	
	SimpleType getType(DustBaseEntity eType) {
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

}
