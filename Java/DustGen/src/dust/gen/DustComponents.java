package dust.gen;

public interface DustComponents {
	interface IdentifiableMeta {
	}
	
	interface DustType extends IdentifiableMeta {
	}

	interface DustEntity {
		DustType getType();
	}
	
	interface DustAttribute extends DustEntity, IdentifiableMeta {
	}

	interface DustLink extends DustEntity, IdentifiableMeta {
	}

	interface DustConst extends DustEntity, IdentifiableMeta {
	}

	interface DustService extends DustEntity {
		DustService[] getExtends();
	}
	
	interface DustCommand extends DustEntity {
		DustService getService();
	}
}
