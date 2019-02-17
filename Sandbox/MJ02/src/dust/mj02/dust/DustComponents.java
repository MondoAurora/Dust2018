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
	
	enum ContextRef {
		msg, self, ctx
	}
	
	enum DataCommand {
		getValue(false), setValue(false), setRef(true), removeRef(true), clearRefs(true);
		
		private final boolean ref;
		
		private DataCommand(boolean ref) {
			this.ref = ref;
		}
	
		public boolean isRef() {
			return ref;
		}
	}

	public interface DustEntity {}
	public interface DustRef {
		<InfoType> InfoType get(RefKey ref);
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
		
		public static void register(String storeId, Object key) {
			DustEntity e = Dust.getEntity(storeId);
			keyToEntity.put(key, e);
			entityToKey.put(e, key);
		}
		
		public static DustEntity getEntity(Object key) {
			return keyToEntity.get(key);
		}
		
		@SuppressWarnings("unchecked")
		public static <RetType> RetType getKey(DustEntity e) { 
			return (RetType) entityToKey.get(e);
		}

	}
}
