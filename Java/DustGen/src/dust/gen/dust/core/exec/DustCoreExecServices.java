package dust.gen.dust.core.exec;

public interface DustCoreExecServices extends DustCoreExecComponents {
	
//	interface DustCoreExecVisitor {
//		DustConstCoreExecVisitorResponse dustDustBaseVisitorVisit(DustEntity entity) throws Exception;
//	}
//
//	interface DustCoreExecBlockProcessor {
//		void dustBaseBlockProcessorBegin() throws Exception;
//		void dustBaseBlockProcessorEnd(DustConstCoreExecVisitorResponse lastResp, Exception optException) throws Exception;
//	}

	interface DustCoreExecBlockProcessor {
		void dustCoreExecBlockProcessorBegin() throws Exception;
		void dustCoreExecBlockProcessorEnd(DustConstCoreExecVisitorResponse lastResp, Exception optException) throws Exception;
	}

	interface DustCoreExecVisitor {
		DustConstCoreExecVisitorResponse dustDustCoreExecVisitorVisit(DustEntity entity) throws Exception;
	}
}
