package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dust.utils.DustUtilsFactory;

@SuppressWarnings({ "serial", "unchecked" })
class MontruGuiSwingPanelEntity extends JPanel implements MontruGuiSwingComponents {
	class EntityHeader extends JPanel {
		GuiEntityInfo ei;

		public EntityHeader(GuiEntityInfo ei) {
			super(new BorderLayout(5, 5));
			this.ei = ei;
			
			add(new JLabel(ei.getTitle(), JLabel.CENTER), BorderLayout.CENTER);
			setBackground(Color.lightGray);
			JLabel lbl = new JLabel(" + ", JLabel.CENTER);
			lbl.setSize(lbl.getHeight(), lbl.getHeight());
			add(lbl, BorderLayout.WEST);
			lbl = new JLabel(" + ", JLabel.CENTER);
			lbl.setSize(lbl.getHeight(), lbl.getHeight());
			add(lbl, BorderLayout.EAST);

			addMouseMotionListener(editor.pnlDesktop.mml);
			addMouseListener(editor.pnlDesktop.ml);
		}

		public GuiEntityInfo getEi() {
			return ei;
		}
	}

	GuiEntityInfo ei;
	Map<GuiEntityInfo, JComponent> linkLabels = new HashMap<>();
	EntityHeader pnlTop;

	MontruGuiSwingPanelEditor editor;
	

	public MontruGuiSwingPanelEntity(MontruGuiSwingPanelEditor editor, GuiEntityInfo ei) {
		super(new GridLayout(0, 1));
		this.ei = ei;
		this.editor = editor;

		pnlTop = new EntityHeader(ei);

		reloadData();
		
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	public void reloadData() {
		removeAll();
		linkLabels.clear();

//		DustEntity entity = ei.get(GuiEntityKey.entity);
		DustUtilsFactory<Object, StringBuilder> factRefs = new DustUtilsFactory<Object, StringBuilder>(false) {
			@Override
			protected StringBuilder create(Object key, Object... hints) {
				return new StringBuilder();
			}
		};

		for (GuiRefInfo ri : (Iterable<GuiRefInfo>) ei.get(GuiEntityKey.links)) {
			factRefs.get(ri.get(GuiRefKey.linkDef)).append(((GuiEntityInfo) ri.get(GuiRefKey.target)).getTitle()).append(", ");
		}

		add(pnlTop);

		for (GuiEntityInfo m : editor.editorModel.getAllTypes()) {
			if (m.contains(GuiEntityKey.showFlags, GuiShowFlag.hide) || !ei.contains(GuiEntityKey.models, m)) {
				continue;
			}

//			JLabel lbMdl = new JLabel((String) m.get(GuiEntityKey.id));
			JLabel lbMdl = editor.widgetManager.createWidget(WidgetType.dataLabel, m, null);
			add(lbMdl);
			if (m == ei.get(GuiEntityKey.type)) {
				lbMdl.setForeground(Color.RED);
			}

			Iterable<GuiEntityInfo> it = (Iterable<GuiEntityInfo>) m.get(GuiEntityKey.attDefs);
			if (null != it) {
				for (GuiEntityInfo ad : it) {
					JPanel pnl = new JPanel(new BorderLayout(10, 0));
					pnl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
					JLabel lblHead = editor.widgetManager.createWidget(WidgetType.dataLabel, ad, null);
					pnl.add(lblHead, BorderLayout.WEST);
					JComponent cmpVal = editor.widgetManager.createWidget(WidgetType.dataEditor, ei, ad);
					pnl.add(cmpVal, BorderLayout.CENTER);
					add(pnl);
				}
			}

			it = (Iterable<GuiEntityInfo>) m.get(GuiEntityKey.linkDefs);
			if (null != it) {
				for (GuiEntityInfo ld : it) {
					StringBuilder links = factRefs.peek(ld);
					String str;
					if (null == links) {
						str = " - ";
					} else {
						str = " -> {" + links + "}";
					}
					
					JPanel pnl = new JPanel(new BorderLayout(10, 0));
					pnl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
					JLabel lblHead = editor.widgetManager.createWidget(WidgetType.dataLabel, ld, null);
					pnl.add(lblHead, BorderLayout.WEST);
					
					JLabel llbl = new JLabel(str);
					pnl.add(llbl, BorderLayout.CENTER);
					add(pnl);

					
					linkLabels.put(ld, pnl);
					add(pnl);
				}
			}
		}

		revalidate();
		repaint();
	}
}