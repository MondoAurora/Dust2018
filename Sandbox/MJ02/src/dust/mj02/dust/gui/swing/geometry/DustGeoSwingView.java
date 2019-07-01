package dust.mj02.dust.gui.swing.geometry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.geometry.DustGeometryComponents;
import dust.mj02.dust.gui.swing.DustGuiSwingComponents.EntitySpecPanelBase;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.utils.DustUtilsSwingComponents.DustSwingCommandManager;

public class DustGeoSwingView extends EntitySpecPanelBase implements DustGeometryComponents, DustKernelComponents {
    private static final long serialVersionUID = 1L;

    class DrawPanel extends JComponent {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (null != mapShapes) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.transform(translate);

                for (Shape s : mapShapes.values()) {
                    g2d.draw(s);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return drawSize;
        }
    }

    enum RendererPanelCmds {
        Refresh
    }

    JScrollPane scpMain;
    DrawPanel drawPanel;

    DustEntity eMsg;
    DustEntity eRenderTarget;
    Dimension drawSize;
    int margin;
    AffineTransform translate;

    Map<Object, Shape> mapShapes;

    DustSwingCommandManager<RendererPanelCmds> cm = new DustSwingCommandManager<RendererPanelCmds>(RendererPanelCmds.class) {
        @Override
        protected void execute(RendererPanelCmds cmd) throws Exception {
            switch (cmd) {
            case Refresh:
                reloadData();
                break;
            }
        }

        public void updateStates() {
        };
    };

    public DustGeoSwingView(DustEntity eRoot) {
        super(eRoot);

        drawSize = new Dimension(500, 200);
        drawPanel = new DrawPanel();

        scpMain = new JScrollPane(drawPanel);

        add(scpMain, BorderLayout.CENTER);

        cm.createButtonPanelDefault(this);
    }

    public void reloadData() {
        if (null == eMsg) {
            eRenderTarget = DustUtils.accessEntity(DataCommand.getEntity, DustGeometryTypes.RenderTarget);

            eMsg = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);
            DustUtils.accessEntity(DataCommand.setRef, eMsg, DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);
            DustUtils.accessEntity(DataCommand.setRef, eMsg, DustGenericLinks.ContextAwareEntity, entity);
        }

        mapShapes = DustUtils.accessEntity(DataCommand.tempSend, eRenderTarget, eMsg);

        Rectangle bb = new Rectangle();

        for (Shape s : mapShapes.values()) {
            Rectangle b = s.getBounds();
            bb.add(b);
        }

        margin = (int) (0.2 * Math.min(bb.height, bb.width));

        int m2 = 2 * margin;

        drawSize.setSize(bb.width + m2, bb.height + m2);
        translate = AffineTransform.getTranslateInstance(margin - bb.getMinX(), margin - bb.getMinY());

        drawPanel.invalidate();
        repaint();
    }
}
