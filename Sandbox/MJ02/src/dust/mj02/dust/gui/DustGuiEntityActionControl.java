package dust.mj02.dust.gui;

import dust.mj02.dust.Dust;
import dust.mj02.dust.gui.swing.DustGuiSwingComponents;

public abstract class DustGuiEntityActionControl<BaseComponentType> implements DustGuiSwingComponents {
	public enum DragItem {
		source, target
	}

	private GuiDataWrapper<BaseComponentType> dragSource;
	private GuiDataWrapper<BaseComponentType> dragTarget;

	protected void dragTargetEnter(Object comp) {
		if (null != dragSource) {
			setItem(DragItem.target, resolveBaseComponent(comp));
		}
	}

	protected void dragTargetLeave(Object comp) {
		if (null != dragSource) {
			if (dragTarget == resolveBaseComponent(comp)) {
				setItem(DragItem.target, null);
			}
		}
	}

	protected void dragSourceSet(Object comp) {
		setItem(DragItem.source, resolveBaseComponent(comp));
	}

	protected void drop() {
		if (null != dragSource) {
			if (null != dragTarget) {
				Dust.accessEntity(DataCommand.setRef, dragTarget.getEntity(), dragTarget.getData(),
						dragSource.getEntity(), null);
				dropped(dragSource, dragTarget);
				setItem(DragItem.target, null);
			} else {
				dropped(dragSource, null);
			}
			
			setItem(DragItem.source, null);
		}
	}
	
	private void setItem(DragItem item, GuiDataWrapper<BaseComponentType> gdw) {
		GuiDataWrapper<BaseComponentType> old = null;
		
		switch ( item ) {
		case source:
			old = dragSource;
			dragSource = gdw;
			break;
		case target:
			old = dragTarget;
			dragTarget = gdw;
			break;
		}
		
		dragItemChanged(item, old, gdw);
	}

	public abstract GuiDataWrapper<BaseComponentType> resolveBaseComponent(Object comp);

	public abstract void setDragTarget(GuiDataWrapper<? extends BaseComponentType> gdw);
	public abstract void setDragSource(GuiDataWrapper<? extends BaseComponentType> gdw);
	public abstract void setLabel(GuiDataWrapper<? extends BaseComponentType> gdw);
	public abstract void setRefList(GuiDataWrapper<? extends BaseComponentType> gdw);

	protected abstract void activateEntities(DustEntity... entities);
	protected abstract void dragItemChanged(DragItem item, GuiDataWrapper<BaseComponentType> gdwOld,
			GuiDataWrapper<BaseComponentType> gdwNew);
	protected abstract void dropped(GuiDataWrapper<BaseComponentType> gdwSource, GuiDataWrapper<BaseComponentType> gdwTarget);
}