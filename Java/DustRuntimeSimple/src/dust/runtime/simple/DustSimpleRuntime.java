package dust.runtime.simple;

import dust.pub.DustBootComponents;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustBootComponents.DustRuntime {
	DustSimpleMetaManager metaMgr;
	DustSimpleContext ctxRoot;
	DustBinaryManager binMgr;
	
	public DustSimpleRuntime() {
		metaMgr = new DustSimpleMetaManager();
	}

	@Override
	public void init(DustConfig config) throws Exception {
	}
	
	@Override
	public void setBinaryManager(DustBinaryManager binMgr) {
		this.binMgr = binMgr;
	}
	
	@Override
	public void dustBaseBlockProcessorBegin() throws Exception {
	}
	
	@Override
	public void dustBaseBlockProcessorEnd(DustBaseVisitorResponse lastResp, Exception optException) throws Exception {
	}

	@Override
	public <ValType> ValType getAttrValue(DustBaseEntity entity, DustBaseAttributeDef field) {
		return ((SimpleEntity)entity).getFieldValue(field);
	}

	@Override
	public void setAttrValue(DustBaseEntity entity, DustBaseAttributeDef field, Object value) {
		((SimpleEntity)entity).setFieldValue(field, value);
	}

	@Override
	public void send(DustBaseEntity msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processRefs(DustBaseVisitor proc, DustBaseEntity root, DustBaseLinkDef... path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DustBaseEntity modifyRefs(DustBaseLinkCommand refCmd, DustBaseEntity left, DustBaseEntity right, DustBaseLinkDef linkDef,
			Object... params) {
		// TODO Auto-generated method stub
		return null;
	}


}
