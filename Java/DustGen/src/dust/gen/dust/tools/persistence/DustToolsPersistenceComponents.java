package dust.gen.dust.tools.persistence;

import dust.gen.dust.DustComponents;

public interface DustToolsPersistenceComponents extends DustComponents {

	enum DustAttributeToolsPersistenceStoreInfo implements DustAttribute {
		idGlobal, idLocal;

		@Override
		public DustType getType() {
			return DustTypeToolsPersistence.StoreInfo;
		}
	}

	enum DustAttributeToolsPersistenceMetaInfo implements DustAttribute {
		globalId, alias, ownerType;

		@Override
		public DustType getType() {
			return DustTypeToolsPersistence.MetaInfo;
		}
	}

	enum DustTypeToolsPersistence implements DustType {
		StoreInfo, MetaInfo;
	}

}
