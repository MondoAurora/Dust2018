package dust.mj02.dust.text;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;

public class DustTextGen implements DustTextComponents, DustProcComponents, DustMetaComponents, DustGenericComponents {

    private static boolean inited = false;

    public static void init() {
        if (!inited) {

            DustUtils.registerService(DustTextCoreServices.TextSource.class, false, DustTextServices.TextSource, DustProcServices.Processor);
            DustUtils.accessEntity(DataCommand.setRef, DustTextTypes.TextStatement, DustMetaLinks.TypeLinkedServices, DustTextServices.TextSource);
            DustUtils.accessEntity(DataCommand.setRef, DustTextTypes.TextSpan, DustMetaLinks.TypeLinkedServices, DustTextServices.TextSource);
            
            DustUtils.registerService(DustTextCoreServices.RendererPlain.class, false, DustTextServices.TextRendererPlain, DustProcServices.Processor, DustProcServices.Evaluator);
            DustUtils.accessEntity(DataCommand.setRef, DustTextTypes.TextRenderer, DustMetaLinks.TypeLinkedServices, DustTextServices.TextRendererPlain);
            
            DustUtils.registerService(DustTextCoreServices.AttToText.class, false, DustTextServices.TextAttToText, DustProcServices.Processor);
            DustUtils.accessEntity(DataCommand.setRef, DustTextTypes.TextAttToText, DustMetaLinks.TypeLinkedServices, DustTextServices.TextAttToText);
            DustUtils.accessEntity(DataCommand.setRef, DustTextTypes.TextAttToText, DustGenericLinks.ConnectedRequires, DustGenericTypes.ContextAware);
            DustUtils.accessEntity(DataCommand.setRef, DustTextTypes.TextAttToText, DustGenericLinks.ConnectedRequires, DustGenericTypes.Reference);

            inited = true;
        }
    }
}
