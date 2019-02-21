package dust.mj02.sandbox;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsDev;

public class DustSandboxListenerDump implements DustSandboxComponents, DustProcComponents.DustProcListener {

	private static boolean inited = false;

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		DustUtilsDev.dump("Dump change", EntityResolver.getKey(DustUtils.getMsgVal(DustProcLinks.ChangeCmd, true)),
				"\n :", DustUtils.getMsgVal(DustProcLinks.ChangeEntity, true), ".",
				DustUtils.getMsgVal(DustProcLinks.ChangeKey, true), "\n ",
				DustUtils.getMsgVal(DustProcAtts.ChangeOldValue, true), ">",
				DustUtils.getMsgVal(DustProcAtts.ChangeNewValue, true));
	}

	public static void init() {
		if (!inited) {
			// DustEntity daGenId =
			// EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal);

			DustEntity daBinObj = EntityResolver.getEntity(DustProcAtts.BinaryObjectName);
			DustEntity dlBinImplSvc = EntityResolver.getEntity(DustProcLinks.BinaryImplementedServices);
			DustEntity dlCtxBin = EntityResolver.getEntity(DustProcLinks.ContextBinaryAssignments);

			// DustEntity dlGenOwner = EntityResolver.getEntity(DustGenericLinks.Owner);
			DustEntity dsListener = EntityResolver.getEntity(DustProcServices.Listener);
			// DustEntity dcProcessChange =
			// EntityResolver.getEntity(DustProcMessages.ListenerProcessChange);

			DustEntity DR_ENTITY_SERVICE = EntityResolver.getEntity(DustDataLinks.EntityServices);
			DustEntity dlCtxListener = EntityResolver.getEntity(DustProcLinks.ContextChangeListeners);

			String cName = DustSandboxListenerDump.class.getName();
			DustEntity ba = Dust.getEntity("ListenerDump: " + cName);

			Dust.accessEntity(DataCommand.setValue, ba, daBinObj, cName, null);
			Dust.accessEntity(DataCommand.setRef, ba, dlBinImplSvc, dsListener, null);

			Dust.accessEntity(DataCommand.setRef, ContextRef.ctx, dlCtxBin, ba, null);

			// Dust.accessEntity(DataCommand.setValue, dsListener, daGenId,
			// "DustProcListener", null);
			// Dust.accessEntity(DataCommand.setValue, dcProcessChange, daGenId,
			// "ProcessChange", null);
			// Dust.accessEntity(DataCommand.setRef, dcProcessChange, dlGenOwner,
			// dsListener, null);

			DustEntity listener = Dust.getEntity("Dumper");
			Dust.accessEntity(DataCommand.setRef, listener, DR_ENTITY_SERVICE, dsListener, null);
			Dust.accessEntity(DataCommand.setRef, ContextRef.ctx, dlCtxListener, listener, null);

			inited = true;
		}
	}
}
