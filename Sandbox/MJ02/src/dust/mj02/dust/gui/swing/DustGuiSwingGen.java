package dust.mj02.dust.gui.swing;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.gui.DustGuiComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;

public class DustGuiSwingGen implements DustGuiComponents, DustProcComponents, DustGenericComponents {
	private static boolean inited = false;

	public static void init() {
		if (inited) {
			return;
		}

		DustUtils.registerService(DustGuiSwingWidgetLabel.class, true, DustGuiServices.Label, DustProcServices.Listener,
				DustProcServices.Active);
		DustUtils.registerService(DustGuiSwingWidgetTextField.class, true, DustGuiServices.TextField,
				DustProcServices.Listener, DustProcServices.Active);
		DustUtils.registerService(DustGuiSwingPanelEntity.class, true, DustGuiServices.PropertyPanel,
				DustProcServices.Listener, DustProcServices.Active);
//		DustUtils.registerService(DustGuiSwingMontruDesktop.class, true, DustGuiServices.MontruDesktop,
//				DustProcServices.Listener, DustProcServices.Active);

//		DustUtils.accessEntity(DataCommand.setRef, DustGuiTypes.MontruDesktop, DustMetaLinks.TypeLinkedServices,
//				DustGuiServices.MontruDesktop);
		DustUtils.accessEntity(DataCommand.setRef, DustGuiTypes.PropertyPanel, DustMetaLinks.TypeLinkedServices,
				DustGuiServices.PropertyPanel);
		DustUtils.accessEntity(DataCommand.setRef, DustGuiTypes.TextField, DustMetaLinks.TypeLinkedServices,
				DustGuiServices.TextField);
		DustUtils.accessEntity(DataCommand.setRef, DustGuiTypes.Label, DustMetaLinks.TypeLinkedServices,
				DustGuiServices.Label);
		
		inited = true;
	}
}
