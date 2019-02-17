package dust.utils;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class DustUtilsJavaSwing extends DustUtilsJava implements DustUtilsSwingComponents {
	
	public static JComponent setBorder(JComponent comp, String title, Color color) {
		comp.setBorder(new TitledBorder(BorderFactory.createLineBorder(color), title));
		return comp;
	}
	
	public static JComponent setBorder(JComponent comp, String title) {
		return setBorder(comp, title, Color.BLACK);
	}
	
	public static JScrollPane setBorderScroll(JComponent comp, String title, Color line) {
		JScrollPane scp = new JScrollPane(comp);
		setBorder(scp, title, line);
		return scp;
	}
	
	public static JScrollPane setBorderScroll(JComponent comp, String title) {
		return setBorderScroll(comp, title, Color.BLACK);
	}
	

}
