package dust.pub;

public interface DustComponents {
	
	enum DustEntityState {
		esTemporal, esInSync, esRefChanged, esChanged, esConstructed, esDestructed
	}

	interface DustIdentifier {
	}

	interface DustField {
	}

	interface DustEntity {
		DustEntityState getState();
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
