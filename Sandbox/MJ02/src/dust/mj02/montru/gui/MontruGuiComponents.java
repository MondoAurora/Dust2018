package dust.mj02.montru.gui;

import dust.mj02.dust.DustComponents;

public interface MontruGuiComponents extends DustComponents {
	
	enum MontruGuiUnits implements DustEntityKey {
		MontruGui
	};


	enum MontruGuiLinks implements DustEntityKey {
		MontruDesktopActivePanel
	};

	enum MontruGuiTypes implements DustEntityKey {
		MontruDesktop
	};

	enum MontruGuiServices implements DustEntityKey {
		MontruDesktop
	};
}
