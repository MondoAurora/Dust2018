package dust.mj02.dust.text;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustTextGen implements DustTextComponents, DustProcComponents, DustMetaComponents {

    private static boolean inited = false;

    public static void init() {
        if (!inited) {

            DustUtils.registerService(DustTextSource.class, false, DustTextServices.TextSource, DustProcServices.Processor);
            DustUtils.accessEntity(DataCommand.setRef, DustTextTypes.TextStatement, DustMetaLinks.TypeLinkedServices, DustTextServices.TextSource);
            DustUtils.accessEntity(DataCommand.setRef, DustTextTypes.TextSpan, DustMetaLinks.TypeLinkedServices, DustTextServices.TextSource);
            
            DustUtils.registerService(DustTextRendererPlain.class, false, DustTextServices.TextRendererPlain, DustProcServices.Processor, DustProcServices.Evaluator);
            DustUtils.accessEntity(DataCommand.setRef, DustTextTypes.TextRenderer, DustMetaLinks.TypeLinkedServices, DustTextServices.TextRendererPlain);
            
            inited = true;
        }
    }
}
