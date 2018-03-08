package dust.runtime.simple;

import dust.pub.DustRuntimeComponents;
import dust.pub.DustUtilsDev;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustRuntimeComponents.DustRuntime {
	
	DustSimpleContext ctxRoot;
	DustBinaryManager binMgr;

	@Override
	public void init(DustConfig config) throws Exception {
		DustUtilsDev.dump("Initializing runtime", this.getClass());
	}
	
	@Override
	public void setBinaryManager(DustBinaryManager binMgr) {
		this.binMgr = binMgr;
	}

	
	@Override
	public DustEntity getEntity(DustContext root, DustField... path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <ValType> ValType getFieldValue(DustEntity entity, DustField field) {
		return ((SimpleEntity)entity).getFieldValue(field);
	}

	@Override
	public void setFieldValue(DustEntity entity, DustField field, Object value) {
		((SimpleEntity)entity).setFieldValue(field, value);
	}

	@Override
	public void send(DustEntity msg) {
		// TODO Auto-generated method stub
		
	}


}
