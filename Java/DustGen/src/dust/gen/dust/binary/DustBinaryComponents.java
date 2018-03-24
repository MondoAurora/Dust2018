package dust.gen.dust.binary;

import dust.gen.dust.DustComponents;
import dust.gen.dust.base.DustBaseComponents;

public interface DustBinaryComponents extends DustComponents, DustBaseComponents {
	
	enum DustMessageBinary implements DustEntity {
		;
		@Override
		public DustType getType() {
			return DustBaseTypes.StatusInfo;
		}
	}
	
	enum DustAttributeBinaryLogicAssignment implements DustAttribute {
		javaClass;
		@Override
		public DustType getType() {
			return DustTypeBinary.LogicAssignment;
		}
	}

	enum DustLinkBinaryLogicAssignment implements DustLink {
		Service;
		@Override
		public DustType getType() {
			return DustTypeBinary.LogicAssignment;
		}
	}

	enum DustLinkBinaryManager implements DustLink {
		LogicAssignments;
		@Override
		public DustType getType() {
			return DustTypeBinary.Manager;
		}
	}
	
	enum DustTypeBinary implements DustType {
		LogicAssignment,
		Manager,
		;
		
	}
	
}
