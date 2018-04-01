package dust.gen.dust.core.comm;

import dust.gen.dust.core.exec.DustCoreExecServices;

public interface DustCoreCommServices extends DustCoreCommComponents, DustCoreExecServices {

	interface DustCoreCommAgent extends DustCoreExecProcessor, DustCoreExecVisitor {
	}

}
