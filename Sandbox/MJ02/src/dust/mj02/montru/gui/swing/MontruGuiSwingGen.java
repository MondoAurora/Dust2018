package dust.mj02.montru.gui.swing;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.gui.DustGuiComponents;
import dust.mj02.dust.gui.swing.DustGuiSwingGen;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.mj02.montru.gui.MontruGuiComponents;

public class MontruGuiSwingGen implements DustGuiComponents, MontruGuiComponents, DustProcComponents, DustGenericComponents {
	private static boolean inited = false;

	public static void init() {
		if (inited) {
			return;
		}
		
		DustGuiSwingGen.init();

		DustUtils.registerService(DustGuiSwingMontruDesktop.class, true, MontruGuiServices.MontruDesktop,
				DustProcServices.Listener, DustProcServices.Active);

		DustUtils.accessEntity(DataCommand.setRef, MontruGuiTypes.MontruDesktop, DustMetaLinks.TypeLinkedServices,
				MontruGuiServices.MontruDesktop);
		
		inited = true;
	}
}
