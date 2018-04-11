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

	interface DustRuntimeEnvironmentManager extends DustKnowledgeProcProcessor {
		<ValType> ValType dustRuntimeEnvironmentManagerGetAttrValue(DustEntity entity, DustAttribute field);
		void dustRuntimeEnvironmentManagerSetAttrValue(DustEntity entity, DustAttribute field, Object value);
	
		DustEntity dustRuntimeEnvironmentManagerGetRefEntity(DustEntity entity, boolean createIfMissing, DustLink linkDef, Object key);
		void dustRuntimeEnvironmentManagerProcessRefs(DustKnowledgeProcVisitor proc, DustEntity root, DustLink... path);
		DustEntity dustRuntimeEnvironmentManagerModifyRefs(DustConstKnowledgeInfoLinkCommand refCmd, DustEntity left, DustEntity right, DustLink linkDef,
				Object... params);
	
		void dustRuntimeEnvironmentManagerSend(DustEntity msg);
	}
}
