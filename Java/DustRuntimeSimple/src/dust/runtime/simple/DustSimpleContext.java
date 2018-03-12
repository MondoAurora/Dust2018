package dust.runtime.simple;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import dust.gen.base.DustBaseServices;
import dust.utils.DustUtilsFactory;

public class DustSimpleContext implements DustSimpleRuntimeComponents, DustBaseServices, DustBaseServices.DustBaseSource {

	private DustSimpleMetaManager idMgr;
	
	private Set<DustBaseSource> parentSources = new HashSet<>();
	
	private EnumMap<DustContext, SimpleEntity> rootEntities = new EnumMap<>(DustContext.class);
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

	public DustEntity getEntity(DustContext root, DustAttrDef... path) {
		SimpleEntity e = rootEntities.get(root);
		
		for ( DustAttrDef f : path ) {
			e = e.getFieldValue(f);
			if ( null == e ) {
				return null;
			}
		}
		
		return e;
	}

	@Override
	public boolean dustSourceIsTypeSupported(DustEntity eType) {
		return true;
	}

	@Override
	public SimpleEntity dustSourceGet(DustEntity eType, String srcId, String revId) throws Exception {
		return factGlobalEntities.get(idMgr.getType(eType)).get(srcId, revId);
	}

	@Override
	public void dustSourceFind(DustEntity eType, DustEntity expression, DustEntity processor) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dustSourceDestruct(DustEntity entity) throws Exception {
		((SimpleEntity)entity).setState(DustEntityState.esDestructed);
	}
	

}
