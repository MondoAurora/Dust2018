package dust.pub;

public interface DustComponents {

	interface DustEntity {
	}

	enum DustRefCommand {
		Add, Replace, Remove, ChangeKey;
	}
	
	interface DustRefVisitor {
		boolean dustRefVisit(DustEntity entity) throws Exception;
	}

	interface Creator<RetType> {
		RetType create(Object... params);
	}

	enum DustConstInfoPub implements DustEntity {
		ErrorClassNotFound, ErrorClassInstantiation, ErrorShutdownFailure, ErrorVistorExecution;
	}
}
