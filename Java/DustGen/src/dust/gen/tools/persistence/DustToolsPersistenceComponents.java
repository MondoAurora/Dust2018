package dust.gen.tools.persistence;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustToolsPersistenceComponents extends DustToolsGenericComponents, DustKnowledgeProcComponents, DustKnowledgeMetaComponents {
	enum DustStatusToolsPersistence implements DustEntity {
		HandlerInvalid, HandlerVersionMismatch;
	}
	
	enum DustCommandToolsPersistenceStore implements DustCommand {
		Read;
	}

	enum DustServiceToolsPersistence implements DustService {
		Store(DustServiceToolsGeneric.Initable, DustServiceKnowledgeProc.Processor),;
		final DustService[] extServices;
		
		private DustServiceToolsPersistence(DustService... extServices) {
			this.extServices = extServices;
		}
	}

	interface DustToolsPersistenceStore extends DustKnowledgeProcProcessor, DustToolsGenericInitable {
		void dustToolsPersistenceStoreRead() throws Exception;
	}

}
