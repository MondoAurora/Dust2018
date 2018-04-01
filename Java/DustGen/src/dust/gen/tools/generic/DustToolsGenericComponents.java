package dust.gen.tools.generic;

import dust.gen.DustComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents.DustTypeKnowledgeMeta;

public interface DustToolsGenericComponents extends DustComponents {

	enum DustAttributeToolsGenericIdentified implements DustAttribute {
		idLocal, idCombined;

		@Override
		public DustType getType() {
			return DustTypeToolsGeneric.Identified;
		}
	}

	enum DustLinkToolsGenericConnected implements DustLink {
		Owner, Requires, Extends;

		@Override
		public DustType getType() {
			return DustTypeToolsGeneric.Connected;
		}
	}

	enum DustLinkToolsGenericChain implements DustLink {
		NextEntity, DefaultMessage;

		@Override
		public DustType getType() {
			return DustTypeToolsGeneric.Chain;
		}
	}

	enum DustTypeToolsGeneric implements DustType {
		Identified, Connected, Chain;
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
			return DustTypeKnowledgeMeta.Service;
		}
	}

}
