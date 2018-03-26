package dust.pub;

import dust.gen.dust.core.data.DustCoreDataComponents;
import dust.gen.dust.core.exec.DustCoreExecComponents;

public interface DustPubComponents extends DustCoreDataComponents, DustCoreExecComponents {
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
			return DustTypeCoreExec.Status;
		}
	}

}
