package dust.mj02.dust.tools;

import dust.mj02.dust.DustComponents;

public interface DustGenericComponents extends DustComponents {
	
	enum DustGenericTypes implements DustEntityKey {
		Identified, Connected, Tag, Stream, ContextAware
	};
	
	enum DustGenericAtts implements DustEntityKey {
		IdentifiedIdLocal, StreamFileName, StreamFileAccess

	};
	
	enum DustGenericLinks implements DustEntityKey {
		ConnectedOwner, ConnectedRequires, ConnectedExtends, ContextAwareEntity
	};
	
}
