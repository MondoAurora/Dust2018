package dust.gen.dust.runtime;

import dust.gen.dust.base.DustBaseComponents;
import dust.gen.dust.meta.DustMetaComponents;

public interface DustRuntimeComponents extends DustBaseComponents, DustMetaComponents {

	enum DustStatusInfoRuntime implements DustEntity {
		LinkCreationError, MessageSendError;

		@Override
		public DustType getType() {
			return DustTypeBase.StatusInfo;
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

	enum DustCommandRuntimeMetaManager implements DustCommand {
		RegisterUnit(null);

		private final DustType paramType;

		private DustCommandRuntimeMetaManager(DustType paramType) {
			this.paramType = paramType;
		}

		@Override
		public DustService getService() {
			return DustServiceRuntime.MetaManager;
		}

		@Override
		public DustType getType() {
			return paramType;
		}
	}

	enum DustServiceRuntime implements DustService {
		MetaManager,
		;
		@Override
		public DustType getType() {
			return DustTypeMeta.Service;
		}
	}
}
