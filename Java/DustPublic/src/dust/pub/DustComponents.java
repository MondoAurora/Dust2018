package dust.pub;

public interface DustComponents {

	interface DustField {
	}

	interface DustEntity {
	}

	enum DustContext {
		CtxApp, CtxSession, CtxThis, CtxMessage, CtxBlock
	}

	class DustException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public DustException(DustEntity errInfo, Throwable wrapped) {
			super(DustUtils.toString(errInfo), wrapped);
		}
	}

}
