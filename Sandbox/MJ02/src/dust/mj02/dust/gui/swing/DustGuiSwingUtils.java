package dust.mj02.dust.gui.swing;

import javax.swing.JComponent;

import dust.mj02.dust.gui.DustGuiComponents;
import dust.utils.DustUtilsSwingComponents;

public class DustGuiSwingUtils implements DustGuiComponents, DustUtilsSwingComponents {
	
	private static final String CLIENT_PROP_KEY = "DustCPDataWrapper";
	
	public static void initJComponent(GuiDataWrapper<? extends JComponent> gdw) {
		gdw.getComponent().putClientProperty(CLIENT_PROP_KEY, gdw);
	}
	
	@SuppressWarnings("unchecked")
	static GuiDataWrapper<JComponent> getDataWrapper(Object comp) {
		return (GuiDataWrapper<JComponent>) ((JComponent)comp).getClientProperty(CLIENT_PROP_KEY);
	}

}
