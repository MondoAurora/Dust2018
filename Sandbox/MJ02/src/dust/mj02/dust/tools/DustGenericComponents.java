package dust.mj02.dust.tools;

import dust.mj02.dust.DustComponents;

public interface DustGenericComponents extends DustComponents {
	
	enum DustGenericTypes implements DustEntityKey {
		Identified, Connected, Tag, Tagged, Stream
	};
	
	enum DustGenericAtts implements DustEntityKey {
		IdentifiedIdLocal, StreamFileName

	};
	
	enum DustGenericLinks implements DustEntityKey {
		ConnectedOwner, ConnectedRequires, ConnectedExtends, TaggedTags
	};
	
}
