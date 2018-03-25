package dust.pub;

import dust.gen.dust.core.data.DustCoreDataServices;
import dust.gen.dust.core.exec.DustCoreExecServices;
import dust.gen.dust.core.runtime.DustCoreRuntimeServices;

public class Dust implements DustCoreDataServices, DustCoreRuntimeServices, DustCoreExecServices {
	
	protected static DustCoreRuntimeManager RUNTIME;

	public static <ValType> ValType getAttrValue(DustEntity entity, DustAttribute field) {
		return RUNTIME.dustCoreRuntimeManagerGetAttrValue(entity, field);
	}

	public static void setAttrValue(DustEntity entity, DustAttribute field, Object value) {
		RUNTIME.dustCoreRuntimeManagerSetAttrValue(entity, field, value);
	}
	
	public static void processRefs(DustCoreExecVisitor proc, DustEntity root, DustLink... path) {
		RUNTIME.dustCoreRuntimeManagerProcessRefs(proc, root, path);
	}

	public static DustEntity getRefEntity(DustEntity entity, boolean createIfMissing, DustLink linkDef, Object key) {
		return RUNTIME.dustCoreRuntimeManagerGetRefEntity(entity, createIfMissing, linkDef, key);
	}

	public static DustEntity modifyRefs(DustConstCoreDataLinkCommand refCmd, DustEntity left, DustEntity right, DustLink linkDef, Object... params) {
		return RUNTIME.dustCoreRuntimeManagerModifyRefs(refCmd, left, right, linkDef, params);
	}
	
	public static void send(DustEntity msg) {
		RUNTIME.dustCoreRuntimeManagerSend(msg);
	}
}
