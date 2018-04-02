package dust.gen.knowledge.info;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeInfoComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents {

	enum DustConstKnowledgeInfoEntityState implements DustEntity {
		Temporal, InSync, RefChanged, Changed, Constructed, Destructed;

		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Const;
		}
	}

	enum DustConstKnowledgeInfoLinkCommand implements DustEntity {
		Add, Replace, Remove, ChangeKey;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Const;
		}
	}

	enum DustConstKnowledgeInfoVisitorResponse implements DustEntity {
		OK, Skip, Exit, Repeat, Restart;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Const;
		}
	}

	enum DustConstKnowledgeInfoContext implements DustEntity {
		Self, Message, Block;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Const;
		}
	}
	
	
	enum DustLinkKnowledgeInfoEntity implements DustLink {
		Services;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeInfo.Entity;
		}
	}

	enum DustAttributeKnowledgeInfoEntity implements DustAttribute {
		svcImpl;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeInfo.Entity;
		}
	}

	enum DustAttributeKnowledgeInfoIterator implements DustAttribute {
		index, key;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeInfo.Iterator;
		}
	}
	
	enum DustLinkKnowledgeInfoIterator implements DustLink {
		Cardinality;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeInfo.Iterator;
		}
	}
	
	enum DustAttributeKnowledgeInfoVariant implements DustAttribute {
		varType, value;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeInfo.Variant;
		}
	}
	
	

	enum DustTypeKnowledgeInfo implements DustType {
		Entity, Link, Tag, Tagged, Variant, Iterator;
	}
}
