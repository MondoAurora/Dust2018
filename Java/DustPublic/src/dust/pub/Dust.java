package dust.pub;

import dust.gen.dust.base.DustBaseServices;

public class Dust implements DustBaseServices {
	
	protected static DustRuntime RUNTIME;

	public static <ValType> ValType getAttrValue(DustEntity entity, DustAttribute field) {
		return RUNTIME.getAttrValue(entity, field);
	}

	public static void setAttrValue(DustEntity entity, DustAttribute field, Object value) {
		RUNTIME.setAttrValue(entity, field, value);
	}
	
	public static void processRefs(DustBaseVisitor proc, DustEntity root, DustLink... path) {
		RUNTIME.processRefs(proc, root, path);
	}

	public static DustEntity modifyRefs(DustBaseLinkCommand refCmd, DustEntity left, DustEntity right, DustLink linkDef, Object... params) {
		return RUNTIME.modifyRefs(refCmd, left, right, linkDef, params);
	}
	
	public static void send(DustEntity msg) {
		RUNTIME.send(msg);
	}
}
