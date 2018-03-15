package dust.pub;

import dust.gen.dust.base.DustBaseComponents;

public class Dust implements DustBaseComponents {
	
	protected static DustRuntime RUNTIME;

	public static <ValType> ValType getAttrValue(DustBaseEntity entity, DustBaseAttribute field) {
		return RUNTIME.getAttrValue(entity, field);
	}

	public static void setAttrValue(DustBaseEntity entity, DustBaseAttribute field, Object value) {
		RUNTIME.setAttrValue(entity, field, value);
	}
	
	public static void processRefs(DustBaseVisitor proc, DustBaseEntity root, DustBaseLink... path) {
		RUNTIME.processRefs(proc, root, path);
	}

	public static DustBaseEntity modifyRefs(DustBaseLinkCommand refCmd, DustBaseEntity left, DustBaseEntity right, DustBaseLink linkDef, Object... params) {
		return RUNTIME.modifyRefs(refCmd, left, right, linkDef, params);
	}
	
	public static void send(DustBaseEntity msg) {
		RUNTIME.send(msg);
	}
}
