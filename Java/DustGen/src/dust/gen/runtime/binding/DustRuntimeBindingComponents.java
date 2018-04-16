package dust.gen.runtime.binding;

import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeBindingComponents extends DustKnowledgeInfoComponents, DustKnowledgeProcComponents {
	
	enum DustStatusRuntimeBinding implements DustEntityWrapper {
		ErrorMethodAccess;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}
	
	enum DustAttributeRuntimeBindingLogicAssignment implements DustEntityAttribute {
		javaClass;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

		@Override
		public <ValType> ValType getValue(DustEntity entity) {
			return ew.getValue(entity);
		}

		@Override
		public void setValue(DustEntity entity, Object value) {
			ew.setValue(entity, value);
		}
	}

	enum DustLinkRuntimeBindingLogicAssignment implements DustEntityLink {
		Service;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

		@Override
		public void process(DustEntity entity, DustRefVisitor proc) {
			ew.process(entity, proc);
		}

		@Override
		public DustEntity get(DustEntity entity, boolean createIfMissing, Object key) {
			return ew.get(entity, createIfMissing, key);
		}

		@Override
		public DustEntity modify(DustEntity entity, DustRefCommand cmd, DustEntity target, Object key) {
			return modify(entity, cmd, target, key);
		}
	}

	enum DustLinkRuntimeBindingManager implements DustEntityLink {
		LogicAssignments;
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

		@Override
		public void process(DustEntity entity, DustRefVisitor proc) {
			ew.process(entity, proc);
		}

		@Override
		public DustEntity get(DustEntity entity, boolean createIfMissing, Object key) {
			return ew.get(entity, createIfMissing, key);
		}

		@Override
		public DustEntity modify(DustEntity entity, DustRefCommand cmd, DustEntity target, Object key) {
			return modify(entity, cmd, target, key);
		}
	}
	
	enum DustTypeRuntimeBinding {
		LogicAssignment,
		Manager,
		;
		
	}
	
}
