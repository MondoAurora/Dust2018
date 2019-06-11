package dust.mj02.dust.tools;

import dust.mj02.dust.DustComponents;

public interface DustCollectionComponents extends DustComponents {
	
	enum DustCollectionTypes implements DustEntityKey {
		Sequence, MapEntry
	};
	
	enum DustCollectionLinks implements DustEntityKey {
		SequenceMembers, MapEntryKey
	};
	
}
