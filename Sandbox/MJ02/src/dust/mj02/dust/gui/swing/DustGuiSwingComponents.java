package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import dust.mj02.dust.gui.DustGuiComponents;
import dust.utils.DustUtilsSwingComponents;

public interface DustGuiSwingComponents extends DustGuiComponents, DustUtilsSwingComponents {
	Dimension ANCHOR_SIZE = new Dimension(16, 16);
	
	int ENTITY_PANEL_BORDER = 10;
	
	int HR = 6;

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
