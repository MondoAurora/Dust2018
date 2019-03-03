package dust.mj02.dust.java;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;

public class DustJavaGen implements DustJavaComponents {

	private static boolean inited = false;

	public static void init() {
		if (!inited) {
			DustUtils.registerService(DustJavaConnector.class, true, DustJavaServices.JavaConnector, DustProcServices.Active);

			DustEntity javaConn = Dust.getEntity("DustJavaConnector");
			DustUtils.accessEntity(DataCommand.setValue, javaConn, DustGenericAtts.identifiedIdLocal, "dust");
			DustUtils.accessEntity(DataCommand.setRef, javaConn, DustDataLinks.EntityServices, DustJavaServices.JavaConnector);
			
			inited = true;
		}
	}
}
