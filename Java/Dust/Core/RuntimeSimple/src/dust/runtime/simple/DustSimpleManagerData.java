package dust.runtime.simple;

import java.util.HashSet;
import java.util.Set;

import dust.gen.DustUtilsGen;
import dust.gen.knowledge.info.DustKnowledgeInfoServices;
import dust.gen.tools.generic.DustToolsGenericComponents;
import dust.pub.Dust;
import dust.pub.DustUtils;
import dust.utils.DustUtilsFactory;

public class DustSimpleManagerData implements DustSimpleRuntimeComponents, DustKnowledgeInfoServices,
		DustKnowledgeInfoServices.DustKnowledgeInfoSource {

	// private DustSimpleManagerMeta meta;
	private Set<DustKnowledgeInfoSource> parentSources = new HashSet<>();

	private Set<InfoEntity> allKnownEntities = new HashSet<>();
	DustUtilsFactory<String, InfoEntity> factGlobalEntities = new DustUtilsFactory<String, InfoEntity>(true) {
		@Override
		protected InfoEntity create(String key, Object... hints) {
			String[] ss = key.split("\\|");
			InfoEntity se;

			String[] s2;
			SimpleType pt;
			SimpleService ps;

			switch (ss[0]) {
			case "Knowledge:Meta:Type":
				se = new SimpleType(DustSimpleManagerData.this, ss[1]);
				break;
			case "Knowledge:Meta:Service":
				se = new SimpleService(DustSimpleManagerData.this, ss[1]);
				break;
			case "Knowledge:Meta:LinkDef":
				s2 = ss[1].split("\\.");
				pt = (SimpleType) get("Knowledge:Meta:Type|" + s2[0]);
				se = pt.getLinkDef(s2[1]);
				break;
			case "Knowledge:Meta:AttDef":
				s2 = ss[1].split("\\.");
				pt = (SimpleType) get("Knowledge:Meta:Type|" + s2[0]);
				se = pt.getAttDef(s2[1]);
				break;
			case "Knowledge:Meta:Command":
				s2 = ss[1].split("\\.");
				ps = (SimpleService) get("Knowledge:Meta:Service|" + s2[0]);
				se = ps.getCommand(s2[1]);
				String cmdId = "dust" + ss[1].replace(":", "").replace(".", "");
				se.setFieldValue(optResolveMeta(DustAttributeToolsGenericIdentified.idLocal), cmdId);
				Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, se, DustToolsGenericComponents.DustLinkToolsGenericConnected.Owner, ps);
				break;
			default:
				se = new InfoEntityData(DustSimpleManagerData.this, null);
				s2 = ss[1].split("\\.");
				se.setFieldValue(optResolveMeta(DustAttributeToolsGenericIdentified.idLocal), s2[(1==s2.length) ? 0 : 1]);
				break;
			}
			allKnownEntities.add(se);
			return se;
		}

		@Override
		protected void initNew(InfoEntity item, String key, Object... hints) {
			// perhaps here come the external loading, but not for now and for meta
		}
	};

	@SuppressWarnings("unchecked")
	<RetType> RetType optResolveMeta(Object entity) {
		if (entity instanceof IdentifiableMeta) {
			String idGlobalId = DustUtilsGen.metaToId((IdentifiableMeta)entity);
			return (RetType) factGlobalEntities.get(idGlobalId);
		} else {
			return (RetType) entity;
		}
	}

	// public void setMeta(DustSimpleManagerMeta meta) {
	// this.meta = meta;
	// addParentSource(meta);
	// }

	void addParentSource(DustKnowledgeInfoSource src) {
		parentSources.add(src);
	}

	@Override
	public boolean dustKnowledgeInfoSourceIsTypeSupported(DustType eType) {
		return true;
	}

	@Override
	public InfoEntity dustKnowledgeInfoSourceGet(String idGlobal) throws Exception {
		InfoEntity ret = null;

		if (DustUtils.isEmpty(idGlobal)) {
			ret = new InfoEntityData(this, null);
			allKnownEntities.add(ret);
		} else {
			// DustType t = DustUtilsGen.getTypeFromId(idStore);
			// if (meta.dustKnowledgeInfoSourceIsTypeSupported(t)) {
			// ret = (InfoEntity) meta.dustKnowledgeInfoSourceGet(idStore);
			// } else
			{
				ret = factGlobalEntities.get(idGlobal);
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
		((InfoEntity) entity).setState(DustConstKnowledgeInfoEntityState.Destructed);
	}

}
