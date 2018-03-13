package dust.gen.dust.base;

public interface DustBaseServices extends DustBaseComponents {
	
	interface DustBaseSource {
		boolean dustSourceIsTypeSupported(DustBaseEntity eType);
		
		DustBaseEntity dustSourceGet(DustBaseEntity eType, String srcId, String revId) throws Exception;
		void dustSourceFind(DustBaseEntity eType, DustBaseEntity expression, DustBaseEntity processor) throws Exception;
		void dustSourceDestruct(DustBaseEntity entity) throws Exception;
	}

	interface DustBaseFilter {
		boolean dustFilterMatch(DustBaseEntity entity) throws Exception;
	}

	interface DustBaseProcessor {
		void dustProcessorProcess(DustBaseEntity entity) throws Exception;
	}

}
