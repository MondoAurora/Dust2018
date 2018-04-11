package dust.gen.runtime.environment;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeEnvironmentComponents
		extends DustKnowledgeInfoComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustStatusRuntimeEnvironment implements DustEntity {
		LinkCreationError, MessageSendError;

	}

	enum DustLinkRuntimeEnvironmentManager implements DustLink {
		InitMessage, BinaryManager, MetaManager;

	}

	enum DustTypeRuntimeEnvironment implements DustType {
		Manager
	}
}
