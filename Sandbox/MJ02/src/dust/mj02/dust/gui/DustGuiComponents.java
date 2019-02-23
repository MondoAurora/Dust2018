package dust.mj02.dust.gui;

import dust.mj02.dust.DustComponents;
import dust.mj02.dust.knowledge.DustDataComponents;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;

public interface DustGuiComponents extends DustComponents, DustGenericComponents, DustDataComponents, DustMetaComponents, DustProcComponents {
	
	enum DustGuiLinks implements DustEntityKey {
		PropertyPanelEntity
	};

	enum DustGuiServices implements DustEntityKey {
		Label, TextField, PropertyPanel
	};

}
