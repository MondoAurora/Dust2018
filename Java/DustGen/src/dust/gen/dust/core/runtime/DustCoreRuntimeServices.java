package dust.gen.dust.core.runtime;

import dust.gen.dust.core.exec.DustCoreExecServices;

public interface DustCoreRuntimeServices extends DustCoreRuntimeComponents, DustCoreExecServices {

	interface DustCoreRuntimeManager extends DustCoreExecProcessor {
		<ValType> ValType dustCoreRuntimeManagerGetAttrValue(DustEntity entity, DustAttribute field);
		void dustCoreRuntimeManagerSetAttrValue(DustEntity entity, DustAttribute field, Object value);
	
		DustEntity dustCoreRuntimeManagerGetRefEntity(DustEntity entity, boolean createIfMissing, DustLink linkDef, Object key);
		void dustCoreRuntimeManagerProcessRefs(DustCoreExecVisitor proc, DustEntity root, DustLink... path);
		DustEntity dustCoreRuntimeManagerModifyRefs(DustConstCoreDataLinkCommand refCmd, DustEntity left, DustEntity right, DustLink linkDef,
				Object... params);
	
		void dustCoreRuntimeManagerSend(DustEntity msg);
	}
	
}
