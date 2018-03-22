package dust.pub.boot;

import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.DustUtils;
import dust.pub.DustUtilsDev;

public class DustBootConsole extends Dust implements DustBootComponents {
	
	public static void main(String[] args) throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				DustUtilsDev.dump("Shutting down...");
				shutdown();
				DustUtilsDev.dump("Graceful shutdown completed.");
			}
		});

		DustUtilsDev.dump("Launching Dust");
		DustConfig cfg = new DustConfigConsole(args);
		
		DustUtilsDev.dump("Initializing components...");
		initComps(cfg);		
	}

	protected static void shutdown() {
		try {
			RUNTIME.dustBaseBlockProcessorEnd(DustBaseVisitorResponse.OK, null);
		} catch (Exception e) {
			DustException.wrapException(e, null);
		}		
	}

	protected static void initComps(DustConfig cfg) throws Exception {
		RUNTIME = optLoadInit(cfg, DustConfigKeys.DustRuntime);

		DustBinaryManager binMgr = optLoadInit(cfg, DustConfigKeys.DustBinaryManager);
		((DustRuntimeBootable)RUNTIME).setBinaryManager(binMgr);
		
		RUNTIME.dustBaseBlockProcessorBegin();
		
		optLoadInit(cfg, DustConfigKeys.DustNodeInit);
	}

	@SuppressWarnings("unchecked")
	protected static <RetType extends DustConfigurable> RetType optLoadInit(DustConfig cfg, DustConfigKeys key) throws Exception {
		RetType ret = null;
		
		String cName = cfg.getCfg(key);
		if ( !DustUtils.isEmpty(cName) ) {
			ret = (RetType) Class.forName(cName).newInstance();
			DustUtilsDev.dump("Initializing", cName);
			ret.init(cfg);
		}
		
		return ret;
	}
}
