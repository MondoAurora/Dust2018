package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.EnumMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import dust.mj02.montru.gui.swing.MontruGuiSwingWidgetManager.AnchoredPanel;
import dust.utils.DustUtilsFactory;

@SuppressWarnings({ "serial", "unchecked" })
class MontruGuiSwingPanelEntity extends JPanel implements MontruGuiSwingComponents {

	MontruGuiSwingPanelEditor editor;
	MontruGuiSwingWidgetManager widgetManager;

	GuiEntityInfo ei;

	DustUtilsFactory<GuiEntityInfo, JComponent> factRows = new DustUtilsFactory<GuiEntityInfo, JComponent>(false) {
		@Override
		protected JComponent create(GuiEntityInfo key, Object... hints) {
			WidgetType wt = (WidgetType) hints[0];
			JComponent comp = null;

			switch (wt) {
			case dataLabel:
				comp = widgetManager.createWidget(wt, key, null);
				break;
			case entityHead:
				comp = widgetManager.createWidget(wt, ei, null);
				comp = widgetManager.anchorPanel(comp, (GuiEntityElement) comp);
				break;
			case dataEditor:
				JPanel pnlContent = new JPanel(new BorderLayout(HR, 0));
				pnlContent.add(widgetManager.createWidget(WidgetType.dataLabel, key, null), BorderLayout.WEST);

				boolean link = (boolean) hints[1];
				if (link) {
					comp = widgetManager.createWidget(WidgetType.dataLabel, ei, key);
					pnlContent.add(comp, BorderLayout.CENTER);

					comp = widgetManager.anchorPanel(pnlContent, (GuiEntityElement) comp);
				} else {
					comp = widgetManager.createWidget(wt, ei, key);
					pnlContent.add(comp, BorderLayout.CENTER);

					JPanel pnlRow = new JPanel(new BorderLayout(0, 0));
					pnlRow.add(Box.createRigidArea(ANCHOR_SIZE), BorderLayout.WEST);
					pnlRow.add(pnlContent, BorderLayout.CENTER);
					comp = pnlRow;
				}
				break;
			default:
				break;

			}

			return comp;
		}

	};

	public MontruGuiSwingPanelEntity(MontruGuiSwingPanelEditor editor, GuiEntityInfo ei) {
		super(new GridLayout(0, 1));
		this.ei = ei;
		this.editor = editor;
		this.widgetManager = editor.getWidgetManager();

		reloadData();

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	public boolean getAnchorOnScreen(EnumMap<AnchorLocation, Point> target, GuiEntityInfo link) {
		AnchoredPanel ap = (AnchoredPanel) factRows.peek(link);
		if (null == ap) {
			target.clear();
			return false;
		} else {
			ap.getAnchorCentersOnScreen(target);
			return true;
		}
	}

	public void reloadData() {
		removeAll();

		add(factRows.get(null, WidgetType.entityHead));

		JComponent row;

		for (GuiEntityInfo m : editor.getEditorModel().getAllTypes()) {
			if (m.contains(GuiEntityKey.showFlags, GuiShowFlag.hide) || !ei.contains(GuiEntityKey.models, m)) {
				continue;
			}

			row = factRows.get(m, WidgetType.dataLabel);
			if (m == ei.get(GuiEntityKey.type)) {
				row.setForeground(Color.RED);
			}
			add(row);

			Iterable<GuiEntityInfo> it = (Iterable<GuiEntityInfo>) m.get(GuiEntityKey.attDefs);
			if (null != it) {
				for (GuiEntityInfo ad : it) {
					row = factRows.get(ad, WidgetType.dataEditor, false);
					add(row);
				}
			}

			it = (Iterable<GuiEntityInfo>) m.get(GuiEntityKey.linkDefs);
			if (null != it) {
				for (GuiEntityInfo ld : it) {
					row = factRows.get(ld, WidgetType.dataEditor, true);
					((AnchoredPanel)row).getElement().guiChangedAttribute(ei, ld, null);
					add(row);
				}
			}
		}

		revalidate();
		repaint();
	}
}