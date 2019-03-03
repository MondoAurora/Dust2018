package dust.mj02.dust.tools;

public class DustToolsGen implements DustGenericComponents {
	public static void init() {
		EntityResolver.register("Tools:Generic:Identified", DustGenericTypes.Identified);
		EntityResolver.register("Tools:Generic:Connected", DustGenericTypes.Connected);
		
		EntityResolver.register("Tools:Generic:Identified.idLocal", DustGenericAtts.IdentifiedIdLocal);
		
		EntityResolver.register("Tools:Generic:Connected.Owner", DustGenericLinks.ConnectedOwner);
		EntityResolver.register("Tools:Generic:Connected.Requires", DustGenericLinks.ConnectedRequires);
	}
}
