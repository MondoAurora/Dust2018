package dust.mj02.montru.gui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
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

	Map<DustRef, Line2D> lines = new HashMap<>();
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
		
		Rectangle hit = new Rectangle(pt.x - HR, pt.y - HR,	2 * HR, 2 * HR);

		for (Map.Entry<DustRef, Line2D> e : lines.entrySet()) {
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
						}
					}
				}
			}
		}, null, null, null);
		
		for ( DustRef rr: lostRefs ) {
			sel.remove(rr);
		}

		repaint();
	}
	
	
	public void removeSelRefs() {
		for ( DustRef rr : sel ) {
			Dust.accessEntity(DataCommand.removeRef, rr.get(RefKey.source), rr.get(RefKey.linkDef), rr.get(RefKey.target), rr.get(RefKey.key));
		}
		
		refreshLines();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Color col = g.getColor();

		for (Map.Entry<DustRef, Line2D> e : lines.entrySet()) {
			Line2D line = e.getValue();
			g.setColor(sel.contains(e.getKey()) ? COL_REF_SEL : COL_REF_NORMAL);
			g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
		}

		g.setColor(col);
	}

//	// A basic implementation of redispatching events.
//	private void redispatchMouseEvent(MouseEvent e) {
//		Point glassPanePoint = e.getPoint();
//		Container container = getParent();
//		Point containerPoint = SwingUtilities.convertPoint(this, glassPanePoint, container);
//		Component component = SwingUtilities.getDeepestComponentAt(container, containerPoint.x, containerPoint.y);
//
//		if ((component != null) && (this != component)) {
//			Point componentPoint = SwingUtilities.convertPoint(this, glassPanePoint, component);
//			component.dispatchEvent(new MouseEvent(component, e.getID(), e.getWhen(), e.getModifiers(),
//					componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger()));
//		}
//
//	}
}