package dust.qnd.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import dust.qnd.pub.QnDDEnvironment;
import dust.qnd.pub.QnDDException;
import dust.qnd.pub.QnDDLogic;
import dust.utils.DustUtilsConfig;
import dust.utils.DustUtilsFactory;

class QnDDCoreKernel extends QnDDEnvironment implements DustUtilsConfig.Configurable, QnDDCoreComponents {

	DustUtilsFactory<String, QnDDCoreStore> factStore = new DustUtilsFactory<String, QnDDCoreStore>(true) {
		@Override
		protected QnDDCoreStore create(String key, Object... hints) {
			return new QnDDCoreStore(QnDDCoreKernel.this, key);
		}
	};

	Set<QnDDCoreEntity> freeEntities = new HashSet<>();

	Set<QnDDCoreLink> links = new HashSet<>();

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
		if ( null == key ) {
			return new QnDDCoreEntity();
		}
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

	public void processRefs(QnDDLinkVisitor lv, QnDDLinkDef link, QnDDCoreEntity src, QnDDCoreEntity target,
			Object key) {
		for (QnDDCoreLink l : links) {
			QnDDLink ll;
			if (null != (ll = QnDDCompUtils.match(l, src, target, link, key))) {
				lv.processLink(ll);
			}
		}
	}

	@Override
	public QnDDLink changeRef(QnDDLinkCmd cmd, QnDDLinkDef ld, QnDDEntity src, QnDDEntity target, Object key) {
		QnDDLink ll = null;
		QnDDCoreEntity eSrc = (QnDDCoreEntity) src;
		QnDDCoreEntity eTgt = (QnDDCoreEntity) target;

		switch (cmd) {
		case Add:
			QnDDCoreLink ql = new QnDDCoreLink(ld, eSrc, eTgt, key);
			links.add(ql);
			ll = ql;
			break;
		case Remove:
			break;
		case Update:
			break;
		}
		
		return ll;
	}
}
