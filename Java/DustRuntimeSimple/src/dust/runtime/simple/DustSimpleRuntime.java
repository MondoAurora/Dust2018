package dust.runtime.simple;

import dust.pub.DustRuntimeComponents;
import dust.pub.DustUtilsDev;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustRuntimeComponents.DustRuntime {

	@Override
	public void init(DustConfig config) throws Exception {
		DustUtilsDev.dump("Initializing runtime", this.getClass());
	}

	
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
	public void send(DustEntity msg) {
		// TODO Auto-generated method stub
		
	}


}
