package dust.gen.knowledge.comm;

import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeCommComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustConstKnowledgeCommStatementType implements DustEntityWrapper {
		Discussion, Entity, Model, Data;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	enum DustAttributeKnowledgeCommTerm implements DustEntityAttribute {
		idStore, idLocal;
		
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
	
	enum DustLinkKnowledgeCommStatement implements DustEntityLink {
		Type;
		
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
	
	enum DustLinkKnowledgeCommAgent {
		Source;
	}


	enum DustTypeKnowledgeComm implements DustEntityWrapper {
		Term, Discussion, Statement;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}
	
	enum DustServiceKnowledgeComm implements DustEntityWrapper {
		Discussion;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	interface DustKnowledgeCommDiscussion extends DustKnowledgeProcProcessor, DustKnowledgeProcVisitor {
	}

}
