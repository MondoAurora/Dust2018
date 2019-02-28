package dust.mj02.montru.gui.swing;

import java.awt.Color;
import java.awt.Dimension;

import dust.mj02.dust.gui.swing.DustGuiSwingComponents;
import dust.mj02.montru.gui.MontruGuiComponents;
import dust.utils.DustUtilsSwingComponents;

public interface MontruGuiSwingComponents extends MontruGuiComponents, DustUtilsSwingComponents, DustGuiSwingComponents {

	Dimension INIT_FRAME_SIZE = new Dimension(1200, 1000);
	
	Color COL_REF_SEL = Color.RED;
	Color COL_REF_NORMAL = Color.BLACK;
}
