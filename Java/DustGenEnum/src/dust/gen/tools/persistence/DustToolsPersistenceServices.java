package dust.gen.tools.persistence;

import dust.gen.knowledge.proc.DustKnowledgeProcServices;
import dust.gen.tools.generic.DustToolsGenericServices;

public interface DustToolsPersistenceServices extends DustToolsPersistenceComponents, DustKnowledgeProcServices, DustToolsGenericServices {

	interface DustToolsPersistenceStore extends DustKnowledgeProcProcessor, DustToolsGenericInitable {
		void dustToolsPersistenceStoreRead() throws Exception;
	}

}
