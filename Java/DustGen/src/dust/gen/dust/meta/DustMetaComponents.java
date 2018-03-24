package dust.gen.dust.meta;

import dust.gen.dust.base.DustBaseComponents;

public interface DustMetaComponents extends DustBaseComponents {
	
	enum DustMetaAttrType {
		fldId, fldInt, fldFloat, fldBool, fldRaw;
	}

	enum DustMetaLinkType {
		linkSingle, linkSet, linkArray, linkMap;
	}

	enum DustTypeMeta implements DustType {
		Vendor, Domain, Unit, Type, AttDef, LinkDef, Service, Command, Link
	}

	enum DustAttributeMetaCommand implements DustAttribute {
		binMethod;
		@Override
		public DustType getType() {
			return DustTypeMeta.Command;
		}
	}

	enum DustAttributeMetaService implements DustAttribute {
		binClass;
		@Override
		public DustType getType() {
			return DustTypeMeta.Service;
		}
	}

}
