package dust.mj02.dust.gui.swing;

import javax.swing.JLabel;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.gui.DustGuiComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsJava;

public class DustGuiSwingWidgetLabel extends JLabel implements DustGuiComponents, 
		DustProcComponents.DustProcListener, DustProcComponents.DustProcActive, 
		DustGuiComponents.GuiDataWrapper<JLabel>  {
	
	private static final long serialVersionUID = 1L;
	
	DustEntity eEntity;
	DustEntity eData;

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
	public void activeInit() throws Exception {
		DustRef e = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeEntity);
		DustRef a = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeKey);

		eEntity = e.get(RefKey.target);
		eData = a.get(RefKey.target);

		String val = DustUtilsJava.toString(DustUtils.accessEntity(DataCommand.getValue, e.get(RefKey.target), a.get(RefKey.target)));
		
		setText(val);
		
		DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextChangeListeners, ContextRef.self);
	}

	@Override
	public void activeRelease() throws Exception {
		
	}
	
	@Override
	public DustEntity getEntity() {
		return eEntity;
	}
	@Override
	public DustEntity getData() {
		return eData;
	}
	@Override
	public JLabel getComponent() {
		return this;
	}

}
