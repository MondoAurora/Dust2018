package dust.pub;

import dust.gen.dust.base.DustBaseComponents;

public interface DustPubComponents extends DustBaseComponents {
	interface Creator<RetType> {
		RetType create(Object ... params);
	}
}
