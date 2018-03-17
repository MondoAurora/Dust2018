package dust.pub.metaenum;

public interface DustMetaEnum {
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
