package dust.gen.knowledge.proc;

import dust.gen.DustGenComponents;
import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents;

public interface DustKnowledgeProcComponents extends DustGenComponents, DustKnowledgeInfoComponents {

	enum DustConstKnowledgeProcVisitorResponse {
		OK, Skip, Exit, Repeat, Restart;
	}

	enum DustLinkKnowledgeProcMessage implements DustEntityLink {
		Command, Target;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

		@Override
		public void process(DustEntity entity, DustRefVisitor proc) {
			ew.process(entity, proc);
		}

		@Override
		public DustEntity get(DustEntity entity, boolean createIfMissing, Object key) {
			return ew.get(entity, createIfMissing, key);
		}

		@Override
		public DustEntity modify(DustEntity entity, DustRefCommand cmd, DustEntity target, Object key) {
			return modify(entity, cmd, target, key);
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
