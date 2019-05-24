package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustUtils;

public interface DustProcCoreServices extends DustKernelImplComponents {

    public static class DustIterator implements DustProcPocessor {

        private final class IterProc {
            DustEntity msgRelay;
            DustEntity proc;
            DustEntity backProc;
            DustEntity filterProc;
            DustEntity filterMsg;

            public IterProc() {
                this.proc = DustUtils.getCtxVal(ContextRef.self, DustProcLinks.RelayTarget, true);
                this.backProc = new DustUtils.RefPathResolver().resolve(ContextRef.msg, DustProcLinks.IteratorPathMsgTarget, true);
                this.filterProc = DustUtils.getCtxVal(ContextRef.self, DustProcLinks.IteratorEvalFilter, true);
                if ( null != filterProc ) {
                    filterMsg = DustUtils.accessEntity(DataCommand.cloneEntity, ContextRef.msg);
                    DustUtils.accessEntity(DataCommand.setRef, filterMsg, DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);
                }
            }

            private void procRef(DustRef ref) {
                DustEntity member = ref.get(RefKey.target);
                
                if ( null != filterProc ) {
                    DustUtils.accessEntity(DataCommand.setRef, filterMsg, DustGenericLinks.ContextAwareEntity, member);
                    DustUtils.accessEntity(DataCommand.tempSend, filterProc, filterMsg);
                    if ( Boolean.FALSE.equals(DustUtils.accessEntity(DataCommand.getValue, filterMsg, DustDataAtts.MessageReturn))) {
                        return;
                    }
                }
                
                if (null == msgRelay) {
                    msgRelay = DustUtils.accessEntity(DataCommand.cloneEntity, ContextRef.msg);
                    optSend(DustProcLinks.IteratorMsgStart);
                } else {
                    optSend(DustProcLinks.IteratorMsgSep);                    
                }
                DustUtils.accessEntity(DataCommand.setRef, msgRelay, DustGenericLinks.ContextAwareEntity, member);
                DustUtils.accessEntity(DataCommand.tempSend, proc, msgRelay);
            }

            private void optSend(DustEntityKey key) {
                if (null != backProc) {
                    DustEntity backMsg = DustUtils.getCtxVal(ContextRef.self, key, true);
                    if (null != backMsg) {
                        DustUtils.accessEntity(DataCommand.tempSend, backProc, backMsg);
                    }
                }
            }
            
            public void execute() {
                DustEntity loopEntity = DustUtils.getCtxVal(ContextRef.self, DustGenericLinks.ContextAwareEntity, true);
//                DustEntity loopKey = DustUtils.getCtxVal(ContextRef.self, DustProcLinks.IteratorLinkLoop, true);
                
                DustUtils.RefPathResolver pr = new DustUtils.RefPathResolver();
                DustRef refIt = pr.resolve(loopEntity, DustProcLinks.IteratorLinkLoop, false);

                refIt.processAll(
//                DustUtils.accessEntity(DataCommand.processRef, loopEntity, loopKey, 
                        new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        procRef(ref);
                    }
                });

                if (null != msgRelay) {
                    optSend(DustProcLinks.IteratorMsgEnd);
                }
            }

        }

        @Override
        public void processorProcess() throws Exception {
           new IterProc().execute();
        }
    }
}
