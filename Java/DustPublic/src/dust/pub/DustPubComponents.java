package dust.pub;

import dust.gen.dust.base.DustBaseComponents;

public interface DustPubComponents extends DustBaseComponents {
	String ID_SEP = ":";
	
	interface Creator<RetType> {
		RetType create(Object ... params);
	}
}
