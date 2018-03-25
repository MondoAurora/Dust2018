package dust.gen.dust.core.binding;

import dust.gen.dust.core.data.DustCoreDataComponents;
import dust.gen.dust.core.exec.DustCoreExecComponents;

public interface DustCoreBindingComponents extends DustCoreDataComponents, DustCoreExecComponents {
	
	enum DustStatusCoreBinding implements DustEntity {
		ErrorMethodAccess
		;
		@Override
		public DustType getType() {
			return DustTypeCoreExec.Status;
		}
	}
	
	enum DustAttributeCoreBindingLogicAssignment implements DustAttribute {
		javaClass;
		@Override
		public DustType getType() {
			return DustTypeCoreBinding.LogicAssignment;
		}
	}

	enum DustLinkCoreBindingLogicAssignment implements DustLink {
		Service;
		@Override
		public DustType getType() {
			return DustTypeCoreBinding.LogicAssignment;
		}
	}

	enum DustLinkCoreBindingManager implements DustLink {
		LogicAssignments;
		@Override
		public DustType getType() {
			return DustTypeCoreBinding.Manager;
		}
	}
	
	enum DustTypeCoreBinding implements DustType {
		LogicAssignment,
		Manager,
		;
		
	}
	
}
