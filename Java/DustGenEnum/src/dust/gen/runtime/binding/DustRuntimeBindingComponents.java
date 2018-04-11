package dust.gen.runtime.binding;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeBindingComponents extends DustKnowledgeInfoComponents, DustKnowledgeProcComponents {
	
	enum DustStatusRuntimeBinding implements DustEntity {
		ErrorMethodAccess
		;
	}
	
	enum DustAttributeRuntimeBindingLogicAssignment implements DustAttribute {
		javaClass;
	}

	enum DustLinkRuntimeBindingLogicAssignment implements DustLink {
		Service;
	}

	enum DustLinkRuntimeBindingManager implements DustLink {
		LogicAssignments;
	}
	
	enum DustTypeRuntimeBinding implements DustType {
		LogicAssignment,
		Manager,
		;
		
	}
	
}
