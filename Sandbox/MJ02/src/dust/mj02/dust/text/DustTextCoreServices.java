package dust.mj02.dust.text;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.DustComponents.ContextRef;
import dust.mj02.dust.DustComponents.DustEntity;
import dust.mj02.dust.knowledge.DustDataComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustCollectionComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsJava;

public interface DustTextCoreServices
        extends DustTextComponents, DustCollectionComponents, DustGenericComponents, DustDataComponents, DustProcComponents {

    class Responder {
        DustEntity eSelf;
        DustEntity eMsg;
        DustEntity renderer;

        public Responder() {
            eSelf = DustUtils.getCtxVal(ContextRef.self, null, false);
            eMsg = DustUtils.getMsgVal(DustTextLinks.TextRenderContextMessage, true);
            renderer = DustUtils.getCtxVal(ContextRef.msg, DustTextLinks.TextRenderContextTarget, true);

            DustUtils.accessEntity(DataCommand.setRef, eMsg, DustTextLinks.TextRenderContextSpanSource, eSelf);
        }

        void send(String txt) {
            DustUtils.accessEntity(DataCommand.setValue, eMsg, DustTextAtts.TextSpanString, txt);
            DustUtils.accessEntity(DataCommand.tempSend, renderer, eMsg);
        }

        static Responder send(String txt, Responder r) {
            if (null == r) {
                r = new Responder();
            }

            r.send(txt);

            return r;
        }
    }

    public class TextSource implements DustProcComponents.DustProcPocessor {

        @Override
        public void processorProcess() throws Exception {
            DustEntity eSelf = DustUtils.getCtxVal(ContextRef.self, null, false);
            DustEntity eMsgCallback = DustUtils.getMsgVal(DustTextLinks.TextRenderContextMessage, true);
            DustEntity renderer = DustUtils.getCtxVal(ContextRef.msg, DustTextLinks.TextRenderContextTarget, true);

            if (DustUtils.hasRef(eSelf, DustDataLinks.EntityModels, DustCollectionTypes.Sequence)) {
                DustEntity eMsgRelay = DustUtils.getCtxVal(ContextRef.msg, null, false);

                DustUtils.accessEntity(DataCommand.processRef, eSelf, DustCollectionLinks.SequenceMembers, new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        DustEntity member = ref.get(RefKey.target);

                        if (!optCallbackWithSpan(member, eMsgCallback, renderer)) {
                            DustUtils.accessEntity(DataCommand.tempSend, member, eMsgRelay);
                        }
                    }
                });
            } else {
                optCallbackWithSpan(eSelf, eMsgCallback, renderer);
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

    public static class AttToText implements DustProcPocessor {
        @Override
        public void processorProcess() throws Exception {
            DustUtils.RefPathResolver pr = new DustUtils.RefPathResolver();
            Object val = pr.resolve(false);
            
            if ( null == val ) {
                DustEntity root = DustUtils.getCtxVal(ContextRef.msg, DustGenericComponents.DustGenericLinks.ContextAwareEntity, true);

                val = pr.resolve(root, false);
            }

            if (null != val) {
                String txt = DustUtilsJava.toString(val);
                Responder.send(txt, null);
            }
        }
    }

    public class RendererPlain implements DustProcComponents.DustProcPocessor, DustProcComponents.DustProcEvaluator {
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

            DustEntity eSelf = DustUtils.getCtxVal(ContextRef.self, null, false);

            DustEntity eMsgCallback = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);
            DustUtils.accessEntity(DataCommand.setRef, eMsgCallback, DustDataLinks.MessageCommand, DustProcMessages.ProcessorProcess);

            DustEntity eMsgRelay = DustUtils.accessEntity(DataCommand.cloneEntity, ContextRef.msg);
            DustUtils.accessEntity(DataCommand.setRef, eMsgRelay, DustDataLinks.MessageCommand, DustProcMessages.ProcessorProcess);
            DustUtils.accessEntity(DataCommand.setRef, eMsgRelay, DustTextLinks.TextRenderContextTarget, eSelf);
            DustUtils.accessEntity(DataCommand.setRef, eMsgRelay, DustTextLinks.TextRenderContextMessage, eMsgCallback);

            DustEntity eRoot = DustUtils.getCtxVal(ContextRef.self, DustTextLinks.TextRendererRoot, true);

            DustUtils.accessEntity(DataCommand.tempSend, eRoot, eMsgRelay);

            return sbContent.toString();
        }

    }

}
