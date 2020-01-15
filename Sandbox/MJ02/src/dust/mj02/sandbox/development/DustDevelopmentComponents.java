package dust.mj02.sandbox.development;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustDevelopmentComponents  extends DustKernelComponents {
    String DEV_SRCDIR_MANUAL = "src";
    String DEV_SRCDIR_GENERATED = "gen";

    
    enum DustDevelopmentTypes implements DustEntityKey {
        Development, DevProject, DevLibrary
    };
    
    enum DustDevelopmentServices implements DustEntityKey {
        DevProject
    };
}
