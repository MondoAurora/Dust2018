package dust.gen.knowledge.info;

import dust.gen.DustUtilsGen.AttributeWrapper;
import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.DustUtilsGen.LinkWrapper;
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

	enum DustConstKnowledgeInfoContext implements DustEntityWrapper {
		Self, Message, Block;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}
	
	
	enum DustLinkKnowledgeInfoEntity implements DustLinkWrapper {
		Services, PrimaryType;
		
		private final LinkWrapper lw = new LinkWrapper(this);

		@Override
		public DustEntity entity() {
			return lw.entity();
		}

		@Override
		public DustLink link() {
			return lw;
		}
	}

	enum DustAttributeKnowledgeInfoEntity implements DustAttributeWrapper {
		svcImpl;
		
		private final AttributeWrapper aw = new AttributeWrapper(this);

		@Override
		public DustEntity entity() {
			return aw.entity();
		}

		@Override
		public DustAttribute attribute() {
			return aw;
		}
	}

	enum DustAttributeKnowledgeInfoIterator implements DustAttributeWrapper {
		index, key;
		
		private final AttributeWrapper aw = new AttributeWrapper(this);

		@Override
		public DustEntity entity() {
			return aw.entity();
		}

		@Override
		public DustAttribute attribute() {
			return aw;
		}
	}
	
	enum DustLinkKnowledgeInfoIterator implements DustLinkWrapper {
		Cardinality;
		
		private final LinkWrapper lw = new LinkWrapper(this);

		@Override
		public DustEntity entity() {
			return lw.entity();
		}

		@Override
		public DustLink link() {
			return lw;
		}
	}
	
	enum DustAttributeKnowledgeInfoVariant implements DustAttributeWrapper {
		varType, value;
		
		private final AttributeWrapper aw = new AttributeWrapper(this);

		@Override
		public DustEntity entity() {
			return aw.entity();
		}

		@Override
		public DustAttribute attribute() {
			return aw;
		}
	}

	enum DustTypeKnowledgeInfo implements DustEntityWrapper {
		Entity, Link, Tag, Tagged, Variant, Iterator;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

	}

	interface DustKnowledgeInfoSource {
		boolean dustKnowledgeInfoSourceIsTypeSupported(DustEntity eType);
		
		DustEntity dustKnowledgeInfoSourceGet(DustEntity type, String idStore) throws Exception;
		void dustKnowledgeInfoSourceFind(DustEntity type, DustEntity expression, DustEntity processor) throws Exception;
		void dustKnowledgeInfoSourceDestruct(DustEntity entity) throws Exception;
	}
}
