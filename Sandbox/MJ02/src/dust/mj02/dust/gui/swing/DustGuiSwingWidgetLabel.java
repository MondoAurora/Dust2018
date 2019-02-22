package dust.mj02.dust.gui.swing;

import javax.swing.JLabel;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.gui.DustGuiComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsJava;

public class DustGuiSwingWidgetLabel extends JLabel implements DustGuiComponents, DustProcComponents.DustProcListener, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;
	
	public static DustGuiSwingWidgetLabel createWidget(DustEntity eEntity, DustEntity eData) {
		DustEntity eWidget = Dust.getEntity(null);
		DustEntity eSvc = EntityResolver.getEntity(DustGuiServices.Label);

		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustProcLinks.ChangeEntity, eEntity);
		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustProcLinks.ChangeKey, eData);
		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustDataLinks.EntityServices, eSvc);
		
		DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextChangeListeners, eWidget);

		
		return DustUtils.getBinary(eWidget, eSvc);
	}

	public DustGuiSwingWidgetLabel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		Object val = DustUtils.getMsgVal(DustProcAtts.ChangeNewValue, false);
//		DustUtilsDev.dump("updating label to", DustUtilsJava.toString(val));
		setText(DustUtilsJava.toString(val));
	}

	@Override
	public void dustProcActiveInit() throws Exception {
		DustRef e = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeEntity);
		DustRef a = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeKey);

		String val = DustUtilsJava.toString(DustUtils.accessEntity(DataCommand.getValue, e.get(RefKey.target), a.get(RefKey.target)));
		
		setText(val);
	}

	@Override
	public void dustProcActiveRelease() throws Exception {
		
	}
}
