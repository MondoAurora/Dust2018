package dust.gen.tools.persistence;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents.DustTypeKnowledgeProc;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustToolsPersistenceComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents {
	enum DustStatusToolsPersistence implements DustEntity {
		HandlerInvalid, HandlerVersionMismatch;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeProc.Status;
		}
	}
	
	enum DustCommandToolsPersistenceStore implements DustCommand {
		Read;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustServiceToolsPersistence.Store;
		}
	}

	enum DustServiceToolsPersistence implements DustService {
		Store(),;
		final DustService[] extServices;
		
		private DustServiceToolsPersistence(DustService... extServices) {
			this.extServices = extServices;
		}
		@Override
		public DustService[] getExtends() {
			return extServices;
		}
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Service;
		}
	}

}
