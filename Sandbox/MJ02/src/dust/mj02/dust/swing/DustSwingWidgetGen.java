package dust.mj02.dust.swing;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;

public class DustSwingWidgetGen implements DustSwingWidgetComponents, DustProcComponents, DustGenericComponents {
	private static boolean inited = false;

	public static void init() {
		if (inited) {
			return;
		}
		
		String cName = DustSwingWidgetLabel.class.getName();
		DustEntity ba = Dust.getEntity("BinaryAssignment: " + cName);
		
		DustUtils.accessEntity(DataCommand.setValue, ba, DustProcAtts.BinaryObjectName, cName, null);
		DustUtils.accessEntity(DataCommand.setRef, ba, DustProcLinks.BinaryImplementedServices, DustSwingWidgetServices.Label, null);

		DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextBinaryAssignments, ba, null);


		DustUtils.accessEntity(DataCommand.setValue, DustSwingWidgetServices.Label, DustProcAtts.BinaryAutoInit, true);

		DustUtils.accessEntity(DataCommand.setRef, DustSwingWidgetServices.Label, DustGenericLinks.Extends, DustProcServices.Listener);
		DustUtils.accessEntity(DataCommand.setRef, DustSwingWidgetServices.Label, DustGenericLinks.Extends, DustProcServices.Active);

		inited = true;
	}
}
