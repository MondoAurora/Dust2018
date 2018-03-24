package dust.gen.dust.aaa;

import dust.gen.dust.base.DustBaseComponents;

public interface DustAaaComponents extends DustBaseComponents {
	
	enum DustStatusInfoAaa implements DustEntity {
		AccessDenied;
		@Override
		public DustType getType() {
			return DustTypeBase.StatusInfo;
		}
	}
	
	enum DustAccessMode implements DustEntity {
		Check, Read, Write, Execute;
		@Override
		public DustType getType() {
			return DustTypeBase.ConstValue;
		}
	}
	
}
