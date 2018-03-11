package dust.runtime.simple;

import dust.pub.DustRuntimeComponents;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustRuntimeComponents.DustRuntime {
	
	DustSimpleContext ctxRoot;
	DustBinaryManager binMgr;

	@Override
	public void init(DustConfig config) throws Exception {
	}
	
	@Override
	public void setBinaryManager(DustBinaryManager binMgr) {
		this.binMgr = binMgr;
	}

	
	@Override
	public DustEntity getEntity(DustContext root, DustAttrDef... path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <ValType> ValType getAttrValue(DustEntity entity, DustAttrDef field) {
		return ((SimpleEntity)entity).getFieldValue(field);
	}

	@Override
	public void setAttrValue(DustEntity entity, DustAttrDef field, Object value) {
		((SimpleEntity)entity).setFieldValue(field, value);
	}

	@Override
	public void send(DustEntity msg) {
		// TODO Auto-generated method stub
		
	}


}
