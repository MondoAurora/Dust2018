package dust.gen.runtime.environment;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeEnvironmentComponents
		extends DustKnowledgeInfoComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustStatusRuntimeEnvironment implements DustEntity {
		LinkCreationError, MessageSendError, GetEntityError;

	}

	enum DustLinkRuntimeEnvironmentManager {
		InitMessage, BinaryManager, MetaManager;

	}

	enum DustTypeRuntimeEnvironment {
		Manager
	}

	interface DustRuntimeEnvironmentManager extends DustKnowledgeProcProcessor {
		DustEntity dustRuntimeEnvironmentManagerGetEntity(DustEntity type, String storeId, String revision);

		<ValType> ValType dustRuntimeEnvironmentManagerGetAttrValue(DustEntity entity, DustEntity field);
		void dustRuntimeEnvironmentManagerSetAttrValue(DustEntity entity, DustEntity field, Object value);
	
		DustEntity dustRuntimeEnvironmentManagerGetRefEntity(DustEntity entity, boolean createIfMissing, DustEntity linkDef, Object key);
		void dustRuntimeEnvironmentManagerProcessRefs(DustKnowledgeProcVisitor proc, DustEntity root, DustEntity... path);
		DustEntity dustRuntimeEnvironmentManagerModifyRefs(DustConstKnowledgeInfoLinkCommand refCmd, DustEntity left, DustEntity right, DustEntity linkDef,
				Object... params);
	
		void dustRuntimeEnvironmentManagerSend(DustEntity msg);
	}
}
