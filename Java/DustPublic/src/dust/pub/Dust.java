package dust.pub;

public class Dust implements DustComponents {
	
	public interface DustEnvironment {
		void launch();
		void shutdown();
		
		DustEntity getEntity(DustEntity type, String storeId, String revision);
		<ValType> ValType getAttrValue(DustEntity entity, DustEntity field);
		void setAttrValue(DustEntity entity, DustEntity field, Object value);
		void processRefs(DustRefVisitor proc, DustEntity root, DustEntity ref);
		DustEntity getRefEntity(DustEntity entity, boolean createIfMissing, DustEntity linkDef, Object key);
		DustEntity modifyRefs(DustRefCommand refCmd, DustEntity left, DustEntity linkDef, DustEntity right, Object key);
		void send(DustEntity msg);
	}

	
	protected static DustEnvironment RUNTIME;
	

	public static DustEntity getEntity(DustEntity type, String storeId, String revision) {
		return RUNTIME.getEntity(type, storeId, revision);
	}


	public static <ValType> ValType getAttrValue(DustEntity entity, DustEntity field) {
		return RUNTIME.getAttrValue(entity, field);
	}

	public static void setAttrValue(DustEntity entity, DustEntity field, Object value) {
		RUNTIME.setAttrValue(entity, field, value);
	}
	
	public static void processRefs(DustRefVisitor proc, DustEntity root, DustEntity ref) {
		RUNTIME.processRefs(proc, root, ref);
	}

	public static DustEntity getRefEntity(DustEntity entity, boolean createIfMissing, DustEntity linkDef, Object key) {
		return RUNTIME.getRefEntity(entity, createIfMissing, linkDef, key);
	}

	public static DustEntity modifyRefs(DustRefCommand refCmd, DustEntity left, DustEntity linkDef, DustEntity right, Object... params) {
		return RUNTIME.modifyRefs(refCmd, left, right, linkDef, params);
	}
	
	public static void send(DustEntity msg) {
		RUNTIME.send(msg);
	}
}
