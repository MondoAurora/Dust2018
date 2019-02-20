package dust.mj02.sandbox;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustComponents;
import dust.mj02.dust.knowledge.DustDataComponents;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsDev;

public class DustSandboxListenerDump implements DustComponents, DustGenericComponents, DustProcComponents,
		DustMetaComponents, DustProcComponents.DustProcListener {

	private static boolean inited = false;

	Object getMsgVal(DustEntityKey key) {
		Object ret = Dust.accessEntity(DataCommand.getValue, ContextRef.msg, EntityResolver.getEntity(key), null, null);
		if (ret instanceof DustRef) {
			ret = ((DustRef) ret).get(RefKey.target);
		}
		return ret;
	}

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		DustUtilsDev.dump("Dump change", EntityResolver.getKey((DustEntity)getMsgVal(DustProcLinks.ChangeCmd)), "\n :",
				getMsgVal(DustProcLinks.ChangeEntity), ".", getMsgVal(DustProcLinks.ChangeKey), "\n ",
				getMsgVal(DustProcAtts.ChangeOldValue), ">", getMsgVal(DustProcAtts.ChangeNewValue));
	}

	public static void init() {
		if (!inited) {
			DustEntity daGenId = EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal);

			DustEntity daBinObj = EntityResolver.getEntity(DustProcAtts.BinaryObjectName);
			DustEntity dlBinImplSvc = EntityResolver.getEntity(DustProcLinks.BinaryImplementedServices);
			DustEntity dlCtxBin = EntityResolver.getEntity(DustProcLinks.ContextBinaryAssignments);
			DustEntity dlLinkDefType = EntityResolver.getEntity(DustMetaLinks.LinkDefType);
			DustEntity dlLinkDefTypeSet = EntityResolver.getEntity(DustMetaValueLinkDefType.LinkDefSet);

			DustEntity dlGenOwner = EntityResolver.getEntity(DustGenericLinks.Owner);
			DustEntity dsListener = EntityResolver.getEntity(DustProcServices.Listener);
			DustEntity dcProcessChange = EntityResolver.getEntity(DustProcMessages.ListenerProcessChange);

			DustEntity DR_ENTITY_SERVICE = EntityResolver.getEntity(DustDataComponents.DustDataLinks.EntityServices);
			DustEntity dlCtxListener = EntityResolver.getEntity(DustProcLinks.ContextChangeListeners);

			String cName = DustSandboxListenerDump.class.getName();
			DustEntity ba = Dust.getEntity("ListenerDump: " + cName);

			Dust.accessEntity(DataCommand.setRef, dlCtxBin, dlLinkDefType, dlLinkDefTypeSet, null);
			Dust.accessEntity(DataCommand.setRef, dlBinImplSvc, dlLinkDefType, dlLinkDefTypeSet, null);

			Dust.accessEntity(DataCommand.setValue, ba, daBinObj, cName, null);
			Dust.accessEntity(DataCommand.setRef, ba, dlBinImplSvc, dsListener, null);

			Dust.accessEntity(DataCommand.setRef, ContextRef.ctx, dlCtxBin, ba, null);

			Dust.accessEntity(DataCommand.setValue, dsListener, daGenId, "DustProcListener", null);
			Dust.accessEntity(DataCommand.setValue, dcProcessChange, daGenId, "ProcessChange", null);
			Dust.accessEntity(DataCommand.setRef, dcProcessChange, dlGenOwner, dsListener, null);

			DustEntity listener = Dust.getEntity("Dumper");
			Dust.accessEntity(DataCommand.setRef, listener, DR_ENTITY_SERVICE, dsListener, null);
			Dust.accessEntity(DataCommand.setRef, ContextRef.ctx, dlCtxListener, listener, null);

			inited = true;
		}
	}
}
