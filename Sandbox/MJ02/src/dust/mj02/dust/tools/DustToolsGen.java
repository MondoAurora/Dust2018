package dust.mj02.dust.tools;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustMetaComponents.DustMetaLinkDefTypeValues;
import dust.mj02.dust.knowledge.DustMetaComponents.DustMetaLinks;

public class DustToolsGen implements DustGenericComponents {
	public static void init() {
//		EntityResolver.register("Tools:Generic:Identified", DustGenericTypes.Identified);
//		EntityResolver.register("Tools:Generic:Connected", DustGenericTypes.Connected);
//		
//		EntityResolver.register("Tools:Generic:Identified.idLocal", DustGenericAtts.IdentifiedIdLocal);
//		
//		EntityResolver.register("Tools:Generic:Connected.Owner", DustGenericLinks.ConnectedOwner);
//		EntityResolver.register("Tools:Generic:Connected.Requires", DustGenericLinks.ConnectedRequires);
		
		DustUtils.accessEntity(DataCommand.setRef, DustGenericAtts.IdentifiedIdLocal, DustMetaLinks.AttDefParent, DustGenericTypes.Identified);
		
		DustUtils.accessEntity(DataCommand.setRef, DustGenericLinks.ConnectedOwner, DustMetaLinks.LinkDefParent, DustGenericTypes.Connected);
		DustUtils.accessEntity(DataCommand.setRef, DustGenericLinks.ConnectedRequires, DustMetaLinks.LinkDefParent, DustGenericTypes.Connected);
		DustUtils.accessEntity(DataCommand.setRef, DustGenericLinks.ConnectedRequires, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);
		DustUtils.accessEntity(DataCommand.setRef, DustGenericLinks.ConnectedExtends, DustMetaLinks.LinkDefParent, DustGenericTypes.Connected);
		DustUtils.accessEntity(DataCommand.setRef, DustGenericLinks.ConnectedExtends, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);

		DustUtils.accessEntity(DataCommand.setRef, DustGenericAtts.StreamFileName, DustMetaLinks.AttDefParent, DustGenericTypes.Stream);
		DustUtils.accessEntity(DataCommand.setRef, DustGenericAtts.StreamFileAccess, DustMetaLinks.AttDefParent, DustGenericTypes.Stream);
		
		DustUtils.accessEntity(DataCommand.setRef, DustGenericLinks.TaggedTags, DustMetaLinks.LinkDefParent, DustGenericTypes.Tagged);
		DustUtils.accessEntity(DataCommand.setRef, DustGenericLinks.TaggedTags, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);


	}
}
