package dust.gen.knowledge.proc;

public interface DustKnowledgeProcServices extends DustKnowledgeProcComponents {
	
	interface DustKnowledgeProcProcessor {
		void dustKnowledgeProcProcessorBegin() throws Exception;
		void dustKnowledgeProcProcessorEnd() throws Exception;
//		void dustKnowledgeProcProcessorEndLater(DustConstKnowledgeProcVisitorResponse lastResp, Exception optException) throws Exception;
	}

	interface DustKnowledgeProcVisitor {
		DustConstKnowledgeProcVisitorResponse dustKnowledgeProcVisitorVisit(DustEntity entity) throws Exception;
	}
}
