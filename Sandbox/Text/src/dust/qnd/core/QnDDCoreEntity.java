package dust.qnd.core;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import dust.qnd.pub.QnDDComponents;
import dust.qnd.pub.QnDDException;
import dust.qnd.pub.QnDDLogic;
import dust.qnd.pub.QnDDServices;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings({ "unchecked", "rawtypes" })
class QnDDCoreEntity implements QnDDCoreComponents, QnDDComponents.QnDDEntity {
	private static final Map<Class, Class> NO_MAPPING = Collections.unmodifiableMap(new HashMap<>());
	private static final String DEF_CLASS_PREFIX = QnDDServices.class.getName() + "$";
	
	private QnDDCoreStore store;
	private String type;
	private String key;

	DustUtilsFactory<Class, EnumMap> attributes = new DustUtilsFactory<Class, EnumMap>(false) {
		@Override
		protected EnumMap create(Class key, Object... hints) {
			return new EnumMap(key);
		}
	};

	Map<Class, Class> logicMapping;

	DustUtilsFactory<Class, Object> factLogic = new DustUtilsFactory<Class, Object>(false) {
		@Override
		protected Object create(Class key, Object... hints) {
			try {
				if ( null == logicMapping ) {
					String lm = getAttValue( QnDDAttCore.logicOverrides);
					
					if ( DustUtilsJava.isEmpty(lm)) {
						logicMapping = NO_MAPPING;
					} else {
						logicMapping = new HashMap<>();
						for (String assignment : lm.split(",") ) {
							String[] a = assignment.split("=");
							if ( -1 == a[0].indexOf(".") ) {
								a[0] = DEF_CLASS_PREFIX + a[0];
							}
							logicMapping.put(Class.forName(a[0]), Class.forName(a[1]));
						}
					}
				}
				
				QnDDLogic logic;

				Class lo = (NO_MAPPING == logicMapping) ? null : logicMapping.get(key);
				if ( null == lo ) {
					logic = (QnDDLogic) key.newInstance();
					store.getKernel().connect(logic, QnDDCoreEntity.this);					
				} else {
					logic = (QnDDLogic) get(lo);
				}
				
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
	public <Logic> Logic getLogic(Class<Logic> lc) {		
		return (Logic) factLogic.get(lc);
	}
	
	@Override
	public void processRefs(QnDDLinkVisitor lv) {
		store.getKernel().processRefs(lv, this, null, null, null);
	}
}
