package dust.gen.dust.core.runtime;

import dust.gen.dust.core.data.DustCoreDataComponents;
import dust.gen.dust.core.exec.DustCoreExecComponents;
import dust.gen.dust.core.meta.DustCoreMetaComponents;

public interface DustCoreRuntimeComponents extends DustCoreDataComponents, DustCoreMetaComponents, DustCoreExecComponents {

	enum DustStatusCoreRuntime implements DustEntity {
		LinkCreationError, MessageSendError;

		@Override
		public DustType getType() {
			return DustTypeCoreExec.Status;
		}
	}

	enum DustLinkCoreRuntimeManager implements DustLink {
		InitMessage, BinaryManager, MetaManager;

		@Override
		public DustType getType() {
			return DustTypeCoreRuntime.Manager;
		}
	}

	enum DustTypeCoreRuntime implements DustType {
		Manager
	}
}
