package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustDataComponents extends DustComponents {
	enum DustDataTypes implements DustEntityKey {
		Entity, Message, Variant
	};
	
	enum DustDataAtts implements DustEntityKey {
		EntityBinaries, MessageReturn, VariantValue
	}
	
	enum DustDataTags implements DustEntityKey {
		EntityChanged, EntityChanging
	}
	
	enum DustDataLinks implements DustEntityKey {
		EntityPrimaryType, EntityModels, EntityServices, EntityTags, EntityAccessControl, 
		MessageCommand, MessageSource, MessageTarget, VariantValueType
	}
}
