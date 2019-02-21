package dust.mj02.dust.swing;

import javax.swing.JLabel;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.knowledge.DustProcComponents.DustProcAtts;
import dust.mj02.dust.knowledge.DustProcComponents.DustProcLinks;
import dust.utils.DustUtilsJava;

public class DustGuiSwingWidgetLabel extends JLabel implements DustGuiSwingComponents, DustProcComponents.DustProcListener, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;
	
	public static JLabel createWidget(DustEntity eEntity, DustEntity eData) {
		DustEntity eWidget = Dust.getEntity(null);
		DustEntity eSvc = EntityResolver.getEntity(DustSwingWidgetServices.Label);

		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustProcLinks.ChangeEntity, eEntity);
		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustProcLinks.ChangeKey, eData);
		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustDataLinks.EntityServices, eSvc);
		
		return DustUtils.getBinary(eWidget, eSvc);
	}

	public DustGuiSwingWidgetLabel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		setText(DustUtils.getMsgVal(DustProcAtts.ChangeNewValue, false));
	}

	@Override
	public void dustProcActiveInit() throws Exception {
//		DustEntity e = DustUtils.getMsgVal(DustProcLinks.ChangeEntity, true);
//		DustEntity a = DustUtils.getMsgVal(DustProcLinks.ChangeKey, true);

		DustRef e = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeEntity);
		DustRef a = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeKey);

		String val = DustUtilsJava.toString(DustUtils.accessEntity(DataCommand.getValue, e.get(RefKey.target), a.get(RefKey.target)));
		
		setText(val);
	}

	@Override
	public void dustProcActiveRelease() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
