package dust.gen.runtime.binding;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeBindingComponents extends DustKnowledgeInfoComponents, DustKnowledgeProcComponents {
	
	enum DustStatusRuntimeBinding implements DustEntity {
		ErrorMethodAccess
		;
	}
	
	enum DustAttributeRuntimeBindingLogicAssignment {
		javaClass;
	}

	enum DustLinkRuntimeBindingLogicAssignment {
		Service;
	}

	enum DustLinkRuntimeBindingManager {
		LogicAssignments;
	}
	
	enum DustTypeRuntimeBinding {
		LogicAssignment,
		Manager,
		;
		
	}
	
}
