package dust.gen.dust.core.meta;

import dust.gen.dust.DustComponents;

public interface DustCoreMetaComponents extends DustComponents {
	
	enum DustConstCoreMetaAttrType {
		Id, Int, Float, Bool, Raw;
	}

	enum DustConstCoreMetaCardinality {
		Single, Set, Array, Map;
	}

	enum DustTypeCoreMeta implements DustType {
		Vendor, Domain, Unit, Type, AttDef, LinkDef, Service, Command, Const
	}

	enum DustAttributeCoreMetaCommand implements DustAttribute {
		boundMethod;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Command;
		}
	}

	enum DustAttributeCoreMetaService implements DustAttribute {
		boundClass;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Service;
		}
	}

	enum DustCommandCoreMetaManager implements DustCommand {
		RegisterUnit(null);
	
		private final DustType paramType;
	
		private DustCommandCoreMetaManager(DustType paramType) {
			this.paramType = paramType;
		}
	
		@Override
		public DustService getService() {
			return DustServiceCoreMeta.Manager;
		}
	
		@Override
		public DustType getType() {
			return paramType;
		}
	}

	enum DustServiceCoreMeta implements DustService {
		Manager,
		;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Service;
		}
	}

}
