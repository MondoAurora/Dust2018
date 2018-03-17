package dust.gen.dust.aaa;

import dust.gen.dust.base.DustBaseComponents;
import dust.pub.metaenum.DustMetaEnum;

public interface DustAaaComponents extends DustMetaEnum, DustBaseComponents {
	
	enum DustAaaMessages implements DustEntity {
		AccessDenied
	}
	
	enum DustAccessMode implements DustEntity {
		Check, Read, Write, Execute
	}
	
}
