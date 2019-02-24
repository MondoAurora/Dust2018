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
			DustUtils.registerService(DustSandboxListenerDump.class, false, DustSandboxServices.SandboxChangeDump, DustProcServices.Listener);

			DustEntity listener = Dust.getEntity("Dumper");
			DustUtils.accessEntity(DataCommand.setRef, listener, DustDataLinks.EntityServices, DustSandboxServices.SandboxChangeDump);
			DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextChangeListeners, listener);
			
			inited = true;
		}
	}
}
