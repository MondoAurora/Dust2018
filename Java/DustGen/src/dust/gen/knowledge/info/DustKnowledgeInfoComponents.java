package dust.gen.knowledge.info;

import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeInfoComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents {

	enum DustConstKnowledgeInfoEntityState {
		Temporal, InSync, RefChanged, Changed, Constructed, Destructed;
	}

	enum DustConstKnowledgeInfoLinkCommand {
		Add, Replace, Remove, ChangeKey;
	}

	enum DustConstKnowledgeInfoVisitorResponse {
		OK, Skip, Exit, Repeat, Restart;
	}

	enum DustConstKnowledgeInfoContext implements DustEntityWrapper {
		Self, Message, Block;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}
	
	
	enum DustLinkKnowledgeInfoEntity implements DustEntityLink {
		Services, PrimaryType;
		
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
			return ew.modify(entity, cmd, target, key);
		}
	}

	enum DustAttributeKnowledgeInfoEntity implements DustEntityAttribute {
		svcImpl;
		
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

	enum DustAttributeKnowledgeInfoIterator implements DustEntityAttribute {
		index, key;
		
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
	
	enum DustLinkKnowledgeInfoIterator implements DustEntityLink {
		Cardinality;
		
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
			return ew.modify(entity, cmd, target, key);
		}
	}
	
	enum DustAttributeKnowledgeInfoVariant implements DustEntityAttribute {
		varType, value;
		
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

	enum DustTypeKnowledgeInfo implements DustEntityWrapper {
		Entity, Link, Tag, Tagged, Variant, Iterator;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

	}

	interface DustKnowledgeInfoSource {
		boolean dustKnowledgeInfoSourceIsTypeSupported(DustEntity eType);
		
		DustEntity dustKnowledgeInfoSourceGet(DustEntity type, String idStore) throws Exception;
		void dustKnowledgeInfoSourceFind(DustEntity type, DustEntity expression, DustEntity processor) throws Exception;
		void dustKnowledgeInfoSourceDestruct(DustEntity entity) throws Exception;
	}
}
