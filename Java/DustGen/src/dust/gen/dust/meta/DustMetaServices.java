package dust.gen.dust.meta;

public interface DustMetaServices extends DustMetaComponents {
	
	interface DustMetaManager {
		void registerUnit(String typeClass, String serviceClass) throws Exception;
	}

}
