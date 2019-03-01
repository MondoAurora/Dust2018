package dust.mj02.dust.gui.swing;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.EnumSet;

import javax.swing.JComponent;

import dust.mj02.dust.gui.DustGuiComponents;
import dust.utils.DustUtilsSwingComponents;

public class DustGuiSwingUtils implements DustGuiComponents, DustUtilsSwingComponents {
	
	private static final String CLIENT_PROP_KEY = "DustCPDataWrapper";
	
	public static void initJComponent(GuiDataWrapper<? extends JComponent> gdw) {
		gdw.getComponent().putClientProperty(CLIENT_PROP_KEY, gdw);
	}
	
	@SuppressWarnings("unchecked")
	public static GuiDataWrapper<JComponent> getDataWrapper(Object comp) {
		return (GuiDataWrapper<JComponent>) ((JComponent)comp).getClientProperty(CLIENT_PROP_KEY);
	}

	public static EnumSet<CtrlStatus> getMouseStatus(MouseEvent me) {
		EnumSet<CtrlStatus> ret = EnumSet.noneOf(CtrlStatus.class);
		
		int mex = me.getModifiersEx();
		
		if ((mex & InputEvent.CTRL_DOWN_MASK) != 0) {
			ret.add(CtrlStatus.ctrl);
		}
		if ((mex & InputEvent.ALT_DOWN_MASK) != 0) {
			ret.add(CtrlStatus.alt);
		}
		if ((mex & InputEvent.SHIFT_DOWN_MASK) != 0) {
			ret.add(CtrlStatus.shift);
		}

		return ret;
	}
}
