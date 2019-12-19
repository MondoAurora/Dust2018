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
import java.awt.geom.Ellipse2D;
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
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.gui.swing.DustGuiSwingWidgetAnchor.AnchoredPanel;
import dust.mj02.montru.gui.swing.DustGuiSwingMontruDesktop.EntityDocWindow;
import dust.utils.DustUtilsMuteManager;
import dust.utils.DustUtilsMuteManager.MutableModule;

class DustGuiSwingMontruLinks extends JPanel implements DustGuiSwingMontruComponents, DustUtilsMuteManager.Mutable {
    private static final long serialVersionUID = 1L;
    
    DustGuiSwingMontruDesktop desktop;

    Map<DustRef, Shape> lines = new HashMap<>();
    Set<DustRef> sel = new HashSet<>();
    Set<Shape> meta = new HashSet<>();

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
    
    @Override
    public MutableModule getModule() {
        return MutableModule.GUI;
    }
    
    @Override
    public void muteReleased() {
        refreshLines();
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
        if ( DustUtilsMuteManager.isMuted(this) ) {
            return;
        }
        
        meta.clear();
        lines.clear();

        EnumMap<AnchorLocation, Point> aSrc = new EnumMap<>(AnchorLocation.class);
        EnumMap<AnchorLocation, Point> aTrg = new EnumMap<>(AnchorLocation.class);

        AnchorLocation[][] test = { { AnchorLocation.Left, AnchorLocation.Left }, { AnchorLocation.Left, AnchorLocation.Right },
                { AnchorLocation.Right, AnchorLocation.Left }, { AnchorLocation.Right, AnchorLocation.Right } };

        Set<DustRef> lostRefs = new HashSet<>(sel);

        Dust.processRefs(new RefProcessor() {

            @Override
            public void processRef(DustRef ref) {
                DustEntity eiSrc = ref.get(RefKey.source);
                DustEntity eiTarg = ref.get(RefKey.target);

                DustEntity srcType = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, eiSrc, DustMetaLinks.LinkDefParent)).get(RefKey.target);

                EntityDocWindow edwSrc = desktop.factDocWindows.peek(srcType);
                EntityDocWindow edwTrg = desktop.factDocWindows.peek(eiTarg);

                if ((null != edwSrc) && edwSrc.iFrame.isShowing() && (null != edwTrg) && edwTrg.iFrame.isShowing()) {
                    edwSrc.pnl.peekAnchored(null).getAnchorCentersOnScreen(aSrc);
                    edwTrg.pnl.peekAnchored(null).getAnchorCentersOnScreen(aTrg);

                    Shape shp = optCreateLine(aSrc, aTrg, test, ref, edwSrc, edwTrg);
                    
                    if (null != shp) {
                        meta.add(shp);
                        double[] pts = new double[6];
                        shp.getPathIterator(null).currentSegment(pts);
                        Ellipse2D endDot = new Ellipse2D.Double(pts[0] - 4, pts[1] - 4, 8.0, 8.0);
                        meta.add(endDot);                        
                    }
                }

            }
        }, null, EntityResolver.getEntity(DustMetaLinks.LinkDefItemTypePrimary), null);

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

                        Shape shp = optCreateLine(aSrc, aTrg, test, ref, edwSrc, edwTrg);
                        if (null != shp) {
                            lines.put(ref, shp);
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
            Dust.accessEntity(DataCommand.removeRef, rr.get(RefKey.source), rr.get(RefKey.linkDef), rr.get(RefKey.target), rr.get(RefKey.key));
        }

        refreshLines();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color col = g.getColor();

        Graphics2D graphics2d = (Graphics2D) g;
        
        for (Map.Entry<DustRef, Shape> e : lines.entrySet()) {
            g.setColor(sel.contains(e.getKey()) ? COL_REF_SEL : COL_REF_NORMAL);
            graphics2d.draw(e.getValue());
        }

        g.setColor(COL_REF_META);
        for (Shape ms : meta) {
            graphics2d.draw(ms);

            if ( ms instanceof Ellipse2D ) {
                graphics2d.fill(ms);
            }
        }

        g.setColor(col);
    }

    public Shape optCreateLine(EnumMap<AnchorLocation, Point> aSrc, EnumMap<AnchorLocation, Point> aTrg, AnchorLocation[][] test, DustRef ref,
            EntityDocWindow edwSrc, EntityDocWindow edwTrg) {
        Shape shp = null;

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

                shp = new Line2D.Float(pt0, pt1);
            } else {
                Point pt0 = new Point(aTrg.get(AnchorLocation.Left));
                Point pt1 = new Point(aSrc.get(AnchorLocation.Left));
                Point orig = edwTrg.pnl.getLocation(null);
                SwingUtilities.convertPointToScreen(orig, edwTrg.pnl);

                int h = pt1.y - pt0.y;
                int w = 4 * (pt0.x - orig.x);

                SwingUtilities.convertPointFromScreen(pt0, DustGuiSwingMontruLinks.this);
                SwingUtilities.convertPointFromScreen(pt1, DustGuiSwingMontruLinks.this);
                shp = new Arc2D.Float(pt0.x - (w / 2), pt0.y, w, h, 90.0f, 180.0f, Arc2D.OPEN);
            }
        }

        return shp;
    }
}
