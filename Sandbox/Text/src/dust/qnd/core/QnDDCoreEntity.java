package dust.qnd.core;

import java.util.EnumMap;

import dust.qnd.pub.QnDDComponents;
import dust.qnd.pub.QnDDException;
import dust.utils.DustUtilsFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
class QnDDCoreEntity implements QnDDCoreComponents, QnDDComponents.QnDDEntity {
	private QnDDCoreStore store;
	private String type;
	private String key;

	DustUtilsFactory<Class, EnumMap> attributes = new DustUtilsFactory<Class, EnumMap>(false) {
		@Override
		protected EnumMap create(Class key, Object... hints) {
			return new EnumMap(key);
		}
	};

	DustUtilsFactory<Class, Object> logic = new DustUtilsFactory<Class, Object>(false) {
		@Override
		protected Object create(Class key, Object... hints) {
			try {
				QnDDLogic logic = (QnDDLogic) key.newInstance();
				store.getKernel().connect(logic, QnDDCoreEntity.this);
				return logic;
			} catch (Exception e) {
				QnDDException.wrapException("Logic creation or connectioin error", e, key, QnDDCoreEntity.this);
				return null;
			}
		}
	};

	void lock(QnDDCoreStore store, String type, String key) {
		this.store = store;
		this.type = type;
		this.key = key;
	}

	void unlock() {
		store = null;
		// key = null;
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	@Override
	public <AttType> AttType getAttValue(Enum<?> key) {
		EnumMap em = attributes.get(key.getClass());
		return (null == em) ? null : (AttType) em.get(key);
	}

	@Override
	public <AttType> AttType setAttValue(Enum<?> key, AttType value) {
		return (AttType) attributes.get(key.getClass()).put(key, value);
	}

	@Override
	public <Logic extends QnDDLogic> Logic getLogic(Class<Logic> lc) {
		// TODO Auto-generated method stub
		return null;
	}
}
