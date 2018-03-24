package dust.gen.dust.base;

import dust.gen.dust.DustComponents;
import dust.gen.dust.meta.DustMetaComponents.DustTypeMeta;

public interface DustBaseComponents extends DustComponents {

	enum DustEntityState implements DustEntity {
		Temporal, InSync, RefChanged, Changed, Constructed, Destructed;

		@Override
		public DustType getType() {
			return DustBaseComponents.DustTypeBase.ConstValue;
		}
	}

	enum DustBaseLinkCommand implements DustEntity {
		Add, Replace, Remove, ChangeKey;
		@Override
		public DustType getType() {
			return DustBaseComponents.DustTypeBase.ConstValue;
		}
	}

	enum DustBaseVisitorResponse implements DustEntity {
		OK, Skip, Exit, Repeat, Restart;
		@Override
		public DustType getType() {
			return DustBaseComponents.DustTypeBase.ConstValue;
		}
	}

	enum DustBaseContext implements DustEntity {
		Self, Message, Block;
		@Override
		public DustType getType() {
			return DustBaseComponents.DustTypeBase.ConstValue;
		}
	}
	
	
	
	

	enum DustLinkBaseMessage implements DustLink {
		Command, Target;
		@Override
		public DustType getType() {
			return DustTypeBase.Message;
		}
	}

	enum DustLinkBaseEntity implements DustLink {
		Services;
		@Override
		public DustType getType() {
			return DustTypeBase.Entity;
		}
	}

	enum DustAttributeBaseEntity implements DustAttribute {
		svcImpl;
		@Override
		public DustType getType() {
			return DustTypeBase.Entity;
		}
	}

	enum DustTypeBase implements DustType {
		Entity, Message, ConstValue, StatusInfo;
	}

	enum DustCommandBaseInitable implements DustCommand {
		Init;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustServiceBase.Initable;
		}
	}

	enum DustServiceBase implements DustService {
		Initable,
		;
		@Override
		public DustType getType() {
			return DustTypeMeta.Service;
		}
	}
}
