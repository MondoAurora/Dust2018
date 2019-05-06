package dust.mj02.dust.knowledge;

interface DustKernelImplComponents extends DustKernelComponents {
    abstract class LazyMsgContainer {
        private DustDataEntity msg = null;

        public DustDataEntity getMsg() {
            if (null == msg) {
                msg = createMsg();
            }
            return msg;
        }

        protected abstract DustDataEntity createMsg();
    }
}
