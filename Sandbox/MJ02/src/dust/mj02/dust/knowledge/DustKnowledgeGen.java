package dust.mj02.dust.knowledge;

public class DustKnowledgeGen implements DustCommComponents, DustMetaComponents, DustDataComponents {
	public static void init() {
		EntityResolver.register("Knowledge:Meta:AttDef:Bool", DustMetaValueAttDefType.AttDefBool);
		EntityResolver.register("Knowledge:Meta:AttDef:Identifier", DustMetaValueAttDefType.AttDefIdentifier);
		EntityResolver.register("Knowledge:Meta:AttDef:Integer", DustMetaValueAttDefType.AttDefInteger);
		EntityResolver.register("Knowledge:Meta:AttDef:Float", DustMetaValueAttDefType.AttDefFloat);
		
		EntityResolver.register("Knowledge:Meta:LinkDef:Single", DustMetaValueLinkDefType.LinkDefSingle);
		EntityResolver.register("Knowledge:Meta:LinkDef:Set", DustMetaValueLinkDefType.LinkDefSet);
		EntityResolver.register("Knowledge:Meta:LinkDef:Array", DustMetaValueLinkDefType.LinkDefArray);
		EntityResolver.register("Knowledge:Meta:LinkDef:Map", DustMetaValueLinkDefType.LinkDefMap);
		
		EntityResolver.register("Knowledge:Meta:AttDef.Type", DustMetaAtts.AttDefType);
		EntityResolver.register("Knowledge:Meta:LinkDef.Type", DustMetaAtts.LinkDefType);
		
		EntityResolver.register("Knowledge:Data:Entity.PrimaryType", DustDataLinks.EntityPrimaryType);
		EntityResolver.register("Knowledge:Data:Entity.Models", DustDataLinks.EntityModels);
		EntityResolver.register("Knowledge:Data:Entity.Services", DustDataLinks.EntityServices);
		
		EntityResolver.register("Knowledge:Meta:Unit", DustMetaTypes.Unit);
		EntityResolver.register("Knowledge:Meta:Type", DustMetaTypes.Type);
		EntityResolver.register("Knowledge:Meta:AttDef", DustMetaTypes.AttDef);
		EntityResolver.register("Knowledge:Meta:LinkDef", DustMetaTypes.LinkDef);
		EntityResolver.register("Knowledge:Meta:Service", DustMetaTypes.Service);
		EntityResolver.register("Knowledge:Meta:Command", DustMetaTypes.Command);
		
		EntityResolver.register("Knowledge:Data:Entity", DustDataTypes.Entity);
		
		EntityResolver.register("Knowledge:Comm:Term.idStore", DustCommAtts.idStore);
		EntityResolver.register("Knowledge:Comm:Term.idLocal", DustCommAtts.idLocal);
		EntityResolver.register("Knowledge:Comm:Term", DustCommTypes.Term);

	}
}
