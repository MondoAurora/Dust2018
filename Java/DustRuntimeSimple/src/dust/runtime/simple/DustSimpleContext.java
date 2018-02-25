package dust.runtime.simple;

import java.util.HashSet;
import java.util.Set;

import dust.gen.base.DustBaseServices;

public class DustSimpleContext implements DustSimpleRuntimeComponents, DustBaseServices, DustBaseServices.DustBaseSource {

	Set<DustBaseSource> parentSources = new HashSet<>();
	
	Set<SimpleEntity> entities = new HashSet<>();
	
	
	
	
	@Override
	public boolean dustSourceIsTypeSupported(String type) {
		return true;
	}

	@Override
	public DustEntity dustSourceGet(String type, String srcId, String revId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dustSourceFind(String type, DustEntity expression) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dustSourceDestruct(DustEntity entity) throws Exception {
		((SimpleEntity)entity).setState(DustEntityState.esDestructed);
	}
	

}
