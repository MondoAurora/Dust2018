package dust.pub;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustPubComponents extends DustKnowledgeInfoComponents, DustKnowledgeProcComponents {
	String ID_SEP = ":";
	String DEFAULT_SEPARATOR = ",";
	String MULTI_FLAG = "*";
	
	interface Creator<RetType> {
		RetType create(Object ... params);
	}
	
	enum DustStatusInfoPub implements DustEntity {
		ErrorClassNotFound, ErrorClassInstantiation, ErrorShutdownFailure, ErrorVistorExecution;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeProc.Status;
		}
	}

}
