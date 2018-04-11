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
	
	
	enum DustLinkKnowledgeInfoEntity implements DustLink {
		Services, PrimaryType;
	}

	enum DustAttributeKnowledgeInfoEntity implements DustAttribute {
		svcImpl;
	}

	enum DustAttributeKnowledgeInfoIterator implements DustAttribute {
		index, key;
	}
	
	enum DustLinkKnowledgeInfoIterator implements DustLink {
		Cardinality;
	}
	
	enum DustAttributeKnowledgeInfoVariant implements DustAttribute {
		varType, value;
	}

	enum DustTypeKnowledgeInfo implements DustType {
		Entity, Link, Tag, Tagged, Variant, Iterator;
	}
}
