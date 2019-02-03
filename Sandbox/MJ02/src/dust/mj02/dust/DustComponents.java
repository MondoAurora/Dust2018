package dust.mj02.dust;

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

	interface EntityProcessor {
		void processEntity(Object key, DustEntity entity);
	}

	interface RefProcessor {
		 void processRef(DustEntity source, DustEntity linkDef, DustEntity target, Object key);
	}
	
	public interface DustContext {
		DustEntity ctxGetEntity(Object globalId);
		<RetType> RetType ctxAccessEntity(DataCommand cmd, DustEntity e, DustEntity key, Object val, Object collId);
		void ctxProcessRefs(RefProcessor proc, DustEntity source, DustEntity linkDef, DustEntity target);
		void ctxProcessEntities(EntityProcessor proc);
	}
}
