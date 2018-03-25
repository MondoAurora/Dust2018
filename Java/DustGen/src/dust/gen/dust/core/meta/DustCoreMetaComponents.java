package dust.gen.dust.core.meta;

import dust.gen.dust.core.data.DustCoreDataComponents;

public interface DustCoreMetaComponents extends DustCoreDataComponents {
	
	enum DustConstCoreMetaAttrType {
		fldId, fldInt, fldFloat, fldBool, fldRaw;
	}

	enum DustConstCoreMetaLinkType {
		linkSingle, linkSet, linkArray, linkMap;
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
