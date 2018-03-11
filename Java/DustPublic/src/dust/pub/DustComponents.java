package dust.pub;

public interface DustComponents {

	interface DustEntity {
	}

	interface DustAttrDef {
	}

	interface DustLinkDef {
	}

	interface DustMsgDef {
	}

	enum DustRefCommand implements DustEntity {
		RefSet, RefRemove, RefRemoveAll, RefChangeKey;
	}

	enum DustProcessResponse implements DustEntity {
		ProcOK, ProcSkip, ProcExit, ProcRepeat, ProcRestart;
	}

	interface DustItemProcessor {
		DustProcessResponse dustItemProcessorProcess(DustEntity entity) throws Exception;
	}

	interface DustBatchProcessor {
		void dustBatchProcessorBegin() throws Exception;

		void dustBatchProcessorEnd(DustProcessResponse lastResp, Exception optException) throws Exception;
	}

	abstract class DustDefaultProcessor implements DustBatchProcessor, DustItemProcessor {
		protected void doProcess(DustEntity entity) throws Exception {
		};

		@Override
		public DustProcessResponse dustItemProcessorProcess(DustEntity entity) throws Exception {
			doProcess(entity);
			return DustProcessResponse.ProcOK;
		}

		@Override
		public void dustBatchProcessorBegin() throws Exception {
		}

		@Override
		public void dustBatchProcessorEnd(DustProcessResponse lastResp, Exception optException) throws Exception {
		}

	}

	enum DustContext implements DustEntity {
		CtxApp, CtxSession, CtxThis, CtxMessage, CtxBlock;
	}

	class DustException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public DustException(DustEntity errInfo, Throwable wrapped) {
			super(DustUtils.toString(errInfo), wrapped);
		}
	}

}
