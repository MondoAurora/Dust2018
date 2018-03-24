package dust.gen.dust.base;

import dust.gen.dust.DustComponents;

public interface DustBaseComponents extends DustComponents {

	enum DustEntityState implements DustEntity {
		Temporal, InSync, RefChanged, Changed, Constructed, Destructed;

		@Override
		public DustType getType() {
			return DustBaseComponents.DustBaseTypes.StatusInfo;
		}
	}

	enum DustBaseLinkCommand implements DustEntity {
		Add, Replace, Remove, ChangeKey;
		@Override
		public DustType getType() {
			return DustBaseComponents.DustBaseTypes.ConstValue;
		}
	}

	enum DustBaseVisitorResponse implements DustEntity {
		OK, Skip, Exit, Repeat, Restart;
		@Override
		public DustType getType() {
			return DustBaseComponents.DustBaseTypes.ConstValue;
		}
	}

	enum DustBaseContext implements DustEntity {
		Self, Message, Block;
		@Override
		public DustType getType() {
			return DustBaseComponents.DustBaseTypes.ConstValue;
		}
	}

	enum DustLinkBaseMessage implements DustLink {
		Command, Target;
		@Override
		public DustType getType() {
			return DustBaseTypes.Message;
		}

	}

	enum DustLinkBaseEntity implements DustLink {
		Services;
		@Override
		public DustType getType() {
			return DustBaseTypes.Entity;
		}
	}

	enum DustAttributeBaseEntity implements DustAttribute {
		svcImpl;
		@Override
		public DustType getType() {
			return DustBaseTypes.Entity;
		}
	}

	enum DustBaseTypes implements DustType {
		Entity, Message, ConstValue, StatusInfo, Service;

	}

	enum DustBaseMessageInitable implements DustMessage {
		Init;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustBaseServices.Initable;
		}
	}

	enum DustBaseServices implements DustService {
		Initable,
		;
		@Override
		public DustType getType() {
			return DustBaseTypes.Service;
		}
	}
}
