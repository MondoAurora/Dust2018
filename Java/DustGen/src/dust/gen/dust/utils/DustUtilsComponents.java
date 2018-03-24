package dust.gen.dust.utils;

import dust.gen.dust.base.DustBaseComponents;

public interface DustUtilsComponents extends DustBaseComponents {
	
	enum DustAttributeUtilsIdentified implements DustAttribute {
		idLocal, idCombined;
		
		@Override
		public DustType getType() {
			return DustTypeUtils.Identified;
		}
	}
	
	enum DustLinkUtilsOwned implements DustLink {
		Owner;
		
		@Override
		public DustType getType() {
			return DustTypeUtils.Owned;
		}
	}

	
	enum DustTypeUtils implements DustType {
		Identified, Owned
		
		;
	}

}
