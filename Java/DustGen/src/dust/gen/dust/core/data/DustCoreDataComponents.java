package dust.gen.dust.core.data;

import dust.gen.dust.core.meta.DustCoreMetaComponents;
import dust.gen.dust.tools.generic.DustToolsGenericComponents;

public interface DustCoreDataComponents extends DustToolsGenericComponents, DustCoreMetaComponents {

	enum DustConstCoreDataEntityState implements DustEntity {
		Temporal, InSync, RefChanged, Changed, Constructed, Destructed;

		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Const;
		}
	}

	enum DustConstCoreDataLinkCommand implements DustEntity {
		Add, Replace, Remove, ChangeKey;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Const;
		}
	}

	enum DustConstCoreDataVisitorResponse implements DustEntity {
		OK, Skip, Exit, Repeat, Restart;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Const;
		}
	}

	enum DustConstCoreDataContext implements DustEntity {
		Self, Message, Block;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Const;
		}
	}
	
	
	enum DustLinkCoreDataEntity implements DustLink {
		Services;
		@Override
		public DustType getType() {
			return DustTypeCoreData.Entity;
		}
	}

	enum DustAttributeCoreDataEntity implements DustAttribute {
		svcImpl;
		@Override
		public DustType getType() {
			return DustTypeCoreData.Entity;
		}
	}

	enum DustTypeCoreData implements DustType {
		Entity, Link, Tag, Tagged, Variant;
	}
}
