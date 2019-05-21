package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustUtils;

public interface DustProcCoreServices extends DustKernelImplComponents {

    public static class DustIterator implements DustProcPocessor {
        private final class IterProc implements RefProcessor {
            DustEntity proc;
            boolean init;

            DustEntity eMsg = null;

            public IterProc(DustEntity proc) {
                this.proc = proc;
                this.init = null != DustUtils.getBinary(proc, DustProcServices.Active);
            }

            @Override
            public void processRef(DustRef ref) {
                DustEntity member = ref.get(RefKey.target);

                if (null == eMsg) {
                    eMsg = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);

                    if (init) {
                        DustUtils.accessEntity(DataCommand.setRef, eMsg, DustDataLinks.MessageCommand, DustProcMessages.ActiveInit);
                        DustUtils.accessEntity(DataCommand.tempSend, proc, eMsg);
                    }
                    DustUtils.accessEntity(DataCommand.setRef, eMsg, DustDataLinks.MessageCommand, DustProcMessages.ProcessorProcess);
                }

                DustUtils.accessEntity(DataCommand.setRef, eMsg, DustGenericLinks.ContextAwareEntity, member);
                DustUtils.accessEntity(DataCommand.tempSend, proc, eMsg);
            }

            void execute(DustEntity src, DustEntity key) {
                DustUtils.accessEntity(DataCommand.processRef, src, key, this);

                if (init && (null != eMsg)) {
                    DustUtils.accessEntity(DataCommand.setRef, eMsg, DustDataLinks.MessageCommand, DustProcMessages.ActiveRelease);
                    DustUtils.accessEntity(DataCommand.tempSend, proc, eMsg);
                }
            }
        }

        @Override
        public void processorProcess() throws Exception {
            DustEntity src = DustUtils.getCtxVal(ContextRef.self, DustGenericLinks.ContextAwareEntity, true);
            DustEntity key = DustUtils.getCtxVal(ContextRef.self, DustProcLinks.IteratorLink, true);
            DustEntity proc = DustUtils.getCtxVal(ContextRef.self, DustProcLinks.RelayTarget, true);

            new IterProc(proc).execute(src, key);
        }
    }
}
