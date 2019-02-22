package dust.mj02.montru.gui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class MontruGuiSwingPanelLinks extends JPanel implements MontruGuiSwingComponents {
	private static final long serialVersionUID = 1L;

	MontruGuiSwingPanelEditor editor;

	Map<GuiRefInfo, Line2D> lines = new HashMap<>();

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

	public MontruGuiSwingPanelLinks(MontruGuiSwingPanelEditor editor) {
		setOpaque(false);
		this.editor = editor;
	}

	void followContent(JComponent comp) {
		comp.addComponentListener(painter);
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

	void hitTest(Point pt) {
		Rectangle hit = new Rectangle(pt.x - MontruGuiSwingFrame.HR, pt.y - MontruGuiSwingFrame.HR,
				2 * MontruGuiSwingFrame.HR, 2 * MontruGuiSwingFrame.HR);

		for (Map.Entry<GuiRefInfo, Line2D> e : lines.entrySet()) {
			if (e.getValue().intersects(hit)) {
				GuiRefInfo ri = e.getKey();
				ri.put(GuiRefKey.selected, !ri.isTrue(GuiRefKey.selected));
			}
		}

		repaint();
	}

	void refreshLines() {
		lines.clear();
		EnumMap<AnchorLocation, Point> aSrc = new EnumMap<>(AnchorLocation.class);
		EnumMap<AnchorLocation, Point> aTrg = new EnumMap<>(AnchorLocation.class);
		
		AnchorLocation[][] test = {{AnchorLocation.Left, AnchorLocation.Left}, 
				{AnchorLocation.Left, AnchorLocation.Right}, 
				{AnchorLocation.Right, AnchorLocation.Left}, 
				{AnchorLocation.Right, AnchorLocation.Right} };

		for (Object o : ((Collection<?>)editor.getEditorModel().getAllRefs()).toArray() ) {
			GuiRefInfo ri = (GuiRefInfo) o;
			GuiEntityInfo eiSrc = ri.get(GuiRefKey.source);
			JComponent frmSource = editor.getEntityPanel(eiSrc);
			GuiEntityInfo eiTarg = ri.get(GuiRefKey.target);
			JComponent frmTarget = editor.getEntityPanel(eiTarg);

			if ((null != frmSource) && frmSource.isShowing() && (null != frmTarget) && frmTarget.isShowing()) {
				((MontruGuiSwingPanelEntity) eiSrc.get(GuiEntityKey.panel)).getAnchorOnScreen(aSrc, ri.get(GuiRefKey.linkDef));
				((MontruGuiSwingPanelEntity) eiTarg.get(GuiEntityKey.panel)).getAnchorOnScreen(aTrg, null);

				if ( !aSrc.isEmpty() && !aTrg.isEmpty() ) {
					Point pt0 = new Point();
					Point pt1 = new Point();
					int min = Integer.MAX_VALUE;
					
					for ( AnchorLocation[] t : test ) {
						int diff = Math.abs(aSrc.get(t[0]).x - aTrg.get(t[1]).x);
						if ( diff < min ) {
							min = diff;
							pt0.setLocation(aSrc.get(t[0]));
							pt1.setLocation(aTrg.get(t[1]));
						}
					}
					
					SwingUtilities.convertPointFromScreen(pt0, this);
					SwingUtilities.convertPointFromScreen(pt1, this);
	
					lines.put(ri, new Line2D.Float(pt0, pt1));
				}				
			}
		}

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Color col = g.getColor();

		for (Map.Entry<GuiRefInfo, Line2D> e : lines.entrySet()) {
			Line2D line = e.getValue();
			g.setColor(e.getKey().isTrue(GuiRefKey.selected) ? MontruGuiSwingFrame.COL_REF_SEL
					: MontruGuiSwingFrame.COL_REF_NORMAL);
			g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
		}

		g.setColor(col);
	}
}