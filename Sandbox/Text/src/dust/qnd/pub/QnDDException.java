package dust.qnd.pub;

public class QnDDException extends RuntimeException implements QnDDComponents {
	private static final long serialVersionUID = 1L;

	public QnDDException(String errInfo, Throwable wrapped, Object... context) {
		super(errInfo.toString(), wrapped);
	}
	
	public static void throwException(String errInfo, Object... context) {
		throw new QnDDException(errInfo, null);
	}
	
	public static void wrapException(String errInfo, Throwable t, Object... context) {
		if ( t instanceof QnDDException ) {
			throw (QnDDException) t;
		} else {
			throw new QnDDException(errInfo, t, context);
		}
	}
}