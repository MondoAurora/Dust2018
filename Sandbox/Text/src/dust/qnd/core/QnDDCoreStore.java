package dust.qnd.core;

import dust.utils.DustUtilsFactory;

class QnDDCoreStore extends DustUtilsFactory<String, QnDDCoreEntity> implements QnDDCoreComponents {

	private final QnDDCoreKernel kernel;
	private final String type;

	public QnDDCoreStore(QnDDCoreKernel kernel, String type) {
		super(true);
		this.kernel = kernel;
		this.type = type;
	}

	@Override
	protected QnDDCoreEntity create(String key, Object... hints) {
		return kernel.lockEntity(this, type, key);
	}

	@Override
	public boolean drop(QnDDCoreEntity value) {
		kernel.unlockEntity(value);
		return super.drop(value);
	}

	QnDDCoreKernel getKernel() {
		return kernel;
	}
}
