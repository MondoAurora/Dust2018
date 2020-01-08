package dust.mj02.sandbox;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsDev;

public class DustSandboxListenerDump implements DustSandboxComponents, DustProcComponents.DustProcListener {

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		DustUtilsDev.dump("Dump change", EntityResolver.getKey(DustUtils.getMsgVal(DustCommLinks.ChangeItemCmd, true)),
				"\n :", DustUtils.getMsgVal(DustCommLinks.ChangeItemEntity, true), ".",
				DustUtils.getMsgVal(DustCommLinks.ChangeItemKey, true), "\n ",
				DustUtils.getMsgVal(DustCommAtts.ChangeItemOldValue, true), ">",
				DustUtils.getMsgVal(DustCommAtts.ChangeItemNewValue, true));
	}

}
