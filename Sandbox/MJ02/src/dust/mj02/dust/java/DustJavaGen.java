package dust.mj02.dust.java;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustKnowledgeGen;

public class DustJavaGen implements DustJavaComponents {

	private static boolean inited = false;

	public static void init() {
		if (!inited) {
			DustKnowledgeGen.init();
			
			DustUtils.accessEntity(DataCommand.setRef, DustJavaLinks.JavaPackageItems, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);
			DustUtils.accessEntity(DataCommand.setRef, DustJavaLinks.JavaPackageMembers, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);

			DustUtils.accessEntity(DataCommand.setRef, DustJavaLinks.JavaItemParentPackage, DustMetaLinks.LinkDefReverse, DustJavaLinks.JavaPackageItems);
			DustUtils.accessEntity(DataCommand.setRef, DustJavaLinks.JavaPackageParent, DustMetaLinks.LinkDefReverse, DustJavaLinks.JavaPackageMembers);

			DustUtils.registerService(DustJavaConnector.class, true, DustJavaServices.JavaConnector, DustProcServices.Active);

			DustEntity javaConn = Dust.getEntity("DustJavaConnector");
			DustUtils.accessEntity(DataCommand.setValue, javaConn, DustGenericAtts.IdentifiedIdLocal, "dust");
			DustUtils.accessEntity(DataCommand.setRef, javaConn, DustDataLinks.EntityServices, DustJavaServices.JavaConnector);
			
			inited = true;
		}
	}
}
