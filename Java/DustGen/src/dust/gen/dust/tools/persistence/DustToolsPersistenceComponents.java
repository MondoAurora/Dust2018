package dust.gen.dust.tools.persistence;

import dust.gen.dust.core.exec.DustCoreExecComponents.DustTypeCoreExec;
import dust.gen.dust.core.meta.DustCoreMetaComponents;
import dust.gen.dust.tools.generic.DustToolsGenericComponents;

public interface DustToolsPersistenceComponents extends DustToolsGenericComponents, DustCoreMetaComponents {
	enum DustStatusToolsPersistence implements DustEntity {
		HandlerInvalid, HandlerVersionMismatch;
		@Override
		public DustType getType() {
			return DustTypeCoreExec.Status;
		}
	}
	
	enum DustCommandToolsPersistenceStore implements DustCommand {
		Read;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustServiceToolsPersistence.Store;
		}
	}

	enum DustServiceToolsPersistence implements DustService {
		Store,;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Service;
		}
	}

}
