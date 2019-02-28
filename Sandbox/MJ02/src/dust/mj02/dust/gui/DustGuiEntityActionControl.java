package dust.mj02.dust.gui;

import dust.mj02.dust.Dust;
import dust.mj02.dust.gui.swing.DustGuiSwingComponents;

public abstract class DustGuiEntityActionControl<BaseComponentType> implements DustGuiSwingComponents {
	private GuiDataWrapper<BaseComponentType> dragSource;
	private GuiDataWrapper<BaseComponentType> dragTarget;
	
	protected void dragTargetEnter(Object comp) {
		if (null != dragSource) {
			dragTarget = resolveBaseComponent(comp);
		}
	}

	protected void dragTargetLeave(Object comp) {
		if (null != dragSource) {
			if (dragTarget == resolveBaseComponent(comp)) {
				dragTarget = null;
			}
		}
	}
	
	protected void dragSourceSet(Object comp) {
		dragSource = resolveBaseComponent(comp);
	}
	
	protected void drop() {
		if ((null != dragTarget) && (null != dragSource)) {
			Dust.accessEntity(DataCommand.setRef, dragTarget.getEntity(), dragTarget.getData(), dragSource.getEntity(),
					null);
		}

		dragSource = dragTarget = null;
	}

	public abstract GuiDataWrapper<BaseComponentType> resolveBaseComponent(Object comp);

	public abstract void setDragTarget(GuiDataWrapper<? extends BaseComponentType> gdw);
	public abstract void setDragSource(GuiDataWrapper<? extends BaseComponentType> gdw);
	public abstract void setLabel(GuiDataWrapper<? extends BaseComponentType> gdw);
	public abstract void setRefList(GuiDataWrapper<? extends BaseComponentType> gdw);
	
	protected abstract void activateEntities(DustEntity... entities);
}