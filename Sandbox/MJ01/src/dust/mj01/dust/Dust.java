package dust.mj01.dust;

public class Dust implements DustComponents {
	
	protected interface DustRuntime {
		DustRef buildRef(DustRef... path);
		void move(DustRef from, DustRef to);
	}

	private static DustRuntime RUNTIME;
	
	protected static synchronized void init(DustRuntime runtime) {
		if ( null == RUNTIME ) {
			RUNTIME = runtime;
		} else {
			throw new DustException("Oops, multiple init calls!");
		}
	}
	
	public static DustRef buildRef(DustRef... path) {
		return RUNTIME.buildRef(path);
	}
	
	void move(DustRef from, DustRef to) {
		RUNTIME.move(from, to);
	}
	
}
