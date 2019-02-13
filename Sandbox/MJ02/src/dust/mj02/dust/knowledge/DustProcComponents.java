package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	interface DustProcInitable {
		public void dustProcInitableInit() throws Exception;
	}
	
	interface DustProcChangeListener {
		public void dustProcChangedAttribute(DustEntity entity, DustEntity att, Object value) throws Exception;
		public void dustProcChangedRef(DustEntity entity, DustRef ref, DataCommand cmd) throws Exception;
	}
	
}
