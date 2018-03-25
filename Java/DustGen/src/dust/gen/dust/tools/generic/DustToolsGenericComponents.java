package dust.gen.dust.tools.generic;

import dust.gen.dust.DustComponents;
import dust.gen.dust.core.meta.DustCoreMetaComponents.DustTypeCoreMeta;

public interface DustToolsGenericComponents extends DustComponents {

	enum DustAttributeToolsGenericIdentified implements DustAttribute {
		idLocal, idCombined;

		@Override
		public DustType getType() {
			return DustTypeToolsGeneric.Identified;
		}
	}

	enum DustLinkToolsGenericConnected implements DustLink {
		Owner, Requires;

		@Override
		public DustType getType() {
			return DustTypeToolsGeneric.Connected;
		}
	}

	enum DustTypeToolsGeneric implements DustType {
		Identified, Connected;
	}

	enum DustCommandToolsGenericInitable implements DustCommand {
		Init;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustServiceToolsGeneric.Initable;
		}
	}

	enum DustServiceToolsGeneric implements DustService {
		Initable,;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Service;
		}
	}

}
