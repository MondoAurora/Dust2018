package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustDataComponents extends DustComponents {
	enum DustDataTypes implements DustEntityKey {
		Entity, Message
	};
	
	enum DustDataAtts implements DustEntityKey {
	}
	
	enum DustDataLinks implements DustEntityKey {
		EntityPrimaryType, EntityModels, EntityServices, EntityBinaries,
		MessageCommand
	}
}
