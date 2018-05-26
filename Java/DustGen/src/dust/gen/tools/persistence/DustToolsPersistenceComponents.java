package dust.gen.tools.persistence;

import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustToolsPersistenceComponents extends DustToolsGenericComponents, DustKnowledgeProcComponents, DustKnowledgeMetaComponents {
	enum DustConstToolsPersistence implements DustEntity {
		HandlerInvalid, HandlerVersionMismatch;
	}
	
	enum DustCommandToolsPersistenceStore {
		Read;
	}

	enum DustServiceToolsPersistence implements DustEntityWrapper {
		Store;
		
		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	interface DustToolsPersistenceStore extends DustKnowledgeProcProcessor, DustToolsGenericInitable {
		void dustToolsPersistenceStoreRead() throws Exception;
	}

}
