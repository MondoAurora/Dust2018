package dust.runtime.simple;

import java.util.HashSet;
import java.util.Set;

import dust.gen.DustUtilsGen;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;
import dust.pub.Dust;
import dust.pub.DustUtils;
import dust.utils.DustUtilsFactory;

public class DustSimpleManagerData implements DustSimpleRuntimeComponents, DustKnowledgeInfoComponents,
		DustKnowledgeInfoComponents.DustKnowledgeInfoSource {

	// private DustSimpleManagerMeta meta;
	private Set<DustKnowledgeInfoSource> parentSources = new HashSet<>();
	private final SimpleType typeType;

	private Set<InfoEntity> allKnownEntities = new HashSet<>();
	private DustUtilsFactory<SimpleType, DustUtilsFactory<String, ? extends InfoEntity>> factGlobalEntities = new DustUtilsFactory<SimpleType, DustUtilsFactory<String, ? extends InfoEntity>>(
			false) {
		@Override
		protected DustUtilsFactory<String, InfoEntity> create(SimpleType typeKey, Object... hints) {
			return new DustUtilsFactory<String, InfoEntity>(true) {
				@Override
				protected InfoEntity create(String key, Object... hints) {
					InfoEntity se = new InfoEntityData(DustSimpleManagerData.this, typeKey);
					String[] s2 = key.split("\\.");
					se.setFieldValue(optResolveMeta(DustAttributeToolsGenericIdentified.idLocal), s2[(1 == s2.length) ? 0 : 1]);
					return se;
				}
			};
		}
	};

	abstract class MetaFactory<MetaParent extends InfoEntity, MetaType extends InfoEntity>
			extends DustUtilsFactory<String, MetaType> {
		protected DustUtilsFactory<String, MetaParent> factMetaParent;

		public MetaFactory() {
			super(true);
		}
		
		@Override
		protected MetaType create(String key, Object... hints) {
			String[] s2 = key.split("\\.");
			MetaParent mp = factMetaParent.get(s2[0]);
			return getItem(mp, s2[1]);
		}
		
		protected abstract MetaType getItem(MetaParent mp, String id);
	};

	public DustSimpleManagerData() {
		typeType = new SimpleType(this, "Knowledge:Meta:Type");
		final DustUtilsFactory<String, SimpleType> tf = new DustUtilsFactory<String, SimpleType>(true) {
			@Override
			protected SimpleType create(String key, Object... hints) {
				SimpleType se = new SimpleType(DustSimpleManagerData.this, key);
				return se;
			}
		};

		tf.put("Knowledge:Meta:Type", typeType);
		tf.put("", typeType);

		factGlobalEntities.put(typeType, tf);
		factGlobalEntities.put(null, tf);

		final DustUtilsFactory<String, SimpleService> sf = new DustUtilsFactory<String, SimpleService>(true) {
			@Override
			protected SimpleService create(String key, Object... hints) {
				return new SimpleService(DustSimpleManagerData.this, key);
			}
		};

		factGlobalEntities.put(tf.get("Knowledge:Meta:Service"), sf);

		addMetaFactory(tf, tf, "Knowledge:Meta:AttDef", new MetaFactory<SimpleType, SimpleAttDef>() {
			@Override
			protected SimpleAttDef getItem(SimpleType mp, String id) {
				return mp.getAttDef(id);
			}
		});
		addMetaFactory(tf, tf, "Knowledge:Meta:LinkDef", new MetaFactory<SimpleType, SimpleLinkDef>() {
			@Override
			protected SimpleLinkDef getItem(SimpleType mp, String id) {
				return mp.getLinkDef(id);
			}
		});
		addMetaFactory(tf, sf, "Knowledge:Meta:Command", new MetaFactory<SimpleService, SimpleCommand>() {
			@Override
			protected SimpleCommand getItem(SimpleService mp, String id) {
				SimpleCommand se = mp.getCommand(id);
				String cmdId = ("dust" + mp.id + id).replace(":", "").replace(".", "");
				se.setFieldValue(optResolveMeta(DustAttributeToolsGenericIdentified.idLocal), cmdId);
				Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, se, DustToolsGenericComponents.DustLinkToolsGenericConnected.Owner, mp);
				return se;
			}
		});
	}

	private <MetaParent extends InfoEntity> void addMetaFactory(DustUtilsFactory<String, SimpleType> tf, DustUtilsFactory<String, MetaParent> mf, String typeName, MetaFactory<MetaParent, ?> fact) {
		SimpleType mt = tf.get(typeName);
		factGlobalEntities.put(mt, fact);
		fact.factMetaParent = mf;
	}


	@SuppressWarnings("unchecked")
	<RetType> RetType optResolveMeta(Object entity) {
		if (entity instanceof InfoEntity) {
			return (RetType) entity;
		} else if (entity instanceof IdentifiableMeta) {
			IdentifiableMeta meta = (IdentifiableMeta) entity;
			String idType = DustUtilsGen.getMetaType(meta);
			SimpleType type = (SimpleType) factGlobalEntities.get(null).get(idType);
			String idStore = DustUtilsGen.metaToStoreId(meta);
			return (RetType) factGlobalEntities.get(type).get(idStore);
		} else {
			return (RetType) entity;
		}
	}

	void addParentSource(DustKnowledgeInfoSource src) {
		parentSources.add(src);
	}

	@Override
	public boolean dustKnowledgeInfoSourceIsTypeSupported(DustType eType) {
		return true;
	}

	@Override
	public InfoEntity dustKnowledgeInfoSourceGet(DustType type, String idStore) throws Exception {
		InfoEntity ret = null;

		if (DustUtils.isEmpty(idStore)) {
			ret = new InfoEntityData(this, null);
			allKnownEntities.add(ret);
		} else {
			SimpleType st = (null == type) ? typeType : optResolveMeta(type);
			ret = factGlobalEntities.get(st).get(idStore);
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
		((InfoEntity) entity).setState(DustConstKnowledgeInfoEntityState.Destructed);
	}

}
