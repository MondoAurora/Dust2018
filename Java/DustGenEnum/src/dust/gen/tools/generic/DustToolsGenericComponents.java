package dust.gen.tools.generic;

import dust.gen.DustComponents;

public interface DustToolsGenericComponents extends DustComponents {

	enum DustAttributeToolsGenericIdentified implements DustAttribute {
		idLocal, idCombined;

	}

	enum DustLinkToolsGenericConnected implements DustLink {
		Owner, Requires, Extends;
	}

	enum DustLinkToolsGenericChain implements DustLink {
		NextEntity, DefaultMessage;
	}

	enum DustTypeToolsGeneric implements DustType {
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
