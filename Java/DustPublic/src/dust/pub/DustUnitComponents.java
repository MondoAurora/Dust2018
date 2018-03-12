package dust.pub;

public interface DustUnitComponents extends DustComponents {
	interface DustTypeDescriptor {
		Class<? extends Enum<?>> getAttribEnum();
		Class<? extends Enum<?>> getLinkEnum();
	}
	interface DustServiceDescriptor {
		Class<? extends Enum<?>> getMessageEnum();
	}
}
