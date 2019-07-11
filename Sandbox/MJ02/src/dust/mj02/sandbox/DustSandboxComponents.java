package dust.mj02.sandbox;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustSandboxComponents
		extends DustKernelComponents {
	
    enum DustSandboxTypes implements DustEntityKey {
        SandboxFinder, SandboxSrcGen
    };

    enum DustSandboxAtts implements DustEntityKey {
        SandboxSrcGenFileNameTemplate
    };

    enum DustSandboxLinks implements DustEntityKey {
        SandboxFinderPath, SandboxFinderEntity, SandboxSrcGenUnits, SandboxSrcGenRenderer
    };


	enum DustSandboxServices implements DustEntityKey {
		SandboxChangeDump, SandboxFinder, SandboxSrcGen
	};

}
