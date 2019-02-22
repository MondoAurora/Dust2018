package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dust.mj02.dust.Dust;

@SuppressWarnings("serial")
public class DustGuiSwingWidgetAnchor extends JLabel implements DustGuiSwingComponents {
	public static class Connector {
		private DustGuiSwingWidgetAnchor dragSource;
		private DustGuiSwingWidgetAnchor dragTarget;

		private final MouseListener mlDragTarget = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (null != dragSource) {
					dragTarget = (DustGuiSwingWidgetAnchor) e.getSource();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (null != dragSource) {
					if (dragTarget == e.getSource()) {
						dragTarget = null;
					}
				}
			}
		};

		private final MouseMotionListener mmlDragSource = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (null == dragSource) {
					dragSource = (DustGuiSwingWidgetAnchor) e.getSource();
				}
			}
		};

		private final MouseListener mlDragSource = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if ((null != dragTarget) && (null != dragSource)) {
					Dust.accessEntity(DataCommand.setRef, dragSource.eEntity, dragTarget.eData, dragTarget.eEntity,
							null);
				}

				dragSource = dragTarget = null;
			}
		};
	}
	
	public static class AnchoredPanel extends JPanel {
		EnumMap<AnchorLocation, DustGuiSwingWidgetAnchor> anchors = new EnumMap<>(AnchorLocation.class);

		public AnchoredPanel(JComponent comp, Connector conn, DustEntity eEntity, DustEntity eData) {
			super(new BorderLayout(HR, HR));

			add(comp, BorderLayout.CENTER);
			
			addAnchor(AnchorLocation.Left, conn, eEntity, eData);
			addAnchor(AnchorLocation.Right, conn, eEntity, eData);
		}

		private void addAnchor(AnchorLocation al, Connector conn, DustEntity eEntity, DustEntity eData) {
			DustGuiSwingWidgetAnchor a = new DustGuiSwingWidgetAnchor(conn, eEntity, eData);
			add(a, al.getSwingConst());
			anchors.put(al, a);
		}

		public void getAnchorCentersOnScreen(EnumMap<AnchorLocation, Point> target) {
			for ( Map.Entry<AnchorLocation, DustGuiSwingWidgetAnchor> ea : anchors.entrySet() ) {
				JComponent comp = ea.getValue();

				Point ret = comp.getLocationOnScreen();
				Dimension d = comp.getSize();
				ret.translate(d.width / 2, d.height / 2);	
				target.put(ea.getKey(), ret);
			}
		}
	}

	private final DustEntity eEntity;
	private final DustEntity eData;

	public DustGuiSwingWidgetAnchor(Connector connector, DustEntity eEntity, DustEntity eData) {
		super(new ImageIcon("images/btn_blue-t.png"));

		addMouseListener(connector.mlDragTarget);

		if (null == eData) {
			addMouseMotionListener(connector.mmlDragSource);
			addMouseListener(connector.mlDragSource);

			eData = EntityResolver.getEntity(DustDataLinks.EntityModels);
		}

		this.eEntity = eEntity;
		this.eData = eData;
	}

	public static AnchoredPanel anchorPanel(JComponent comp, Connector conn, DustEntity eEntity, DustEntity eData) {
		return new AnchoredPanel(comp, conn, eEntity, eData);
	}

}
