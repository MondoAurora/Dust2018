package dust.mj02.montru.gui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class MontruGuiSwingPanelLinks extends JPanel implements MontruGuiSwingComponents {
	private static final long serialVersionUID = 1L;

	ArrayList<RefInfo> arrRefs = new ArrayList<>();
	EntityInfoResolver eiCompRes;

	Map<RefInfo, Line2D> lines = new HashMap<>();

	ComponentListener painter = new ComponentAdapter() {
		@Override
		public void componentMoved(ComponentEvent e) {
			refreshLines();
		}
		@Override
		public void componentResized(ComponentEvent e) {
			refreshLines();
		}
		@Override
		public void componentHidden(ComponentEvent e) {
			refreshLines();
		}
		@Override
		public void componentShown(ComponentEvent e) {
			refreshLines();
		}
	};

	public MontruGuiSwingPanelLinks(EntityInfoResolver eiCompRes, ArrayList<RefInfo> arrRefs) {
		setOpaque(false);
		this.arrRefs = arrRefs;
		this.eiCompRes = eiCompRes;
	}

	public void followContent(JComponent comp) {
		comp.addComponentListener(painter);
	}

	public void followParent(JComponent comp) {
		comp.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setSize(e.getComponent().getSize());
				refreshLines();
			}
		});
	}

	void hitTest(Point pt) {
		Rectangle hit = new Rectangle(pt.x - MontruGuiSwingFrame.HR, pt.y - MontruGuiSwingFrame.HR,
				2 * MontruGuiSwingFrame.HR, 2 * MontruGuiSwingFrame.HR);

		for (Map.Entry<RefInfo, Line2D> e : lines.entrySet()) {
			if (e.getValue().intersects(hit)) {
				RefInfo ri = e.getKey();
				ri.put(RefKey.selected, !ri.isTrue(RefKey.selected));
			}
		}

		repaint();
	}

	private void refreshLines() {
		lines.clear();

		for (RefInfo ri : arrRefs) {
			EntityInfo eiSrc = ri.get(RefKey.source);
			JComponent frmSource = eiCompRes.getEntityPanel(eiSrc);
			JComponent frmTarget = eiCompRes.getEntityPanel(ri.get(RefKey.target));

			if ((null != frmSource) && (null != frmTarget)) {
				JComponent comp = ((MontruGuiSwingPanelEntity) eiSrc.get(EntityKey.panel)).linkLabels.get(ri.get(RefKey.linkDef));

				if (null != comp) {
					Point ptSource = new Point(0, comp.getHeight() / 2);
					SwingUtilities.convertPointToScreen(ptSource, comp);
					Point ptTarget = new Point(0, 0);
					SwingUtilities.convertPointToScreen(ptTarget, frmTarget);

					SwingUtilities.convertPointFromScreen(ptSource, this);
					SwingUtilities.convertPointFromScreen(ptTarget, this);

					lines.put(ri, new Line2D.Float(ptSource, ptTarget));
				}
			}
		}

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Color col = g.getColor();

		for (Map.Entry<RefInfo, Line2D> e : lines.entrySet()) {
			Line2D line = e.getValue();
			g.setColor(e.getKey().isTrue(RefKey.selected) ? MontruGuiSwingFrame.COL_REF_SEL
					: MontruGuiSwingFrame.COL_REF_NORMAL);
			g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
		}

		g.setColor(col);
	}
}