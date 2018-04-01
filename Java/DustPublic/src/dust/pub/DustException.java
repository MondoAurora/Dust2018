package dust.pub;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;

public class DustException extends RuntimeException implements DustKnowledgeInfoComponents {
	private static final long serialVersionUID = 1L;

	public DustException(DustEntity errInfo, Throwable wrapped, Object... context) {
		super(DustUtils.toString(errInfo), wrapped);
	}
	
	public static void throwException(DustEntity errInfo, Object... context) {
		throw new DustException(errInfo, null);
	}
	
	@Deprecated
	public static void wrapException(Throwable t) {
		if ( t instanceof DustException ) {
			throw (DustException) t;
		} else {
			throw new DustException(null, t);
		}
	}
	
	public static void wrapException(Throwable t, DustEntity errInfo, Object... context) {
		if ( t instanceof DustException ) {
			throw (DustException) t;
		} else {
			throw new DustException(errInfo, t, context);
		}
	}

	@Deprecated
	public static void throwException() {
		throw new DustException(null, null);
	}

}