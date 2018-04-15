package dust.gen.tools.generic;

import dust.gen.DustGenComponents;

public interface DustToolsGenericComponents extends DustGenComponents {

	enum DustAttributeToolsGenericIdentified {
		idLocal, idCombined;

	}

	enum DustLinkToolsGenericConnected {
		Owner, Requires, Extends;
	}

	enum DustLinkToolsGenericChain {
		NextEntity, DefaultMessage;
	}

	enum DustTypeToolsGeneric {
		Identified, Connected, Chain;
	}

	enum DustCommandToolsGenericInitable {
		Init;
	}

	enum DustServiceToolsGeneric {
		Initable,;
		
	}

	interface DustToolsGenericInitable {
		void dustToolsGenericInitableInit() throws Exception;
	}

}
