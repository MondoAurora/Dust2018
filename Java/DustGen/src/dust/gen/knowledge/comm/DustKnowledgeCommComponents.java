package dust.gen.knowledge.comm;

import dust.gen.DustUtilsGen.AttributeWrapper;
import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.DustUtilsGen.LinkWrapper;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeCommComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustConstKnowledgeCommStatementType implements DustEntityWrapper {
		Discussion, Entity, Model, Data;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	enum DustAttributeKnowledgeCommTerm implements DustAttributeWrapper {
		idStore, idLocal;
		
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
	
	enum DustLinkKnowledgeCommStatement implements DustLinkWrapper {
		Type;
		
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
	
	enum DustLinkKnowledgeCommAgent {
		Source;
	}


	enum DustTypeKnowledgeComm implements DustEntityWrapper {
		Term, Discussion, Statement;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}
	
	enum DustServiceKnowledgeComm implements DustEntityWrapper {
		Discussion;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	interface DustKnowledgeCommDiscussion extends DustKnowledgeProcProcessor, DustKnowledgeProcVisitor {
	}

}
