package dust.gen.base;
import dust.pub.DustComponents;

public interface DustBaseServices extends DustComponents {
	
	interface DustBaseSource {
		boolean dustSourceIsTypeSupported(String type);
		
		DustEntity dustSourceGet(String type, String srcId, String revId) throws Exception;
		void dustSourceFind(String type, DustEntity expression, DustEntity processor) throws Exception;
		void dustSourceDestruct(DustEntity entity) throws Exception;
	}


	interface DustBaseFilter {
		boolean dustFilterMatch(DustEntity entity) throws Exception;
	}

	interface DustBaseProcessor {
		void dustProcessorProcess(DustEntity entity) throws Exception;
	}

}
