package dust.gen.dust.aaa;

import dust.gen.dust.base.DustBaseComponents;

public interface DustAaaComponents extends DustBaseComponents {
	
	enum DustAaaMessages implements DustEntity {
		AccessDenied;
		@Override
		public DustType getType() {
			return DustBaseTypes.StatusInfo;
		}
	}
	
	enum DustAccessMode implements DustEntity {
		Check, Read, Write, Execute;
		@Override
		public DustType getType() {
			return DustBaseTypes.ConstValue;
		}
	}
	
}
