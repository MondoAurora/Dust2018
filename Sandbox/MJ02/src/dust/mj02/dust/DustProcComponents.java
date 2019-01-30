package dust.mj02.dust;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	interface DustProcInitable {
		public void dustProcInitableInit() throws Exception;
	}
	
}
