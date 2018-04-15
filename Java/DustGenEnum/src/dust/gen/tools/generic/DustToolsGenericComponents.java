package dust.gen.tools.generic;

import dust.gen.DustComponents;

public interface DustToolsGenericComponents extends DustComponents {

	enum DustAttributeToolsGenericIdentified implements DustEntity {
		idLocal, idCombined;

	}

	enum DustLinkToolsGenericConnected implements DustEntity {
		Owner, Requires, Extends;
	}

	enum DustLinkToolsGenericChain implements DustEntity {
		NextEntity, DefaultMessage;
	}

	enum DustTypeToolsGeneric implements DustEntity {
		Identified, Connected, Chain;
	}

	enum DustCommandToolsGenericInitable implements DustCommand {
		Init;
	}

	enum DustServiceToolsGeneric implements DustService {
		Initable,;
		final DustService[] extServices;
		
		private DustServiceToolsGeneric(DustService... extServices) {
			this.extServices = extServices;
		}
	}

}
