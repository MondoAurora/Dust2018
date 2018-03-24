package dust.gen.dust.runtime;

import dust.gen.dust.base.DustBaseComponents;

public interface DustRuntimeComponents extends DustBaseComponents {

	enum DustMessageRuntime implements DustEntity {
		LinkCreationError, MessageSendError;

		@Override
		public DustType getType() {
			return DustBaseTypes.StatusInfo;
		}
	}

	enum DustLinkRuntimeRuntime implements DustLink {
		InitMessage, BinaryManager, MetaManager;

		@Override
		public DustType getType() {
			return DustTypeRuntime.Runtime;
		}
	}

	enum DustTypeRuntime implements DustType {
		Runtime, MetaManager
	}

	enum DustRuntimeMessageMetaManager implements DustMessage {
		RegisterUnit(null);

		private final DustType paramType;

		private DustRuntimeMessageMetaManager(DustType paramType) {
			this.paramType = paramType;
		}

		@Override
		public DustService getService() {
			return DustRuntimeServices.MetaManager;
		}

		@Override
		public DustType getType() {
			return paramType;
		}
	}

	enum DustRuntimeServices implements DustService {
		MetaManager,
		;
		@Override
		public DustType getType() {
			return DustBaseTypes.Service;
		}
	}
}
