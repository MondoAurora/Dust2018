package dust.gen.runtime.binding;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeBindingComponents extends DustKnowledgeInfoComponents, DustKnowledgeProcComponents {
	
	enum DustStatusRuntimeBinding implements DustEntity {
		ErrorMethodAccess
		;
	}
	
	enum DustAttributeRuntimeBindingLogicAssignment implements DustEntity {
		javaClass;
	}

	enum DustLinkRuntimeBindingLogicAssignment implements DustEntity {
		Service;
	}

	enum DustLinkRuntimeBindingManager implements DustEntity {
		LogicAssignments;
	}
	
	enum DustTypeRuntimeBinding implements DustEntity {
		LogicAssignment,
		Manager,
		;
		
	}
	
}
