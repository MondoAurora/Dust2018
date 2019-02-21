package dust.mj02.dust.knowledge;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.tools.DustGenericComponents;

public class DustKnowledgeGen implements DustCommComponents, DustMetaComponents, DustDataComponents, DustProcComponents, DustGenericComponents {
	private static boolean inited = false;

	public static void init() {
		if (inited) {
			return;
		}

		EntityResolver.register("Knowledge:Meta:AttDef:Bool", DustMetaValueAttDefType.AttDefBool);
		EntityResolver.register("Knowledge:Meta:AttDef:Identifier", DustMetaValueAttDefType.AttDefIdentifier);
		EntityResolver.register("Knowledge:Meta:AttDef:Integer", DustMetaValueAttDefType.AttDefInteger);
		EntityResolver.register("Knowledge:Meta:AttDef:Float", DustMetaValueAttDefType.AttDefFloat);

		EntityResolver.register("Knowledge:Meta:LinkDef:Single", DustMetaValueLinkDefType.LinkDefSingle);
		EntityResolver.register("Knowledge:Meta:LinkDef:Set", DustMetaValueLinkDefType.LinkDefSet);
		EntityResolver.register("Knowledge:Meta:LinkDef:Array", DustMetaValueLinkDefType.LinkDefArray);
		EntityResolver.register("Knowledge:Meta:LinkDef:Map", DustMetaValueLinkDefType.LinkDefMap);

		EntityResolver.register("Knowledge:Meta:AttDef.Type", DustMetaLinks.AttDefType);
		EntityResolver.register("Knowledge:Meta:LinkDef.Type", DustMetaLinks.LinkDefType);

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

		DustEntity dlBinImplSvc = EntityResolver.getEntity(DustProcLinks.BinaryImplementedServices);
		DustEntity dlCtxBin = EntityResolver.getEntity(DustProcLinks.ContextBinaryAssignments);
		DustEntity dlLinkDefType = EntityResolver.getEntity(DustMetaLinks.LinkDefType);
		DustEntity dlLinkDefTypeSet = EntityResolver.getEntity(DustMetaValueLinkDefType.LinkDefSet);

		DustEntity dlGenExtends = EntityResolver.getEntity(DustGenericLinks.Extends);
		DustEntity dlGenRequires = EntityResolver.getEntity(DustGenericLinks.Requires);

		Dust.accessEntity(DataCommand.setRef, dlCtxBin, dlLinkDefType, dlLinkDefTypeSet, null);
		Dust.accessEntity(DataCommand.setRef, dlBinImplSvc, dlLinkDefType, dlLinkDefTypeSet, null);
		Dust.accessEntity(DataCommand.setRef, dlGenExtends, dlLinkDefType, dlLinkDefTypeSet, null);
		Dust.accessEntity(DataCommand.setRef, dlGenRequires, dlLinkDefType, dlLinkDefTypeSet, null);
		
		DustUtils.accessEntity(DataCommand.setValue, DustProcServices.Listener, DustGenericAtts.identifiedIdLocal, "DustProcListener");
		DustUtils.accessEntity(DataCommand.setValue, DustProcMessages.ListenerProcessChange, DustGenericAtts.identifiedIdLocal, "ProcessChange");
		DustUtils.accessEntity(DataCommand.setRef, DustProcMessages.ListenerProcessChange, DustGenericLinks.Owner, DustProcServices.Listener);

		
		DustUtils.accessEntity(DataCommand.setValue, DustProcServices.Active, DustGenericAtts.identifiedIdLocal, "DustProcActive");
		DustUtils.accessEntity(DataCommand.setValue, DustProcMessages.ActiveInit, DustGenericAtts.identifiedIdLocal, "Init");
		DustUtils.accessEntity(DataCommand.setRef, DustProcMessages.ActiveInit, DustGenericLinks.Owner, DustProcServices.Active);

		DustUtils.accessEntity(DataCommand.setValue, DustProcMessages.ActiveRelease, DustGenericAtts.identifiedIdLocal, "Release");
		DustUtils.accessEntity(DataCommand.setRef, DustProcMessages.ActiveRelease, DustGenericLinks.Owner, DustProcServices.Active);


		inited = true;
	}
}
