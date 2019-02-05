package dust.mj02.dust.tools;

public class DustToolsGen implements DustGenericComponents {
	public static void init() {
		EntityResolver.register("Tools:Generic:Identified", DustGenericTypes.Identified);
		EntityResolver.register("Tools:Generic:Connected", DustGenericTypes.Connected);
		
		EntityResolver.register("Tools:Generic:Identified.idLocal", DustGenericAtts.identifiedIdLocal);
		
		EntityResolver.register("Tools:Generic:Connected.Owner", DustGenericLinks.Owner);
		EntityResolver.register("Tools:Generic:Connected.Extends", DustGenericLinks.Extends);
	}
}
