package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dust.mj02.dust.gui.DustGuiComponents;

@SuppressWarnings("serial")
public class DustGuiSwingWidgetAnchor extends JLabel implements DustGuiSwingComponents, DustGuiComponents.GuiDataWrapper<JLabel> {
	public static class AnchoredPanel extends JPanel {
		EnumMap<AnchorLocation, DustGuiSwingWidgetAnchor> anchors = new EnumMap<>(AnchorLocation.class);

		public AnchoredPanel(JComponent comp, DustGuiSwingEntityActionControl conn, DustEntity eEntity, DustEntity eData) {
			super(new BorderLayout(HR, HR));

			add(comp, BorderLayout.CENTER);
			
			addAnchor(AnchorLocation.Left, conn, eEntity, eData);
			addAnchor(AnchorLocation.Right, conn, eEntity, eData);
		}

		private void addAnchor(AnchorLocation al, DustGuiSwingEntityActionControl conn, DustEntity eEntity, DustEntity eData) {
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

	final DustEntity eEntity;
	final DustEntity eData;

	private DustGuiSwingWidgetAnchor(DustGuiSwingEntityActionControl mac, DustEntity eEntity, DustEntity eData) {
		super(new ImageIcon("images/btn_blue-t.png"));
		
		mac.setDragTarget(this);

		if (null == eData) {
			mac.setDragSource(this);
			eData = EntityResolver.getEntity(DustDataLinks.EntityModels);
		}

		this.eEntity = eEntity;
		this.eData = eData;
	}

	public static AnchoredPanel anchorPanel(JComponent comp, DustGuiSwingEntityActionControl conn, DustEntity eEntity, DustEntity eData) {
		return new AnchoredPanel(comp, conn, eEntity, eData);
	}

	@Override
	public DustEntity getEntity() {
		return eEntity;
	}
	@Override
	public DustEntity getData() {
		return eData;
	}
	@Override
	public JLabel getComponent() {
		return this;
	}
}
