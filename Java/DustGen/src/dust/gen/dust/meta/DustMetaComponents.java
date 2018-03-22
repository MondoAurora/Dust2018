package dust.gen.dust.meta;

import dust.gen.dust.base.DustBaseComponents;

public interface DustMetaComponents extends DustBaseComponents {
	
	enum DustMetaAttrType {
		fldId, fldInt, fldFloat, fldBool, fldRaw;
	}

	enum DustMetaLinkType {
		linkSingle, linkSet, linkArray, linkMap;
	}

}
