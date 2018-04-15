package dust.gen.runtime.environment;

import dust.gen.knowledge.proc.DustKnowledgeProcServices;

public interface DustRuntimeEnvironmentServices extends DustRuntimeEnvironmentComponents, DustKnowledgeProcServices {

	interface DustRuntimeEnvironmentManager extends DustKnowledgeProcProcessor {
		<ValType> ValType dustRuntimeEnvironmentManagerGetAttrValue(DustEntity entity, DustEntity field);
		void dustRuntimeEnvironmentManagerSetAttrValue(DustEntity entity, DustEntity field, Object value);
	
		DustEntity dustRuntimeEnvironmentManagerGetRefEntity(DustEntity entity, boolean createIfMissing, DustEntity linkDef, Object key);
		void dustRuntimeEnvironmentManagerProcessRefs(DustKnowledgeProcVisitor proc, DustEntity root, DustLink... path);
		DustEntity dustRuntimeEnvironmentManagerModifyRefs(DustConstKnowledgeInfoLinkCommand refCmd, DustEntity left, DustEntity right, DustEntity linkDef,
				Object... params);
	
		void dustRuntimeEnvironmentManagerSend(DustEntity msg);
	}
	
}
