package dust.mj02.dust.gui.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;

import javax.swing.JComponent;

import dust.mj02.dust.Dust;
import dust.mj02.dust.gui.DustGuiEntityActionControl;

public abstract class DustGuiSwingEntityActionControl extends DustGuiEntityActionControl<JComponent>
		implements DustGuiSwingComponents {
	private final MouseListener mlDragTarget = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			dragTargetEnter((JComponent) e.getSource());
		}

		@Override
		public void mouseExited(MouseEvent e) {
			dragTargetLeave((JComponent) e.getSource());
		}
	};

	private final MouseMotionListener mmlDragSource = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			dragSourceSet(e.getSource());
			handleDragEvent(e);
		}
	};

	private final MouseListener mlDragSource = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			handleDragEvent(null);
			drop();
		}
	};

	private final MouseListener mlLabelActivator = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (1 < e.getClickCount()) {
				activateEntities(resolveBaseComponent(e.getSource()).getEntity());
			}
		}
	};

	private final MouseListener mlLinkActivator = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (1 < e.getClickCount()) {
				GuiDataWrapper<JComponent> gdw = resolveBaseComponent(e.getSource());
				HashSet<DustEntity> refs = new HashSet<>();
				Dust.accessEntity(DataCommand.processRef, gdw.getEntity(), gdw.getData(), new RefProcessor() {

					@Override
					public void processRef(DustRef ref) {
						refs.add(ref.get(RefKey.target));
					}
				}, null);

				if (!refs.isEmpty()) {
					activateEntities(refs.toArray(new DustEntity[refs.size()]));
				}
			}
		}
	};
	
	protected void handleDragEvent(MouseEvent me) {
		
	}

	@Override
	public GuiDataWrapper<JComponent> resolveBaseComponent(Object comp) {
		return DustGuiSwingUtils.getDataWrapper(comp);
	}

	@Override
	public void setDragTarget(GuiDataWrapper<? extends JComponent> gdw) {
		DustGuiSwingUtils.initJComponent(gdw);
		gdw.getComponent().addMouseListener(mlDragTarget);
	}

	@Override
	public void setDragSource(GuiDataWrapper<? extends JComponent> gdw) {
		DustGuiSwingUtils.initJComponent(gdw);
		gdw.getComponent().addMouseMotionListener(mmlDragSource);
		gdw.getComponent().addMouseListener(mlDragSource);
	}

	@Override
	public void setLabel(GuiDataWrapper<? extends JComponent> gdw) {
		DustGuiSwingUtils.initJComponent(gdw);
		gdw.getComponent().addMouseListener(mlLabelActivator);
	}

	@Override
	public void setRefList(GuiDataWrapper<? extends JComponent> gdw) {
		DustGuiSwingUtils.initJComponent(gdw);
		gdw.getComponent().addMouseListener(mlLinkActivator);
	}
}