package dust.mj02.sandbox.development;

import dust.mj02.dust.DustUtils;

public interface DustDevelopmentUtils extends DustDevelopmentComponents {
        
    public static void init() {
        DustUtils.registerService(DustDevelopmentProject.class, false, DustDevelopmentServices.DevProject, DustProcServices.Processor);
        DustUtils.accessEntity(DataCommand.setRef, DustDevelopmentTypes.DevProject, DustMetaLinks.TypeLinkedServices, DustDevelopmentServices.DevProject);
    }

}
