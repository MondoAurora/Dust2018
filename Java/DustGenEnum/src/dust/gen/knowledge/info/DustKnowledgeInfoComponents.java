package dust.gen.knowledge.info;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeInfoComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents {

	enum DustConstKnowledgeInfoEntityState implements DustConst {
		Temporal, InSync, RefChanged, Changed, Constructed, Destructed;
	}

	enum DustConstKnowledgeInfoLinkCommand implements DustConst {
		Add, Replace, Remove, ChangeKey;
	}

	enum DustConstKnowledgeInfoVisitorResponse implements DustConst {
		OK, Skip, Exit, Repeat, Restart;
	}

	enum DustConstKnowledgeInfoContext implements DustConst {
		Self, Message, Block;
	}
	
	
	enum DustLinkKnowledgeInfoEntity implements DustEntity {
		Services, PrimaryType;
	}

	enum DustAttributeKnowledgeInfoEntity implements DustEntity {
		svcImpl;
	}

	enum DustAttributeKnowledgeInfoIterator implements DustEntity {
		index, key;
	}
	
	enum DustLinkKnowledgeInfoIterator implements DustEntity {
		Cardinality;
	}
	
	enum DustAttributeKnowledgeInfoVariant implements DustEntity {
		varType, value;
	}

	enum DustTypeKnowledgeInfo implements DustEntity {
		Entity, Link, Tag, Tagged, Variant, Iterator;
	}
}
