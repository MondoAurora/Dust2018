package dust.gen.runtime.environment;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeEnvironmentComponents extends DustKnowledgeInfoComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustStatusRuntimeEnvironment implements DustEntity {
		LinkCreationError, MessageSendError;

		@Override
		public DustType getType() {
			return DustTypeKnowledgeProc.Status;
		}
	}

	enum DustLinkRuntimeEnvironmentManager implements DustLink {
		InitMessage, BinaryManager, MetaManager;

		@Override
		public DustType getType() {
			return DustTypeRuntimeEnvironment.Manager;
		}
	}

	enum DustTypeRuntimeEnvironment implements DustType {
		Manager
	}
}
