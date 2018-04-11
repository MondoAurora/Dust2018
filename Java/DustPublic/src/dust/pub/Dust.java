package dust.pub;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.runtime.environment.DustRuntimeEnvironmentComponents;

public class Dust implements DustKnowledgeInfoComponents, DustRuntimeEnvironmentComponents, DustKnowledgeProcComponents {
	
	protected static DustRuntimeEnvironmentManager RUNTIME;

	public static <ValType> ValType getAttrValue(DustEntity entity, DustAttribute field) {
		return RUNTIME.dustRuntimeEnvironmentManagerGetAttrValue(entity, field);
	}

	public static void setAttrValue(DustEntity entity, DustAttribute field, Object value) {
		RUNTIME.dustRuntimeEnvironmentManagerSetAttrValue(entity, field, value);
	}
	
	public static void processRefs(DustKnowledgeProcVisitor proc, DustEntity root, DustLink... path) {
		RUNTIME.dustRuntimeEnvironmentManagerProcessRefs(proc, root, path);
	}

	public static DustEntity getRefEntity(DustEntity entity, boolean createIfMissing, DustLink linkDef, Object key) {
		return RUNTIME.dustRuntimeEnvironmentManagerGetRefEntity(entity, createIfMissing, linkDef, key);
	}

	public static DustEntity modifyRefs(DustConstKnowledgeInfoLinkCommand refCmd, DustEntity left, DustLink linkDef, DustEntity right, Object... params) {
		return RUNTIME.dustRuntimeEnvironmentManagerModifyRefs(refCmd, left, right, linkDef, params);
	}
	
	public static void send(DustEntity msg) {
		RUNTIME.dustRuntimeEnvironmentManagerSend(msg);
	}
}
