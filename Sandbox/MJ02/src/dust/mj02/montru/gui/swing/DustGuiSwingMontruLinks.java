package dust.mj02.montru.gui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import dust.mj02.dust.Dust;
import dust.mj02.dust.gui.swing.DustGuiSwingWidgetAnchor.AnchoredPanel;
import dust.mj02.montru.gui.swing.DustGuiSwingMontruDesktop.EntityDocWindow;

class DustGuiSwingMontruLinks extends JPanel implements DustGuiSwingMontruComponents {
	private static final long serialVersionUID = 1L;

	DustGuiSwingMontruDesktop desktop;

	// enum ShapeType {
	// line, arc
	// }
	//
	// class ShapeInfo {
	// DustRef ref;
	// ShapeType st;
	// Shape shape;
	//
	// public ShapeInfo(DustRef ref, Line2D shape) {
	// st = ShapeType.line;
	// this.shape = shape;
	// }
	//
	// public ShapeInfo(DustRef ref, Arc2D shape) {
	// st = ShapeType.arc;
	// this.shape = shape;
	// }
	//
	// public void draw(Graphics g) {
	// g.setColor(sel.contains(ref) ? COL_REF_SEL : COL_REF_NORMAL);
	//
	// ((Graphics2D)g).draw(shape);
	//
	//// switch (st) {
	//// case arc:
	//// Arc2D arc = (Arc2D) shape;
	//// g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(),
	// (int) line.getY2());
	//// break;
	//// case line:
	//// Line2D line = (Line2D) shape;
	//// g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(),
	// (int) line.getY2());
	//// break;
	//// }
	// }
	// }

	Map<DustRef, Shape> lines = new HashMap<>();
	Set<DustRef> sel = new HashSet<>();

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

	public DustGuiSwingMontruLinks(DustGuiSwingMontruDesktop desktop) {
		this.desktop = desktop;
		setOpaque(false);

		followParent(desktop);
	}

	void followContent(JComponent comp) {
		comp.addComponentListener(painter);
		refreshLines();
	}

	void followParent(JComponent comp) {
		comp.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setSize(e.getComponent().getSize());
				refreshLines();
			}
		});
	}

	public void hitTest(MouseEvent me) {
		if ((me.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
			sel.clear();
		}

		Point pt = me.getPoint();

		Rectangle hit = new Rectangle(pt.x - HR, pt.y - HR, 2 * HR, 2 * HR);

		for (Map.Entry<DustRef, Shape> e : lines.entrySet()) {
			if (e.getValue().intersects(hit)) {
				DustRef r = e.getKey();
				if (sel.contains(r)) {
					sel.remove(r);
				} else {
					sel.add(r);
				}
			}
		}

		repaint();
	}

	void refreshLines() {
		lines.clear();
		EnumMap<AnchorLocation, Point> aSrc = new EnumMap<>(AnchorLocation.class);
		EnumMap<AnchorLocation, Point> aTrg = new EnumMap<>(AnchorLocation.class);

		AnchorLocation[][] test = { { AnchorLocation.Left, AnchorLocation.Left },
				{ AnchorLocation.Left, AnchorLocation.Right }, { AnchorLocation.Right, AnchorLocation.Left },
				{ AnchorLocation.Right, AnchorLocation.Right } };

		Set<DustRef> lostRefs = new HashSet<>(sel);

		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustRef ref) {
				lostRefs.remove(ref);

				DustEntity eiSrc = ref.get(RefKey.source);
				DustEntity eiTarg = ref.get(RefKey.target);

				EntityDocWindow edwSrc = desktop.factDocWindows.peek(eiSrc);
				EntityDocWindow edwTrg = desktop.factDocWindows.peek(eiTarg);

				if ((null != edwSrc) && edwSrc.iFrame.isShowing() && (null != edwTrg) && edwTrg.iFrame.isShowing()) {
					AnchoredPanel apSrcLink = edwSrc.pnl.peekAnchored(ref.get(RefKey.linkDef));
					if ((null != apSrcLink) && apSrcLink.isShowing()) {
						apSrcLink.getAnchorCentersOnScreen(aSrc);
						edwTrg.pnl.peekAnchored(null).getAnchorCentersOnScreen(aTrg);

						if (!aSrc.isEmpty() && !aTrg.isEmpty()) {
							if (edwSrc != edwTrg) {
								Point pt0 = new Point();
								Point pt1 = new Point();
								int min = Integer.MAX_VALUE;

								for (AnchorLocation[] t : test) {
									int diff = Math.abs(aSrc.get(t[0]).x - aTrg.get(t[1]).x);
									if (diff < min) {
										min = diff;
										pt0.setLocation(aSrc.get(t[0]));
										pt1.setLocation(aTrg.get(t[1]));
									}
								}

								SwingUtilities.convertPointFromScreen(pt0, DustGuiSwingMontruLinks.this);
								SwingUtilities.convertPointFromScreen(pt1, DustGuiSwingMontruLinks.this);

								lines.put(ref, new Line2D.Float(pt0, pt1));
							} else {
								Point pt0 = new Point(aTrg.get(AnchorLocation.Left));
								Point pt1 = new Point(aSrc.get(AnchorLocation.Left));
								Point orig = edwTrg.pnl.getLocation(null);
								SwingUtilities.convertPointToScreen(orig, edwTrg.pnl);

								int h = pt1.y - pt0.y;
								int w = 4 * (pt0.x - orig.x);
								
								SwingUtilities.convertPointFromScreen(pt0, DustGuiSwingMontruLinks.this);
								SwingUtilities.convertPointFromScreen(pt1, DustGuiSwingMontruLinks.this);
								lines.put(ref,
										new Arc2D.Float(pt0.x - (w / 2), pt0.y, w, h, 90.0f, 180.0f, Arc2D.OPEN));
							}
						}
					}
				}
			}
		}, null, null, null);

		for (DustRef rr : lostRefs) {
			sel.remove(rr);
		}

		repaint();
	}

	public void removeSelRefs() {
		for (DustRef rr : sel) {
			Dust.accessEntity(DataCommand.removeRef, rr.get(RefKey.source), rr.get(RefKey.linkDef),
					rr.get(RefKey.target), rr.get(RefKey.key));
		}

		refreshLines();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Color col = g.getColor();

		for (Map.Entry<DustRef, Shape> e : lines.entrySet()) {
			g.setColor(sel.contains(e.getKey()) ? COL_REF_SEL : COL_REF_NORMAL);
			((Graphics2D) g).draw(e.getValue());
		}

		// for (ShapeInfo si : lines.values()) {
		// si.draw(g);
		// // Line2D line = e.getValue();
		// // g.setColor(sel.contains(e.getKey()) ? COL_REF_SEL : COL_REF_NORMAL);
		// // g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(),
		// (int)
		// // line.getY2());
		// }

		g.setColor(col);
	}
}