package dust.gen.dust.core.exec;

public interface DustCoreExecServices extends DustCoreExecComponents {
	
	interface DustCoreExecBlockProcessor {
		void dustCoreExecBlockProcessorBegin() throws Exception;
		void dustCoreExecBlockProcessorEnd(DustConstCoreExecVisitorResponse lastResp, Exception optException) throws Exception;
	}

	interface DustCoreExecVisitor {
		DustConstCoreExecVisitorResponse dustDustCoreExecVisitorVisit(DustEntity entity) throws Exception;
	}
}
