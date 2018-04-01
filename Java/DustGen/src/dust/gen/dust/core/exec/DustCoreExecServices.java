package dust.gen.dust.core.exec;

public interface DustCoreExecServices extends DustCoreExecComponents {
	
	interface DustCoreExecProcessor {
		void dustCoreExecProcessorBegin() throws Exception;
		void dustCoreExecProcessorEnd() throws Exception;
//		void dustCoreExecProcessorEndLater(DustConstCoreExecVisitorResponse lastResp, Exception optException) throws Exception;
	}

	interface DustCoreExecVisitor {
		DustConstCoreExecVisitorResponse dustDustCoreExecVisitorVisit(DustEntity entity) throws Exception;
	}
}
