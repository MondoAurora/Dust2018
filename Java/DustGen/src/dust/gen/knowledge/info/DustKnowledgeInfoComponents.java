package dust.gen.knowledge.info;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeInfoComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents {

	enum DustConstKnowledgeInfoEntityState {
		Temporal, InSync, RefChanged, Changed, Constructed, Destructed;
	}

	enum DustConstKnowledgeInfoLinkCommand {
		Add, Replace, Remove, ChangeKey;
	}

	enum DustConstKnowledgeInfoVisitorResponse {
		OK, Skip, Exit, Repeat, Restart;
	}

	enum DustConstKnowledgeInfoContext {
		Self, Message, Block;
	}
	
	
	enum DustLinkKnowledgeInfoEntity {
		Services, PrimaryType;
	}

	enum DustAttributeKnowledgeInfoEntity {
		svcImpl;
	}

	enum DustAttributeKnowledgeInfoIterator {
		index, key;
	}
	
	enum DustLinkKnowledgeInfoIterator {
		Cardinality;
	}
	
	enum DustAttributeKnowledgeInfoVariant {
		varType, value;
	}

	enum DustTypeKnowledgeInfo {
		Entity, Link, Tag, Tagged, Variant, Iterator;
	}

	interface DustKnowledgeInfoSource {
		boolean dustKnowledgeInfoSourceIsTypeSupported(DustEntity eType);
		
		DustEntity dustKnowledgeInfoSourceGet(DustEntity type, String idStore) throws Exception;
		void dustKnowledgeInfoSourceFind(DustEntity type, DustEntity expression, DustEntity processor) throws Exception;
		void dustKnowledgeInfoSourceDestruct(DustEntity entity) throws Exception;
	}
}
