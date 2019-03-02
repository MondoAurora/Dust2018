package dust.mj02.dust.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.gui.swing.DustGuiSwingComponents;
import dust.utils.DustUtilsJava;

public abstract class DustGuiEntityActionControl<BaseComponentType> implements DustGuiSwingComponents {
	public enum DragItem {
		source, target
	}

	private GuiDataWrapper<BaseComponentType> dragSource;
	private GuiDataWrapper<BaseComponentType> dragTarget;

	protected ArrayList<DustEntity> arrTypes = new ArrayList<>();
	protected Set<DustEntity> selected = new HashSet<>();

	public Collection<DustEntity> getAllTypes() {
		return arrTypes;
	}

	public Collection<DustEntity> getAllSelected() {
		return selected;
	}

	public boolean types(CollectionAction action, DustEntity e) {
		return DustUtilsJava.manageCollection(action, arrTypes, e);
	}

	public boolean select(CollectionAction action, DustEntity e) {
		return DustUtilsJava.manageCollection(action, selected, e);
	}

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

	protected void drop(EnumSet<CtrlStatus> ctrlStatus) {
		if (null != dragSource) {
			if (null != dragTarget) {
				Dust.accessEntity(DataCommand.setRef, dragTarget.getEntity(), dragTarget.getData(),
						dragSource.getEntity(), null);
				dropped(ctrlStatus, dragSource, dragTarget);
				setItem(DragItem.target, null);
			} else {
				dropped(ctrlStatus, dragSource, null);
			}

			setItem(DragItem.source, null);
		}
	}

	private void setItem(DragItem item, GuiDataWrapper<BaseComponentType> gdw) {
		GuiDataWrapper<BaseComponentType> old = null;

		switch (item) {
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
	protected abstract void dropped(EnumSet<CtrlStatus> ctrlStatus, GuiDataWrapper<BaseComponentType> gdwSource,
			GuiDataWrapper<BaseComponentType> gdwTarget);
}