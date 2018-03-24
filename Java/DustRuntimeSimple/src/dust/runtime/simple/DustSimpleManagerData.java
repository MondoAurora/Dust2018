package dust.runtime.simple;

import java.util.HashSet;
import java.util.Set;

import dust.gen.dust.base.DustBaseServices;
import dust.pub.DustUtils;
import dust.utils.DustUtilsFactory;

public class DustSimpleManagerData implements DustSimpleRuntimeComponents, DustBaseServices, DustBaseServices.DustBaseSource {

	private Set<DustBaseSource> parentSources = new HashSet<>();
	
	private Set<SimpleEntity> allKnownEntities = new HashSet<>();
	DustUtilsFactory<SimpleType, DustUtilsFactory<String, SimpleEntity>> factGlobalEntities = new DustUtilsFactory<SimpleType, DustUtilsFactory<String, SimpleEntity>>(true) {
		@Override
		protected DustUtilsFactory<String, SimpleEntity> create(SimpleType typeKey, Object... hints) {
			return new DustUtilsFactory<String, SimpleEntity>(true) {
				@Override
				protected SimpleEntity create(String key, Object... hints) {
					SimpleEntity se = new SimpleEntity(DustSimpleManagerData.this, typeKey);
					allKnownEntities.add(se);
					return se;
				}
				
				protected void initNew(SimpleEntity item, String key, Object... hints) {
					for ( DustBaseSource src : parentSources ) {
						if ( src.dustSourceIsTypeSupported(typeKey.getEntity())) {
							// load the instance content
						}
					}
				};
			};
		}
	};
	
	@Override
	public boolean dustSourceIsTypeSupported(DustEntity eType) {
		return true;
	}

	@Override
	public SimpleEntity dustSourceGet(DustType type, String srcId, String revId) throws Exception {
		SimpleEntity ret = null;
		if ( (null == type) || DustUtils.isEmpty(srcId) ) {
			ret = new SimpleEntity(this, null);
			allKnownEntities.add(ret);
		} else {
//			ret = factGlobalEntities.get(mgrMeta.getType(type)).get(srcId, revId);
		}
		return ret;
	}

	@Override
	public void dustSourceFind(DustType type, DustEntity expression, DustEntity processor) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dustSourceDestruct(DustEntity entity) throws Exception {
		((SimpleEntity)entity).setState(DustEntityState.Destructed);
	}
	

}
