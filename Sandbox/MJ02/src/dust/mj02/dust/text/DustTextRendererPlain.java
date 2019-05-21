package dust.mj02.dust.text;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustDataComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsJava;

public class DustTextRendererPlain implements DustTextComponents, DustDataComponents, DustProcComponents, DustProcComponents.DustProcPocessor,
        DustProcComponents.DustProcEvaluator {

    StringBuilder sbContent;

    @Override
    public void processorProcess() throws Exception {
        String txt = DustUtils.getMsgVal(DustTextAtts.TextSpanString, false);

        if (!DustUtilsJava.isEmpty(txt)) {
            sbContent.append(txt);
        }
    }

    @Override
    public Object evaluatorEvaluate() throws Exception {
        sbContent = new StringBuilder();

        DustEntity eMsgCallback = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);
        DustUtils.accessEntity(DataCommand.setRef, eMsgCallback, DustDataLinks.MessageCommand, DustProcMessages.ProcessorProcess);
        
        DustEntity eMsgRelay = DustUtils.accessEntity(DataCommand.cloneEntity, ContextRef.msg);
        DustUtils.accessEntity(DataCommand.setRef, eMsgRelay, DustDataLinks.MessageCommand, DustProcMessages.ProcessorProcess);

        DustEntity eSelf = DustUtils.getCtxVal(ContextRef.self, null, false);
        DustUtils.accessEntity(DataCommand.setRef, eMsgRelay, DustTextLinks.TextRenderContextTarget, eSelf);
        DustUtils.accessEntity(DataCommand.setRef, eMsgRelay, DustTextLinks.TextRenderContextMessage, eMsgCallback);
        
        DustEntity eRoot = DustUtils.getCtxVal(ContextRef.self, DustTextLinks.TextRendererRoot, true);

        DustUtils.accessEntity(DataCommand.tempSend, eRoot, eMsgRelay);

        return sbContent.toString();
    }

}
