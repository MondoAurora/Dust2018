package dust.runtime.simple;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import dust.gen.dust.base.DustBaseServices;
import dust.utils.DustUtilsFactory;

public class DustSimpleContext implements DustSimpleRuntimeComponents, DustBaseServices, DustBaseServices.DustBaseSource {

	private DustSimpleMetaManager idMgr;
	
	private Set<DustBaseSource> parentSources = new HashSet<>();
	
	private EnumMap<DustBaseContext, SimpleEntity> rootEntities = new EnumMap<>(DustBaseContext.class);
	private Set<SimpleEntity> allKnownEntities = new HashSet<>();
	private DustUtilsFactory<SimpleType, DustUtilsFactory<String, SimpleEntity>> factGlobalEntities = new DustUtilsFactory<SimpleType, DustUtilsFactory<String, SimpleEntity>>(true) {
		@Override
		protected DustUtilsFactory<String, SimpleEntity> create(SimpleType typeKey, Object... hints) {
			return new DustUtilsFactory<String, SimpleEntity>(true) {
				@Override
				protected SimpleEntity create(String key, Object... hints) {
					SimpleEntity se = new SimpleEntity(DustSimpleContext.this, typeKey);
					allKnownEntities.add(se);
					return se;
				}
				
				protected void initNew(SimpleEntity item, String key, Object... hints) {
					for ( DustBaseSource src : parentSources ) {
						if ( src.dustSourceIsTypeSupported(typeKey.id)) {
							// load the instance content
						}
					}
				};
			};
		}
	};
	
	DustSimpleContext(DustSimpleMetaManager idMgr) {
		this.idMgr = idMgr;
	}

	public DustBaseEntity getEntity(DustBaseContext root, DustBaseAttributeDef... path) {
		SimpleEntity e = rootEntities.get(root);
		
		for ( DustBaseAttributeDef f : path ) {
			e = e.getFieldValue(f);
			if ( null == e ) {
				return null;
			}
		}
		
		return e;
	}

	@Override
	public boolean dustSourceIsTypeSupported(DustBaseEntity eType) {
		return true;
	}

	@Override
	public SimpleEntity dustSourceGet(DustBaseEntity eType, String srcId, String revId) throws Exception {
		return factGlobalEntities.get(idMgr.getType(eType)).get(srcId, revId);
	}

	@Override
	public void dustSourceFind(DustBaseEntity eType, DustBaseEntity expression, DustBaseEntity processor) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dustSourceDestruct(DustBaseEntity entity) throws Exception {
		((SimpleEntity)entity).setState(DustEntityState.esDestructed);
	}
	

}
