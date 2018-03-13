package dust.gen.dust.meta;

import dust.gen.dust.base.DustBaseComponents;

public interface DustMetaComponents extends DustBaseComponents {
	interface DustMetaTypeDescriptor {
		Class<? extends Enum<?>> getAttribEnum();
		Class<? extends Enum<?>> getLinkEnum();
	}
	
	interface DustMetaServiceDescriptor {
		Class<? extends Enum<?>> getMessageEnum();
	}
}
