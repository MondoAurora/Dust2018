package dust.gen.dust.tools.persistence;

import dust.gen.dust.core.exec.DustCoreExecServices;
import dust.gen.dust.tools.generic.DustToolsGenericServices;

public interface DustToolsPersistenceServices extends DustToolsPersistenceComponents, DustCoreExecServices, DustToolsGenericServices {

	interface DustToolsPersistenceStore extends DustCoreExecBlockProcessor, DustToolsGenericInitable {
	}

}
