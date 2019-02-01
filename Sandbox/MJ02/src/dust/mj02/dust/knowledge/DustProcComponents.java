package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	interface DustProcInitable {
		public void dustProcInitableInit() throws Exception;
	}
	
}
