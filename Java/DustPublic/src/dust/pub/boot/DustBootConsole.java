package dust.pub.boot;

import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.DustUtils;
import dust.pub.DustUtilsDev;
import dust.pub.DustPubComponents.DustStatusInfoPub;

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
			RUNTIME.dustCoreExecBlockProcessorEnd(DustConstCoreExecVisitorResponse.OK, null);
		} catch (Exception e) {
			DustException.wrapException(e, DustStatusInfoPub.ErrorShutdownFailure);
		}		
	}

	protected static void initComps(DustConfig cfg) throws Exception {
		RUNTIME = optLoadInit(cfg, DustConfigKeys.DustRuntime);

		DustBindingManager binMgr = optLoadInit(cfg, DustConfigKeys.DustBinding);
		((DustRuntimeBootable)RUNTIME).setBinaryManager(binMgr);
		
		RUNTIME.dustCoreExecBlockProcessorBegin();
		
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
