package dust.mj02.sandbox;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustSandboxComponents
		extends DustKernelComponents {
	
	enum DustSandboxUnits implements DustEntityKey {
		DustSandbox
	};


	enum DustSandboxServices implements DustEntityKey {
		SandboxChangeDump
	};

}
