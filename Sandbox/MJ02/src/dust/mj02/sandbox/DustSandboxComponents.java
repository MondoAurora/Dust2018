package dust.mj02.sandbox;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustSandboxComponents
		extends DustKernelComponents {
	
	enum DustSandboxUnits implements DustEntityKey {
		DustSandbox
    };
    
    enum DustSandboxTypes implements DustEntityKey {
        SandboxFinder
    };

    enum DustSandboxLinks implements DustEntityKey {
        SandboxFinderPath, SandboxFinderEntity
    };


	enum DustSandboxServices implements DustEntityKey {
		SandboxChangeDump, SandboxFinder
	};

}
