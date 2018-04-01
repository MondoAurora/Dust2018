package dust.gen.knowledge.meta;

import dust.gen.DustComponents;

public interface DustKnowledgeMetaComponents extends DustComponents {
	
	enum DustConstKnowledgeMetaAttrType {
		Id, Int, Float, Bool, Raw;
	}

	enum DustConstKnowledgeMetaCardinality {
		Single, Set, Array, Map;
	}

	enum DustTypeKnowledgeMeta implements DustType {
		Vendor, Domain, Unit, Type, AttDef, LinkDef, Service, Command, Const
	}

	enum DustAttributeKnowledgeMetaCommand implements DustAttribute {
		boundMethod;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Command;
		}
	}

	enum DustAttributeKnowledgeMetaService implements DustAttribute {
		boundClass;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Service;
		}
	}

	enum DustCommandKnowledgeMetaManager implements DustCommand {
		RegisterUnit(null);
	
		private final DustType paramType;
	
		private DustCommandKnowledgeMetaManager(DustType paramType) {
			this.paramType = paramType;
		}
	
		@Override
		public DustService getService() {
			return DustServiceKnowledgeMeta.Manager;
		}
	
		@Override
		public DustType getType() {
			return paramType;
		}
	}

	enum DustServiceKnowledgeMeta implements DustService {
		Manager,
		;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Service;
		}
	}

}
