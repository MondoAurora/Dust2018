package dust.runtime.simple;

import java.util.HashMap;
import java.util.Map;

import dust.gen.dust.meta.DustMetaServices;
import dust.gen.dust.meta.DustMetaServices.DustMetaTypeDescriptor;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleMetaManager implements DustSimpleRuntimeComponents, DustMetaServices.DustMetaManager, DustBootComponents.DustConfigurable, DustBootComponents.DustShutdownAware {
	
	private DustUtilsFactory<Enum<?>, SimpleType> factType = new DustUtilsFactory<Enum<?>, SimpleType>(true) {
		@Override
		protected SimpleType create(Enum<?> key, Object... hints) {
			return new SimpleType(key);
		}
	};
	
	private Map<DustBaseAttribute, SimpleAttribute> mapAttributes = new HashMap<>();
	private Map<DustBaseLink, SimpleLinkDef> mapLinkDefs = new HashMap<>();
	
	DustUtilsFactory<Enum<?>, SimpleEntity> constants = new DustUtilsFactory<Enum<?>, SimpleEntity>(false) {
		@Override
		protected SimpleEntity create(Enum<?> key, Object... hints) {
			// TODO Auto-generated method stub
			return null;
		}		
	};
	
	SimpleAttribute getAtt(DustBaseAttribute att) {
		return mapAttributes.get(att);
	}

	SimpleLinkDef getAtt(DustBaseLink link) {
		return mapLinkDefs.get(link);
	}
	
	SimpleEntity optResolveEntity(DustBaseEntity entity) {
		return (entity instanceof Enum) ? constants.get((Enum<?>) entity) : null;
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
		for ( Enum<?> t : types.getEnumConstants() )	{
			DustMetaTypeDescriptor md = (DustMetaTypeDescriptor) t;
			SimpleType st = factType.get(t, md);
			
			for ( Enum<?> a : md.getAttribEnum().getEnumConstants() )	{
				DustBaseAttribute att = (DustBaseAttribute) a;
				mapAttributes.put(att, st.get(att));
			}
		}
	}

}
