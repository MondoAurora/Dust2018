package dust.runtime.simple;

import dust.gen.tools.generic.DustToolsGenericComponents;
import dust.pub.Dust;
import dust.pub.DustUtils;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleManagerMeta implements DustSimpleRuntimeComponents, // DustMetaServices.DustMetaManager,
		DustBootComponents.DustConfigurable, DustBootComponents.DustShutdownAware {

	private DustUtilsFactory<DustType, SimpleType> factType = new DustUtilsFactory<DustType, SimpleType>(false) {
		@Override
		protected SimpleType create(DustType key, Object... hints) {
			return new SimpleType(key);
		}
	};

	DustUtilsFactory<DustAttribute, SimpleAttDef> factAttDefs = new DustUtilsFactory<DustAttribute, SimpleAttDef>(
			false) {
		@Override
		protected SimpleAttDef create(DustAttribute key, Object... hints) {
			return new SimpleAttDef(getSimpleType(key.getType()), key);
		}
	};

	DustUtilsFactory<DustLink, SimpleLinkDef> factLinkDefs = new DustUtilsFactory<DustLink, SimpleLinkDef>(false) {
		@Override
		protected SimpleLinkDef create(DustLink key, Object... hints) {
			return new SimpleLinkDef(getSimpleType(key.getType()), key);
		}
	};

	DustUtilsFactory<Enum<?>, SimpleEntity> factConstants = new DustUtilsFactory<Enum<?>, SimpleEntity>(false) {
		@Override
		protected SimpleEntity create(Enum<?> key, Object... hints) {
			SimpleEntity se = new SimpleEntity(null, getSimpleType(DustTypeKnowledgeMeta.Const));
			setFieldValue(se, DustToolsGenericComponents.DustAttributeToolsGenericIdentified.idLocal, DustUtils.toLocalId(key));

			if (key instanceof DustCommand) {
				DustService svc = ((DustCommand) key).getService();
				Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, se, svc, DustToolsGenericComponents.DustLinkToolsGenericConnected.Owner);
				setFieldValue(se, DustToolsGenericComponents.DustAttributeToolsGenericIdentified.idCombined, DustUtils.toEnumId((Enum<?>) svc));
			}

			if (key instanceof DustService) {
				for (DustService ext : ((DustService) key).getExtends()) {
					Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, se, ext, DustToolsGenericComponents.DustLinkToolsGenericConnected.Extends);
				}
			}

			return se;
		}
	};

	void setFieldValue(SimpleEntity se, DustAttribute att, Object value) {
		SimpleAttDef ad = factAttDefs.get(att);
		se.setFieldValue(ad, value);
	};
	//
	// void addRef(SimpleEntity left, DustLink link, DustEntity right) {
	// SimpleLinkDef ld = factLinkDefs.get(link);
	// se.setFieldValue(ad, value);
	// };

	SimpleType getSimpleType(DustType type) {
		return factType.get(type);
	}

	SimpleAttDef getSimpleAttDef(DustAttribute att) {
		return factAttDefs.get(att);
	}

	SimpleLinkDef getSimpleLinkDef(DustLink link) {
		return factLinkDefs.get(link);
	}

	SimpleEntity optResolveEntity(DustEntity entity) {
		return (entity instanceof Enum) ? factConstants.get((Enum<?>) entity) : null;
	}

	@Override
	public void init(DustConfig config) throws Exception {
	}

	@Override
	public void shutdown() throws Exception {
		factType.clear();
	}
}
