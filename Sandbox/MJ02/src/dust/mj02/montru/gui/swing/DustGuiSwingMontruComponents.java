package dust.mj02.montru.gui.swing;

import java.awt.Color;
import java.awt.Dimension;

import dust.mj02.dust.gui.swing.DustGuiSwingComponents;
import dust.mj02.montru.gui.MontruGuiComponents;

public interface DustGuiSwingMontruComponents extends DustGuiSwingComponents, MontruGuiComponents {
	Dimension INIT_FRAME_SIZE = new Dimension(1200, 800);
	
	Color COL_DRAGLINE = Color.MAGENTA;
	Color COL_REF_SEL = Color.RED;
    Color COL_REF_NORMAL = Color.BLACK;
    Color COL_REF_META = Color.BLUE;
	
	String CFG_MONTRU = "Montru";
	String CFG_LASTUNIT = "lastLoadedUnit";
}
