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
import javax.swing.border.BevelBorder;

import dust.utils.DustUtilsFactory;

@SuppressWarnings({ "serial", "unchecked" })
class MontruGuiSwingPanelEntity extends JPanel implements MontruGuiSwingComponents {
	class EntityHeader extends JPanel implements GuiEntityElement {
		GuiEntityInfo ei;

		public EntityHeader(GuiEntityInfo ei) {
			super(new BorderLayout(5, 5));
			this.ei = ei;
			
			setBackground(Color.lightGray);
			
			JLabel lbl = new JLabel(ei.getTitle(), JLabel.CENTER);
			add(lbl, BorderLayout.CENTER);			
			add(createDragger(), BorderLayout.WEST);
			add(createDragger(), BorderLayout.EAST);

			addMouseListener(editor.mlDragTarget);
		}

		public JLabel createDragger() {
			JLabel lbl = editor.widgetManager.createWidget(WidgetType.dataLabel, ei, null);

			lbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			lbl.setText("OO");

			lbl.addMouseMotionListener(editor.mmlDragSource);
			lbl.addMouseListener(editor.mlDragSource);

			return lbl;
		}

		@Override
		public GuiEntityInfo getEntityInfo() {
			return ei;
		}

		@Override
		public void guiChangedAttribute(GuiEntityInfo entity, GuiEntityInfo att, Object value) {
			
		}

		@Override
		public void guiChangedRef(GuiEntityInfo entity, GuiRefInfo ref, DataCommand cmd) {
			
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

		pnlTop = new EntityHeader(ei);
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
			lbMdl.addMouseListener(editor.mlLabelActivator);


			Iterable<GuiEntityInfo> it = (Iterable<GuiEntityInfo>) m.get(GuiEntityKey.attDefs);
			if (null != it) {
				for (GuiEntityInfo ad : it) {
					JPanel pnl = new JPanel(new BorderLayout(10, 0));
					pnl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
					JLabel lblHead = editor.widgetManager.createWidget(WidgetType.dataLabel, ad, null);
					lblHead.addMouseListener(editor.mlLabelActivator);
					pnl.add(lblHead, BorderLayout.WEST);
					JComponent cmpVal = editor.widgetManager.createWidget(WidgetType.dataEditor, ei, ad);
					pnl.add(cmpVal, BorderLayout.CENTER);
					add(pnl);
//					pnl.addMouseMotionListener(editor.pnlDesktop.mml);

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
					lblHead.addMouseListener(editor.mlLabelActivator);
					pnl.add(lblHead, BorderLayout.WEST);
					
					JLabel lblLink = editor.widgetManager.createWidget(WidgetType.dataLabel, ei, ld);

					lblLink.setText(str);
					lblLink.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
					pnl.add(lblLink, BorderLayout.CENTER);
					
					lblLink.addMouseListener(editor.mlDragTarget);
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