package dust.gen.runtime.binding;

import dust.gen.DustUtilsGen.AttributeWrapper;
import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.DustUtilsGen.LinkWrapper;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeBindingComponents extends DustKnowledgeInfoComponents, DustKnowledgeProcComponents {
	
	enum DustConstRuntimeBinding implements DustEntityWrapper {
		ErrorMethodAccess;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}
	
	enum DustAttributeRuntimeBindingLogicAssignment implements DustAttributeWrapper {
		javaClass;
		
		private final AttributeWrapper aw = new AttributeWrapper(this);

		@Override
		public DustEntity entity() {
			return aw.entity();
		}

		@Override
		public DustAttribute attribute() {
			return aw;
		}
	}

	enum DustLinkRuntimeBindingLogicAssignment implements DustLinkWrapper {
		Service;
		
		private final LinkWrapper lw = new LinkWrapper(this);

		@Override
		public DustEntity entity() {
			return lw.entity();
		}

		@Override
		public DustLink link() {
			return lw;
		}
	}

	enum DustLinkRuntimeBindingManager implements DustLinkWrapper {
		LogicAssignments;
		private final LinkWrapper lw = new LinkWrapper(this);

		@Override
		public DustEntity entity() {
			return lw.entity();
		}

		@Override
		public DustLink link() {
			return lw;
		}
	}
	
	enum DustTypeRuntimeBinding {
		LogicAssignment,
		Manager,
		;
		
	}
	
}
