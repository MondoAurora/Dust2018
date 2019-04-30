package dust.mj02.dust.knowledge;

import dust.mj02.dust.knowledge.DustDataContext.SimpleEntity;

interface DustKernelImplComponents extends DustKernelComponents {
    abstract class LazyMsgContainer {
        private SimpleEntity msg = null;

        public SimpleEntity getMsg() {
            if (null == msg) {
                msg = createMsg();
            }
            return msg;
        }

        protected abstract SimpleEntity createMsg();
    }
}
