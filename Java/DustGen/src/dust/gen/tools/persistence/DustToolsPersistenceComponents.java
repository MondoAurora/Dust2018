package dust.gen.tools.persistence;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustToolsPersistenceComponents extends DustToolsGenericComponents, DustKnowledgeProcComponents, DustKnowledgeMetaComponents {
	enum DustStatusToolsPersistence implements DustEntity {
		HandlerInvalid, HandlerVersionMismatch;
	}
	
	enum DustCommandToolsPersistenceStore {
		Read;
	}

	enum DustServiceToolsPersistence {
		Store(),;
	}

	interface DustToolsPersistenceStore extends DustKnowledgeProcProcessor, DustToolsGenericInitable {
		void dustToolsPersistenceStoreRead() throws Exception;
	}

}
