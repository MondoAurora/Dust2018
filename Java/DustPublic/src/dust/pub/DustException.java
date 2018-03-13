package dust.pub;

import dust.gen.dust.base.DustBaseComponents;

public class DustException extends RuntimeException implements DustBaseComponents {
	private static final long serialVersionUID = 1L;

	public DustException(DustBaseEntity errInfo, Throwable wrapped) {
		super(DustUtils.toString(errInfo), wrapped);
	}
}