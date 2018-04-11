package dust.gen.knowledge.meta;

import dust.gen.DustComponents;

public interface DustKnowledgeMetaComponents extends DustComponents {
	
	enum DustConstKnowledgeMetaAttrType implements DustConst {
		Id, Int, Float, Bool, Raw;
		
	}

	enum DustConstKnowledgeMetaCardinality implements DustConst {
		Single, Set, Array, Map;
		
	}

	enum DustTypeKnowledgeMeta implements DustType {
		Unit, Type, AttDef, LinkDef, Service, Command, Const
	}

	enum DustAttributeKnowledgeMetaCommand implements DustAttribute {
		boundMethod;
	}

	enum DustAttributeKnowledgeMetaService implements DustAttribute {
		boundClass;
	}

}
