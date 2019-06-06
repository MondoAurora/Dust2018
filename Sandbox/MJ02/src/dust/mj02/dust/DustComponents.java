package dust.mj02.dust;

import java.util.HashMap;
import java.util.Map;

import dust.utils.DustUtilsComponents;
import dust.utils.DustUtilsJava;

public interface DustComponents extends DustUtilsComponents {

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

	enum RefKey {
		source, target, linkDef, key
	}

	enum ContextRef implements DustEntity {
		msg, self, session
	}

	enum TagCommand implements DustEntityKey {
		set, clear, test
	}

	enum DataCommand implements DustEntityKey {
		getEntity(false, false), cloneEntity(false, false), dropEntity(false, true), 
		getValue(false, false), setValue(false, true), processContent(false, false), 
		processRef(false, false), setRef(true, true), updateRef(true, true), removeRef(true, true), clearRefs(true, true), 
		tempSend(false, false);

        private final boolean ref;
        private final boolean change;

		private DataCommand(boolean ref, boolean change) {
            this.ref = ref;
            this.change = change;
		}

		public boolean isRef() {
			return ref;
		}
		
		public boolean isChange() {
            return change;
        }
	}

	public interface DustEntity {
	}

	public interface DustEntityKey {
	}

	public interface DustRef {
		<InfoType> InfoType get(RefKey ref);
        void processAll(RefProcessor proc);
        boolean contains(DustEntity entity);
        int count();
        void hackUpdate(DustEntity entity);
	}

	interface EntityProcessor {
		void processEntity(DustEntity entity);
	}

	interface RefProcessor {
		void processRef(DustRef ref);
	}

	interface ContentProcessor {
		void processContent(DustEntity eOwner, DustEntity eKey, Object value);
	}

	public abstract class EntityResolver {
		private static Map<Object, DustEntity> keyToEntity = new HashMap<>();
		private static Map<DustEntity, Object> entityToKey = new HashMap<>();

		public static DustEntity register(String storeId, Object key) {
			DustEntity e = Dust.getEntity(storeId);
			keyToEntity.put(key, e);
			entityToKey.put(e, key);
			return e;
		}

		public static DustEntity getEntity(Object key) {
			DustEntity e = keyToEntity.get(key);

			if (null == e) {
				e = DustTempHacks.loadFromEnum(key);
			}

			return e;
		}

		@SuppressWarnings("unchecked")
		public static <RetType> RetType getKey(DustEntity e) {
			return (RetType) entityToKey.get(e);
		}
	}
	
	public static class FinderByAttValue implements RefProcessor {
		private DustEntity found;
		private final Object key;
		private final Object value;

		public FinderByAttValue(Object key, Object value) {
			this.key = key;
			this.value = value;
		}
		@Override
		public void processRef(DustRef ref) {
			if (null == found) {
				DustEntity t = ref.get(RefKey.target);
				String k = DustUtils.accessEntity(DataCommand.getValue, t, key);
				if (DustUtilsJava.isEqual(value, k)) {
					found = t;
				}
			}
		}
		
		public DustEntity getFound() {
			return found;
		}
	}

}
