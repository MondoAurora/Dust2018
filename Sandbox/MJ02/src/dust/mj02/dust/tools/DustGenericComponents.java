package dust.mj02.dust.tools;

import dust.mj02.dust.DustComponents;

public interface DustGenericComponents extends DustComponents {
	
	enum DustGenericTypes implements DustEntityKey {
		Identified, Connected, Tag, Stream, 
		ContextAware, Reference, Callback
	};
	
	enum DustGenericAtts implements DustEntityKey {
		IdentifiedIdLocal, StreamFileName, StreamFileAccess, StreamWriter

	};
	
	enum DustGenericLinks implements DustEntityKey {
	    TagClass,
		ConnectedOwner, ConnectedRequires, ConnectedExtends, 
		ContextAwareEntity, ReferencePath, CallbackMessage
	};
	
}
