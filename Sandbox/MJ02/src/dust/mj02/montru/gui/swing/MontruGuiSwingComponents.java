package dust.mj02.montru.gui.swing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;

import dust.mj02.montru.gui.MontruGuiComponents;

public interface MontruGuiSwingComponents extends MontruGuiComponents {

	Dimension INIT_FRAME_SIZE = new Dimension(800, 400);
	int HR = 6;
	Color COL_REF_SEL = Color.RED;
	Color COL_REF_NORMAL = Color.BLACK;

	interface EntitySwingCompResolver {
		JComponent getEntityPanel(GuiEntityInfo ei);
	}

}
