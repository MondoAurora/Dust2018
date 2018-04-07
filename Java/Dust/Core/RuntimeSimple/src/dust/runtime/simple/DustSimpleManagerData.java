package dust.runtime.simple;

import java.util.HashSet;
import java.util.Set;

import dust.gen.DustUtilsGen;
import dust.gen.knowledge.info.DustKnowledgeInfoServices;
import dust.pub.DustUtils;
import dust.utils.DustUtilsFactory;

public class DustSimpleManagerData implements DustSimpleRuntimeComponents, DustKnowledgeInfoServices,
		DustKnowledgeInfoServices.DustKnowledgeInfoSource {

	private DustSimpleManagerMeta meta;
	private Set<DustKnowledgeInfoSource> parentSources = new HashSet<>();

	private Set<SimpleEntity> allKnownEntities = new HashSet<>();
	DustUtilsFactory<DustType, DustUtilsFactory<String, SimpleEntity>> factGlobalEntities = new DustUtilsFactory<DustType, DustUtilsFactory<String, SimpleEntity>>(
			true) {
		@Override
		protected DustUtilsFactory<String, SimpleEntity> create(DustType typeKey, Object... hints) {
			return new DustUtilsFactory<String, SimpleEntity>(true) {
				@Override
				protected SimpleEntity create(String key, Object... hints) {
					SimpleType st = meta.getSimpleType(typeKey);
					SimpleEntity se = new SimpleEntity(DustSimpleManagerData.this, st);
					allKnownEntities.add(se);
					return se;
				}

				@Override
				protected void initNew(SimpleEntity item, String key, Object... hints) {
					// perhaps here come the external loading, but not for now and for meta
				}
			};
		}
	};

	public void setMeta(DustSimpleManagerMeta meta) {
		this.meta = meta;
		addParentSource(meta);
	}

	void addParentSource(DustKnowledgeInfoSource src) {
		parentSources.add(src);
	}

	@Override
	public boolean dustKnowledgeInfoSourceIsTypeSupported(DustType eType) {
		return true;
	}

	@Override
	public SimpleEntity dustKnowledgeInfoSourceGet(String idGlobal) throws Exception {
		SimpleEntity ret = null;

		if (DustUtils.isEmpty(idGlobal)) {
			ret = new SimpleEntity(this, null);
			allKnownEntities.add(ret);
		} else {
			DustType t = DustUtilsGen.getTypeFromId(idGlobal);
			if (meta.dustKnowledgeInfoSourceIsTypeSupported(t)) {
				ret = (SimpleEntity) meta.dustKnowledgeInfoSourceGet(idGlobal);
			} else {
				ret = factGlobalEntities.get(t).get(idGlobal);
			}
		}
		return ret;
	}

	@Override
	public void dustKnowledgeInfoSourceFind(DustType type, DustEntity expression, DustEntity processor)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void dustKnowledgeInfoSourceDestruct(DustEntity entity) throws Exception {
		((SimpleEntity) entity).setState(DustConstKnowledgeInfoEntityState.Destructed);
	}

}
