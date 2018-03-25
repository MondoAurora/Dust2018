package dust.gen.dust.core.data;

public interface DustCoreDataServices extends DustCoreDataComponents {
	
	interface DustCoreDataSource {
		boolean dustCoreDataSourceIsTypeSupported(DustEntity eType);
		
		DustEntity dustCoreDataSourceGet(DustType type, String srcId, String revId) throws Exception;
		void dustCoreDataSourceFind(DustType type, DustEntity expression, DustEntity processor) throws Exception;
		void dustCoreDataSourceDestruct(DustEntity entity) throws Exception;
	}

}
