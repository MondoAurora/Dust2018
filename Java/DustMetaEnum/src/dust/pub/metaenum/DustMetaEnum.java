package dust.pub.metaenum;

public interface DustMetaEnum {
	interface DustMetaTypeDescriptor {
		Class<? extends Enum<?>> getAttribEnum();
		Class<? extends Enum<?>> getLinkEnum();
	}
	
	interface DustMetaServiceDescriptor {
		Class<? extends Enum<?>> getMessageEnum();
	}
	
	interface DustMetaCommandDescriptor {
		DustMetaTypeDescriptor getParamTypeDescriptor();
	}
	
}
