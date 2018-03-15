package dust.gen.dust.meta;

import dust.gen.dust.base.DustBaseComponents;

public interface DustMetaServices extends DustBaseComponents {
	interface DustMetaTypeDescriptor {
		Class<? extends Enum<?>> getAttribEnum();
		Class<? extends Enum<?>> getLinkEnum();
	}
	
	interface DustMetaServiceDescriptor {
		Class<? extends Enum<?>> getMessageEnum();
	}
	
	interface DustMetaManager {
		void registerUnit(Class<? extends Enum<?>> types, Class<? extends Enum<?>> services);
	}
}
