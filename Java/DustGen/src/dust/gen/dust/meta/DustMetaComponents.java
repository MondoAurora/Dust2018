package dust.gen.dust.meta;

import dust.gen.dust.base.DustBaseComponents;

public interface DustMetaComponents extends DustBaseComponents {
	
	enum DustAttrType {
		fldId, fldInt, fldFloat, fldBool;
	}

	enum DustLinkType {
		linkSingle, linkSet, linkArray;
	}

}
