package dust.mj02.dust.gui;

import dust.mj02.dust.DustComponents;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.utils.DustUtilsSwingComponents;

public interface DustGuiComponents extends DustComponents, DustKernelComponents, DustUtilsSwingComponents {
	
	enum DustGuiUnits implements DustEntityKey {
		DustGui, DustGuiSwing
	};

	
	enum DustGuiLinks implements DustEntityKey {
		PropertyPanelEntity,// MontruDesktopActivePanel
	};

	enum DustGuiTypes implements DustEntityKey {
		Label, TextField, PropertyPanel,// MontruDesktop
	};

	enum DustGuiServices implements DustEntityKey {
		Label, TextField, PropertyPanel,// MontruDesktop
	};
	
    enum DustGuiTags implements DustEntityKey {
        ItemHidden
    }


	interface GuiComponentControl <ComponentType> {
		ComponentType getComponent();
	}

	interface GuiDataWrapper<ComponentType> extends GuiComponentControl<ComponentType>{
		DustEntity getEntity();
		DustEntity getData();
	}
	
	public enum CtrlStatus {
		alt, shift, ctrl;
	}

}
