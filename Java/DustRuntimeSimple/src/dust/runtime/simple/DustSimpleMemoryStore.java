package dust.runtime.simple;

import dust.gen.base.DustBaseServices;
import dust.pub.DustRuntimeComponents;

public class DustSimpleMemoryStore implements DustSimpleRuntimeComponents, DustBaseServices, DustBaseServices.DustBaseSource, DustRuntimeComponents.DustDataContainer {

	DustBaseSource parent;
	
	@Override
	public DustEntity getEntity(DustContext root, DustField... path) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public <ValType> ValType getFieldValue(DustEntity entity, DustField field) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFieldValue(DustEntity entity, DustField field, Object value) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public boolean dustSourceIsTypeSupported(String type) {
		return true;
	}

	@Override
	public DustEntity dustSourceGet(String type, String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dustSourceFind(String type, DustEntity expression) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dustSourceDestruct(DustEntity entity) throws Exception {
		// TODO Auto-generated method stub
		
	}
	

}
