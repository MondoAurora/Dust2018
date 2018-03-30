package dust.gen.dust.core.aaa;

import dust.gen.dust.core.data.DustCoreDataComponents;
import dust.gen.dust.core.exec.DustCoreExecComponents;

public interface DustCoreAaaComponents extends DustCoreDataComponents, DustCoreExecComponents {
	
	enum DustStatusCoreAaa implements DustEntity {
		AccessDenied;
		@Override
		public DustType getType() {
			return DustTypeCoreExec.Status;
		}
	}
	
	enum DustConstCoreAaaAccessMode implements DustEntity {
		Check, Read, Write, Execute;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Const;
		}
	}
	
}
