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

//		EntityResolver.register("Knowledge:Meta:AttDef:Bool", DustMetaAttDefTypeValues.AttDefBool);
//		EntityResolver.register("Knowledge:Meta:AttDef:Identifier", DustMetaAttDefTypeValues.AttDefIdentifier);
//		EntityResolver.register("Knowledge:Meta:AttDef:Integer", DustMetaAttDefTypeValues.AttDefInteger);
//		EntityResolver.register("Knowledge:Meta:AttDef:Float", DustMetaAttDefTypeValues.AttDefFloat);
//
//		EntityResolver.register("Knowledge:Meta:LinkDef:Single", DustMetaLinkDefTypeValues.LinkDefSingle);
//		EntityResolver.register("Knowledge:Meta:LinkDef:Set", DustMetaLinkDefTypeValues.LinkDefSet);
//		EntityResolver.register("Knowledge:Meta:LinkDef:Array", DustMetaLinkDefTypeValues.LinkDefArray);
//		EntityResolver.register("Knowledge:Meta:LinkDef:Map", DustMetaLinkDefTypeValues.LinkDefMap);
//
//		EntityResolver.register("Knowledge:Meta:AttDef.Type", DustMetaLinks.AttDefType);
//		EntityResolver.register("Knowledge:Meta:LinkDef.Type", DustMetaLinks.LinkDefType);
//
//		EntityResolver.register("Knowledge:Data:Entity.PrimaryType", DustDataLinks.EntityPrimaryType);
//		EntityResolver.register("Knowledge:Data:Entity.Models", DustDataLinks.EntityModels);
//		EntityResolver.register("Knowledge:Data:Entity.Services", DustDataLinks.EntityServices);
//
//		EntityResolver.register("Knowledge:Meta:Type", DustMetaTypes.Type);
//		EntityResolver.register("Knowledge:Meta:AttDef", DustMetaTypes.AttDef);
//		EntityResolver.register("Knowledge:Meta:LinkDef", DustMetaTypes.LinkDef);
//		EntityResolver.register("Knowledge:Meta:Service", DustMetaTypes.Service);
//		EntityResolver.register("Knowledge:Meta:Command", DustMetaTypes.Command);
//
//		EntityResolver.register("Knowledge:Data:Entity", DustDataTypes.Entity);
//
//		EntityResolver.register("Knowledge:Comm:Unit", DustCommTypes.Unit);
//		EntityResolver.register("Knowledge:Comm:Persistent", DustCommTypes.Persistent);
//		EntityResolver.register("Knowledge:Comm:Persistent.idStore", DustCommAtts.PersistentId);
//		EntityResolver.register("Knowledge:Comm:Persistent.idLocal", DustCommAtts.TermIdLocal);
		
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.LinkDefReverse, DustMetaLinks.LinkDefReverse, DustMetaLinks.LinkDefReverse);

		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.AttDefParent, DustMetaLinks.LinkDefReverse, DustMetaLinks.TypeAttDefs);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.LinkDefParent, DustMetaLinks.LinkDefReverse, DustMetaLinks.TypeLinkDefs);


		DustEntity dlBinImplSvc = EntityResolver.getEntity(DustProcLinks.BinaryImplementedServices);
		DustEntity dlCtxBin = EntityResolver.getEntity(DustProcLinks.ContextBinaryAssignments);
		DustEntity dlLinkDefType = EntityResolver.getEntity(DustMetaLinks.LinkDefType);
		DustEntity dlLinkDefTypeSet = EntityResolver.getEntity(DustMetaLinkDefTypeValues.LinkDefSet);

		DustEntity dlGenExtends = EntityResolver.getEntity(DustGenericLinks.ConnectedExtends);
		DustEntity dlGenRequires = EntityResolver.getEntity(DustGenericLinks.ConnectedRequires);

		Dust.accessEntity(DataCommand.setRef, dlCtxBin, dlLinkDefType, dlLinkDefTypeSet, null);
		Dust.accessEntity(DataCommand.setRef, dlBinImplSvc, dlLinkDefType, dlLinkDefTypeSet, null);
		DustUtils.accessEntity(DataCommand.setRef, DustProcLinks.ContextChangeListeners, dlLinkDefType, dlLinkDefTypeSet, null);
		
		Dust.accessEntity(DataCommand.setRef, dlGenExtends, dlLinkDefType, dlLinkDefTypeSet, null);
		Dust.accessEntity(DataCommand.setRef, dlGenRequires, dlLinkDefType, dlLinkDefTypeSet, null);
		
		DustUtils.accessEntity(DataCommand.setValue, DustProcServices.Listener, DustGenericAtts.IdentifiedIdLocal, "DustProcListener");
		DustUtils.accessEntity(DataCommand.setValue, DustProcMessages.ListenerProcessChange, DustGenericAtts.IdentifiedIdLocal, "ProcessChange");
		DustUtils.accessEntity(DataCommand.setRef, DustProcMessages.ListenerProcessChange, DustGenericLinks.ConnectedOwner, DustProcServices.Listener);

		
		DustUtils.accessEntity(DataCommand.setValue, DustProcServices.Active, DustGenericAtts.IdentifiedIdLocal, "DustProcActive");
		DustUtils.accessEntity(DataCommand.setValue, DustProcMessages.ActiveInit, DustGenericAtts.IdentifiedIdLocal, "Init");
		DustUtils.accessEntity(DataCommand.setRef, DustProcMessages.ActiveInit, DustGenericLinks.ConnectedOwner, DustProcServices.Active);

		DustUtils.accessEntity(DataCommand.setValue, DustProcMessages.ActiveRelease, DustGenericAtts.IdentifiedIdLocal, "Release");
		DustUtils.accessEntity(DataCommand.setRef, DustProcMessages.ActiveRelease, DustGenericLinks.ConnectedOwner, DustProcServices.Active);

		
		DustUtils.accessEntity(DataCommand.setValue, DustMetaLinks.LinkDefReverse, DustGenericAtts.IdentifiedIdLocal, "Reverse");
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.LinkDefReverse, DustDataLinks.EntityPrimaryType, DustMetaTypes.LinkDef);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.LinkDefReverse, DustMetaLinks.LinkDefParent, DustMetaTypes.LinkDef);


		DustUtils.accessEntity(DataCommand.setValue, DustMetaLinks.AttDefParent, DustGenericAtts.IdentifiedIdLocal, "ParentType");
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.AttDefParent, DustDataLinks.EntityPrimaryType, DustMetaTypes.LinkDef);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.AttDefParent, DustMetaLinks.LinkDefParent, DustMetaTypes.AttDef);
		
		DustUtils.accessEntity(DataCommand.setValue, DustMetaLinks.LinkDefParent, DustGenericAtts.IdentifiedIdLocal, "ParentType");
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.LinkDefParent, DustDataLinks.EntityPrimaryType, DustMetaTypes.LinkDef);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.LinkDefParent, DustMetaLinks.LinkDefParent, DustMetaTypes.LinkDef);
		
		DustUtils.accessEntity(DataCommand.setValue, DustMetaLinks.TypeAttDefs, DustGenericAtts.IdentifiedIdLocal, "Attributes");
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.TypeAttDefs, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.TypeAttDefs, DustDataLinks.EntityPrimaryType, DustMetaTypes.LinkDef);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.TypeAttDefs, DustMetaLinks.LinkDefParent, DustMetaTypes.Type);
		
		DustUtils.accessEntity(DataCommand.setValue, DustMetaLinks.TypeLinkDefs, DustGenericAtts.IdentifiedIdLocal, "Links");
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.TypeLinkDefs, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.TypeLinkDefs, DustDataLinks.EntityPrimaryType, DustMetaTypes.LinkDef);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.TypeLinkDefs, DustMetaLinks.LinkDefParent, DustMetaTypes.Type);
		
		

		
		
		
		DustUtils.accessEntity(DataCommand.setRef, DustCommAtts.PersistentEntityId, DustMetaLinks.AttDefParent, DustCommTypes.Persistent);
		DustUtils.accessEntity(DataCommand.setRef, DustCommAtts.PersistentCommitId, DustMetaLinks.AttDefParent, DustCommTypes.Persistent);
		
		DustUtils.accessEntity(DataCommand.setRef, DustCommLinks.PersistentContainingUnit, DustMetaLinks.LinkDefParent, DustCommTypes.Persistent);
		DustUtils.accessEntity(DataCommand.setRef, DustCommLinks.PersistentStoreWith, DustMetaLinks.LinkDefParent, DustCommTypes.Persistent);
		DustUtils.accessEntity(DataCommand.setRef, DustCommLinks.UnitEntities, DustMetaLinks.LinkDefParent, DustCommTypes.Unit);
		DustUtils.accessEntity(DataCommand.setRef, DustCommLinks.UnitEntities, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);
		DustUtils.accessEntity(DataCommand.setRef, DustCommLinks.PersistentContainingUnit, DustMetaLinks.LinkDefReverse, DustCommLinks.UnitEntities);

		DustUtils.accessEntity(DataCommand.setRef, DustCommTypes.Unit, DustGenericLinks.ConnectedRequires, DustCommTypes.Persistent);
		DustUtils.accessEntity(DataCommand.setRef, DustCommTypes.Unit, DustGenericLinks.ConnectedRequires, DustGenericTypes.Identified);

		
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.AttDefParent, DustMetaLinks.LinkDefParent, DustMetaTypes.AttDef);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.AttDefType, DustMetaLinks.LinkDefParent, DustMetaTypes.AttDef);

		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.LinkDefParent, DustMetaLinks.LinkDefParent, DustMetaTypes.LinkDef);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.LinkDefType, DustMetaLinks.LinkDefParent, DustMetaTypes.LinkDef);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaLinks.LinkDefReverse, DustMetaLinks.LinkDefParent, DustMetaTypes.LinkDef);

		DustUtils.accessEntity(DataCommand.setRef, DustDataLinks.EntityPrimaryType, DustMetaLinks.LinkDefParent, DustDataTypes.Entity);
		DustUtils.accessEntity(DataCommand.setRef, DustDataLinks.EntityModels, DustMetaLinks.LinkDefParent, DustDataTypes.Entity);
		DustUtils.accessEntity(DataCommand.setRef, DustDataLinks.EntityModels, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);
		DustUtils.accessEntity(DataCommand.setRef, DustDataLinks.EntityServices, DustMetaLinks.LinkDefParent, DustDataTypes.Entity);
		DustUtils.accessEntity(DataCommand.setRef, DustDataLinks.EntityServices, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);

		
		DustUtils.accessEntity(DataCommand.setRef, DustMetaTypes.AttDef, DustCommLinks.PersistentStoreWith, DustMetaLinks.AttDefParent);
		DustUtils.accessEntity(DataCommand.setRef, DustMetaTypes.LinkDef, DustCommLinks.PersistentStoreWith, DustMetaLinks.LinkDefParent);
		
		for ( DustMetaTypes mt : DustMetaTypes.values() ) {
			DustUtils.accessEntity(DataCommand.setRef, mt, DustGenericLinks.ConnectedRequires, DustGenericTypes.Identified);			
		}
		
		inited = true;
	}
}
