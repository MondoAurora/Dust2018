package dust.mj02.sandbox;

import dust.mj02.dust.DustComponents;
import dust.mj02.dust.knowledge.DustCommComponents;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;

public interface DustSandboxComponents
		extends DustComponents, DustMetaComponents, DustProcComponents, DustGenericComponents, DustCommComponents {
	
	enum DustSandboxServices implements DustEntityKey {
		SandboxChangeDump
	};

}
