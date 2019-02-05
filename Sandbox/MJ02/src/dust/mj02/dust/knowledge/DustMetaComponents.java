package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustMetaComponents extends DustComponents {
	
	enum DustMetaTypes {
		Type, AttDef, LinkDef
	};
	
	enum DustMetaAtts {
		LinkDefType, AttDefType
	};
	
	enum DustMetaValueAttDefType {
		AttDefBool, AttDefIdentifier, AttDefFloat, AttDefInteger
	};
	
	enum DustMetaValueLinkDefType {
		LinkDefSet, LinkDefMap, LinkDefArray, LinkDefSingle
	};
	
}
