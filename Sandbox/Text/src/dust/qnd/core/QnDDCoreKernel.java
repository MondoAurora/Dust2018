package dust.qnd.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import dust.qnd.pub.QnDDComponents;
import dust.qnd.pub.QnDDException;
import dust.utils.DustUtilsConfig;
import dust.utils.DustUtilsFactory;

class QnDDCoreKernel extends QnDDComponents.QnDDEnvironment implements DustUtilsConfig.Configurable, QnDDCoreComponents {

	DustUtilsFactory<String, QnDDCoreStore> factStore = new DustUtilsFactory<String, QnDDCoreStore>(true) {
		@Override
		protected QnDDCoreStore create(String key, Object... hints) {
			return new QnDDCoreStore(QnDDCoreKernel.this, key);
		}
	};

	Set<QnDDCoreEntity> freeEntities = new HashSet<>();

	public QnDDCoreKernel() {
		// TODO Auto-generated constructor stub
	}

	QnDDCoreEntity lockEntity(QnDDCoreStore store, String type, String key) {
		QnDDCoreEntity e;

		if (freeEntities.isEmpty()) {
			e = new QnDDCoreEntity();
		} else {
			Iterator<QnDDCoreEntity> it = freeEntities.iterator();
			e = it.next();
			it.remove();
		}

		e.lock(store, type, key);

		return e;
	}

	void unlockEntity(QnDDCoreEntity e) {
		e.unlock();
		if (!freeEntities.add(e)) {
			QnDDException.throwException("unlock called multiple times on entity", e);
		}
	}

	public QnDDCoreEntity getEntity(String type, String key) {
		return factStore.get(type).get(key);
	}

	public void connect(QnDDLogic logic, QnDDEntity entity) throws Exception {
		super.connect(logic, entity);
	}
	
	@Override
	public void init(DustUtilsConfig config) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	void launch() {
		
	}
	
	void shutdown() {
		
	}
}
