package dust.mj02.dust.tools;

import dust.mj02.dust.DustComponents;

public interface DustGenericComponents extends DustComponents {
	
	enum DustGenericTypes {
		Identified, Connected, Tag, Tagged
	};
	
	enum DustGenericAtts {
		identifiedIdLocal, 

	};
	
	enum DustGenericLinks {
		Owner, Requires, Tags
	};
	
}
