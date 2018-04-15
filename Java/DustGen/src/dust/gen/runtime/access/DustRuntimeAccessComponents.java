package dust.gen.runtime.access;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeAccessComponents extends DustKnowledgeInfoComponents, DustKnowledgeProcComponents {
	
	enum DustStatusRuntimeAccess implements DustEntity {
		AccessDenied;
	}
	
	enum DustConstRuntimeAccessAccessMode {
		Check, Read, Write, Execute;
	}
	
}
