package dust.mj02.sandbox;

import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsDev;

public class DustSandboxSrcGen implements DustSandboxComponents, DustProcComponents.DustProcPocessor {

    @Override
    public void processorProcess() throws Exception {
        DustUtilsDev.dump("Now generating source codes...");
    }

}
