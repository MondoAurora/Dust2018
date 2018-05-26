package dust.gen.knowledge.proc;

import dust.gen.DustGenComponents;
import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.DustUtilsGen.LinkWrapper;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents;

public interface DustKnowledgeProcComponents extends DustGenComponents, DustKnowledgeInfoComponents {

	enum DustConstKnowledgeProcVisitorResponse {
		OK, Skip, Exit, Repeat, Restart;
	}

	enum DustLinkKnowledgeProcMessage implements DustLinkWrapper {
		Command, Target;
		
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

	enum DustTypeKnowledgeProc {
		Message, Status;
	}

	
	
	enum DustCommandKnowledgeProcVisitor implements DustEntityWrapper {
		Visit;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	enum DustCommandKnowledgeProcProcessor implements DustEntityWrapper {
		Begin, End;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	enum DustServiceKnowledgeProc implements DustEntityWrapper {
		Visitor, Processor;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	interface DustKnowledgeProcProcessor {
			void dustKnowledgeProcProcessorBegin() throws Exception;
			void dustKnowledgeProcProcessorEnd() throws Exception;
	//		void dustKnowledgeProcProcessorEndLater(DustConstKnowledgeProcVisitorResponse lastResp, Exception optException) throws Exception;
		}

	interface DustKnowledgeProcVisitor {
		DustConstKnowledgeProcVisitorResponse dustKnowledgeProcVisitorVisit(DustEntity entity) throws Exception;
	}
}
