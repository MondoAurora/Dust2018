package dust.mj02.dust;

public class Dust implements DustComponents {
	
	public interface DustRuntime extends DustContext {
//		DustRef buildRef(DustRef... path);
//		void move(DustRef from, DustRef to);
	}

	private static DustRuntime RUNTIME;
	
	protected static synchronized void init(DustRuntime runtime) {
		if ( null == RUNTIME ) {
			RUNTIME = runtime;
		} else {
			throw new DustException("Oops, multiple init calls!");
		}
	}

	public static DustEntity getEntity(Object si) {
		return RUNTIME.ctxGetEntity(si);
	}

	public static <RetType> RetType accessEntity(DataCommand cmd, DustEntity e, Object key, Object val, Object collId) {
		return RUNTIME.ctxAccessEntity(cmd, e, key, val, collId);
	}
	
	public static void processEntities(EntityProcessor proc) {
		RUNTIME.ctxProcessEntities(proc);
	}

	public static void processRefs(RefProcessor proc, DustEntity source, Object linkDef, DustEntity target) {
		RUNTIME.ctxProcessRefs(proc, source, linkDef, target);
	}
	
	
//	public static DustRef buildRef(DustRef... path) {
//		return RUNTIME.buildRef(path);
//	}
//	
//	void move(DustRef from, DustRef to) {
//		RUNTIME.move(from, to);
//	}
}
