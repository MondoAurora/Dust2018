package dust.pub;

import dust.gen.dust.base.DustBaseComponents;

public class DustException extends RuntimeException implements DustBaseComponents {
	private static final long serialVersionUID = 1L;

	public DustException(DustBaseEntity errInfo, Throwable wrapped) {
		super(DustUtils.toString(errInfo), wrapped);
	}
	
	public static void throwException(DustBaseEntity errInfo, Object... context) {
		throw new DustException(errInfo, null);
	}
	
	public static void wrapException(Throwable t, DustBaseEntity errInfo) {
		if ( t instanceof DustException ) {
			throw (DustException) t;
		} else {
			throw new DustException(errInfo, t);
		}
	}

}