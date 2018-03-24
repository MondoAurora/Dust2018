package dust.gen.dust;

public interface DustComponents {
	interface DustType {
	}

	interface DustEntity {
		DustType getType();
	}
	
	interface DustAttribute extends DustEntity {
	}

	interface DustLink extends DustEntity {
	}

	interface DustService extends DustEntity {
	}
	
	interface DustCommand extends DustEntity {
		DustService getService();
	}

}
