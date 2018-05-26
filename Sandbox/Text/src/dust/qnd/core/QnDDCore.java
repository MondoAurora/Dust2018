package dust.qnd.core;

import dust.qnd.pub.QnDDException;
import dust.utils.DustUtilsConfig;
import dust.utils.DustUtilsConfig.DustConfigConsole;
import dust.utils.DustUtilsDev;
import text.test.Test01;

public class QnDDCore implements QnDDCoreComponents {

	private static QnDDCoreKernel kernel;
	
	public static void main(String[] args) throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				DustUtilsDev.dump("Shutting down...");
				try {
					kernel.shutdown();
					DustUtilsDev.dump("Graceful shutdown completed.");
				} catch (Exception e) {
					QnDDException.wrapException("Shutdown failure", e);
				}		
			}
		});

		DustUtilsDev.dump("Launching QnDDCore");
		DustUtilsConfig cfg = new DustConfigConsole(args);
		
		DustUtilsDev.dump("Initializing...");
		
		kernel = new QnDDCoreKernel();
		
		kernel.init(cfg);
		
		DustUtilsDev.dump("Launching...");
		
		kernel.launch();
		
		Test01.main(args);
	}

	public static QnDDEnvironment getKernel() {
		return kernel;
	}
}
