package dust.gen.runtime.binding;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeBindingComponents extends DustKnowledgeInfoComponents, DustKnowledgeProcComponents {
	
	enum DustStatusRuntimeBinding implements DustEntity {
		ErrorMethodAccess
		;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeProc.Status;
		}
	}
	
	enum DustAttributeRuntimeBindingLogicAssignment implements DustAttribute {
		javaClass;
		@Override
		public DustType getType() {
			return DustTypeRuntimeBinding.LogicAssignment;
		}
	}

	enum DustLinkRuntimeBindingLogicAssignment implements DustLink {
		Service;
		@Override
		public DustType getType() {
			return DustTypeRuntimeBinding.LogicAssignment;
		}
	}

	enum DustLinkRuntimeBindingManager implements DustLink {
		LogicAssignments;
		@Override
		public DustType getType() {
			return DustTypeRuntimeBinding.Manager;
		}
	}
	
	enum DustTypeRuntimeBinding implements DustType {
		LogicAssignment,
		Manager,
		;
		
	}
	
}
