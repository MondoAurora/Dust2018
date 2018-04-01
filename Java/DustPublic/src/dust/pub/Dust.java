package dust.pub;

import dust.gen.knowledge.info.DustKnowledgeInfoServices;
import dust.gen.knowledge.proc.DustKnowledgeProcServices;
import dust.gen.runtime.environment.DustRuntimeEnvironmentServices;

public class Dust implements DustKnowledgeInfoServices, DustRuntimeEnvironmentServices, DustKnowledgeProcServices {
	
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

	public static DustEntity modifyRefs(DustConstKnowledgeInfoLinkCommand refCmd, DustEntity left, DustEntity right, DustLink linkDef, Object... params) {
		return RUNTIME.dustRuntimeEnvironmentManagerModifyRefs(refCmd, left, right, linkDef, params);
	}
	
	public static void send(DustEntity msg) {
		RUNTIME.dustRuntimeEnvironmentManagerSend(msg);
	}
}
