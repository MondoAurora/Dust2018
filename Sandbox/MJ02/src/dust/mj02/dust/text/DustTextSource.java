package dust.mj02.dust.text;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustDataComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustCollectionComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsJava;

public class DustTextSource implements DustTextComponents, DustCollectionComponents, DustGenericComponents, DustDataComponents, DustProcComponents,
        DustProcComponents.DustProcPocessor {

    @Override
    public void processorProcess() throws Exception {
        DustEntity eSelf = DustUtils.getCtxVal(ContextRef.self, null, false);
        DustEntity eMsgCallback = DustUtils.getMsgVal(DustTextLinks.TextRenderContextMessage, true);
        DustEntity renderer = DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg, DustTextLinks.TextRenderContextTarget);

        if (!optCallbackWithSpan(eSelf, eMsgCallback, renderer)) {
            DustEntity eMsgRelay = DustUtils.getCtxVal(ContextRef.msg, null, false);

            DustUtils.accessEntity(DataCommand.processRef, eSelf, DustCollectionLinks.SequenceMembers, new RefProcessor() {
                DustEntity eMgsEval = null;

                @Override
                public void processRef(DustRef ref) {
                    DustEntity member = ref.get(RefKey.target);

                    if (optCallbackWithSpan(member, eMsgCallback, renderer)) {
                        return;
                    }

                    if (null != DustUtils.getBinary(member, DustProcServices.Evaluator)) {
                        if (null == eMgsEval) {
                            eMgsEval = DustUtils.accessEntity(DataCommand.cloneEntity, eMsgRelay);
                            DustUtils.accessEntity(DataCommand.setRef, eMgsEval, DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);
                        }

                        DustUtils.accessEntity(DataCommand.tempSend, member, eMgsEval);

                        optCallbackWithSpan(eMgsEval, eMsgCallback, renderer, DustDataAtts.MessageReturn);
                    } else {
                        DustUtils.accessEntity(DataCommand.tempSend, member, eMsgRelay);
                    }
                }
            });
        }
    }

    private boolean optCallbackWithSpan(DustEntity source, DustEntity eMsg, DustEntity renderer) {
        return optCallbackWithSpan(source, eMsg, renderer, DustTextAtts.TextSpanString);
    }

    private boolean optCallbackWithSpan(DustEntity source, DustEntity eMsg, DustEntity renderer, Object txtKey) {
        String txt = DustUtils.accessEntity(DataCommand.getValue, source, txtKey);

        if (!DustUtilsJava.isEmpty(txt)) {
            DustUtils.accessEntity(DataCommand.setValue, eMsg, DustTextAtts.TextSpanString, txt);
            DustUtils.accessEntity(DataCommand.setRef, eMsg, DustTextLinks.TextRenderContextSpanSource, source);
            DustUtils.accessEntity(DataCommand.tempSend, renderer, eMsg);
            return true;
        }

        return false;
    }

}
