package dust.mj02.dust;

import java.util.HashMap;
import java.util.Map;

public interface DustComponents {

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
		msg, self, ctx
	}

	enum DataCommand implements DustEntityKey {
		getValue(false), setValue(false), setRef(true), removeRef(true), clearRefs(true), tempSend(false);

		private final boolean ref;

		private DataCommand(boolean ref) {
			this.ref = ref;
		}

		public boolean isRef() {
			return ref;
		}
	}

	public interface DustEntity {
	}

	public interface DustEntityKey {
	}

	public interface DustRef {
		<InfoType> InfoType get(RefKey ref);
		void processAll(RefProcessor proc);
	}

	interface EntityProcessor {
		void processEntity(Object key, DustEntity entity);
	}

	interface RefProcessor {
		void processRef(DustRef ref);
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

			if ((null == e) && (key instanceof DustEntityKey)) {
				// so that all enums will have their entity without problem
				String kk = key.getClass().getName() +":" + ((Enum<?>) key).name();
				e = register(kk, key);
			}

			return e;
		}

		@SuppressWarnings("unchecked")
		public static <RetType> RetType getKey(DustEntity e) {
			return (RetType) entityToKey.get(e);
		}

	}
}
