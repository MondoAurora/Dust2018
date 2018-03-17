package dust.gen.dust.aaa;

import dust.gen.dust.base.DustBaseComponents;
import dust.pub.metaenum.DustMetaEnum;

public interface DustAaaComponents extends DustMetaEnum, DustBaseComponents {
	
	enum DustAaaMessages implements DustBaseEntity {
		AccessDenied
	}
	
	enum DustAccessMode implements DustBaseEntity {
		Check, Read, Write, Execute
	}
	
}
