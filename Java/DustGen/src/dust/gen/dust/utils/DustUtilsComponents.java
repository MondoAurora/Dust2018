package dust.gen.dust.utils;

import dust.gen.dust.base.DustBaseComponents;

public interface DustUtilsComponents extends DustBaseComponents {
	
	enum DustAttributeUtilsIdentified implements DustAttribute {
		idLocal, idCombined;
		
		@Override
		public DustType getType() {
			return DustUtilsTypes.Identified;
		}
	}
	
	enum DustLinkUtilsOwned implements DustLink {
		Owner;
		
		@Override
		public DustType getType() {
			return DustUtilsTypes.Owned;
		}
	}

	
	enum DustUtilsTypes implements DustType {
		Identified, Owned
		
		;
	}

}
