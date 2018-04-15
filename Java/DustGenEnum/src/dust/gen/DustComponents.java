package dust.gen;

public interface DustComponents {
	interface IdentifiableMeta {
	}

	interface DustEntity extends IdentifiableMeta {
	}

	interface DustEntity {
//		DustEntity getType();
	}

	interface DustEntity extends DustEntity, IdentifiableMeta {
	}

	interface DustEntity extends DustEntity, IdentifiableMeta {
	}

	interface DustConst extends DustEntity, IdentifiableMeta {
	}

	interface DustService extends DustEntity, IdentifiableMeta {
	}

	interface DustCommand extends DustEntity, IdentifiableMeta {
	}
}
