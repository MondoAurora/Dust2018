package dust.gen.base;
import dust.pub.DustComponents;

public interface DustBaseServices extends DustComponents {
	
	interface DustBaseSource {
		boolean dustSourceIsTypeSupported(String type) ;
		DustEntity dustSourceGet(String type, String id) throws Exception;
		void dustSourceFind(String type, DustEntity expression) throws Exception;
		void dustSourceDestruct(DustEntity entity) throws Exception;
	}

}
