package dust.gen.knowledge.meta;

import dust.gen.DustGenComponents;
import dust.gen.DustUtilsGen.EntityWrapper;

public interface DustKnowledgeMetaComponents extends DustGenComponents {
	
	enum DustConstKnowledgeMetaAttrType {
		Id, Int, Float, Bool, Raw;
	}

	enum DustConstKnowledgeMetaCardinality implements DustEntityWrapper {
		Single, Set, Array, Map;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	enum DustTypeKnowledgeMeta implements DustEntityWrapper {
		Unit, Type, AttDef, LinkDef, Service, Command, Const;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	enum DustAttributeKnowledgeMetaCommand {
		boundMethod;
	}

	enum DustAttributeKnowledgeMetaService {
		boundClass;
	}

}
