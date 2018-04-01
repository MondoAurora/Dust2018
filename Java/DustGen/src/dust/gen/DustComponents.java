package dust.gen;

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
		DustService[] getExtends();
	}
	
	interface DustCommand extends DustEntity {
		DustService getService();
	}
}
