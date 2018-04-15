package dust.gen.knowledge.meta;

import dust.gen.DustGenComponents;

public interface DustKnowledgeMetaComponents extends DustGenComponents {
	
	enum DustConstKnowledgeMetaAttrType {
		Id, Int, Float, Bool, Raw;
	}

	enum DustConstKnowledgeMetaCardinality {
		Single, Set, Array, Map;
	}

	enum DustTypeKnowledgeMeta {
		Unit, Type, AttDef, LinkDef, Service, Command, Const
	}

	enum DustAttributeKnowledgeMetaCommand {
		boundMethod;
	}

	enum DustAttributeKnowledgeMetaService {
		boundClass;
	}

}
