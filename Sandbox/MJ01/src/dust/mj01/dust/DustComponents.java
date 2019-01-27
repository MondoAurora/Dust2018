package dust.mj01.dust;

public interface DustComponents {
	public interface DustRef {};
	public interface DustId extends DustRef {};
	
	public enum DustCtx implements DustId {
		CtxBlock, CtxMessage, CtxSelf;
	}
	
	public class DustException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public DustException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			// TODO Auto-generated constructor stub
		}

		public DustException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		public DustException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

	}
}
