package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import dust.mj02.montru.gui.MontruGuiComponents;

public interface MontruGuiSwingComponents extends MontruGuiComponents {

	Dimension INIT_FRAME_SIZE = new Dimension(1200, 600);
	Dimension ANCHOR_SIZE = new Dimension(16, 16);
	
	int ENTITY_PANEL_BORDER = 10;
	
	int HR = 6;
	Color COL_REF_SEL = Color.RED;
	Color COL_REF_NORMAL = Color.BLACK;

	enum AnchorLocation {
		Left(BorderLayout.WEST), Right(BorderLayout.EAST);
		
		final String swingConst;

		private AnchorLocation(String swingConst) {
			this.swingConst = swingConst;
		}
		
		public String getSwingConst() {
			return swingConst;
		}
	}
}
