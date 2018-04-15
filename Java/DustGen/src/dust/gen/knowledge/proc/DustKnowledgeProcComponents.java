package dust.gen.knowledge.proc;

import dust.gen.DustGenComponents;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents;

public interface DustKnowledgeProcComponents extends DustGenComponents, DustKnowledgeInfoComponents {

	enum DustConstKnowledgeProcVisitorResponse {
		OK, Skip, Exit, Repeat, Restart;
	}

	enum DustLinkKnowledgeProcMessage {
		Command, Target;
	}

	enum DustTypeKnowledgeProc {
		Message, Status;
	}

	
	
	enum DustCommandKnowledgeProcVisitor {
		Visit;
	}

	enum DustCommandKnowledgeProcProcessor {
		Begin, End;
	}

	enum DustServiceKnowledgeProc {
		Visitor, Processor;
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
