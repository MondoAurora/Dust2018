package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dust.mj02.dust.Dust;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

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
			add(lbl, BorderLayout.WEST);
			lbl = new JLabel(" + ", JLabel.CENTER);
			add(lbl, BorderLayout.EAST);

			addMouseMotionListener(editor.pnlDesktop.mml);
			addMouseListener(editor.pnlDesktop.ml);
		}

		public GuiEntityInfo getEi() {
			return ei;
		}
	}

	GuiEntityInfo ei;
	Map<GuiEntityInfo, JLabel> linkLabels = new HashMap<>();
	EntityHeader pnlTop;

	MontruGuiSwingPanelEditor editor;
	

	public MontruGuiSwingPanelEntity(MontruGuiSwingPanelEditor editor, GuiEntityInfo ei) {
		super(new GridLayout(0, 1));
		this.ei = ei;
		this.editor = editor;

		pnlTop = new EntityHeader(ei);

		reloadData();
	}

	public void reloadData() {
		removeAll();
		linkLabels.clear();

		DustEntity entity = ei.get(GuiEntityKey.entity);
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

		for (GuiEntityInfo m : editor.arrTypes) {
			if (m.contains(GuiEntityKey.showFlags, GuiShowFlag.hide) || !ei.contains(GuiEntityKey.models, m)) {
				continue;
			}

			JLabel lbMdl = new JLabel((String) m.get(GuiEntityKey.id));
			add(lbMdl);
			if (m == ei.get(GuiEntityKey.type)) {
				lbMdl.setForeground(Color.RED);
			}

			Iterable<GuiEntityInfo> it = (Iterable<GuiEntityInfo>) m.get(GuiEntityKey.attDefs);
			if (null != it) {
				for (GuiEntityInfo ad : it) {
					Object val = Dust.accessEntity(DataCommand.getValue, entity, ad.get(GuiEntityKey.entity), null, null);
					JPanel pnl = new JPanel(new BorderLayout(10, 0));
					pnl.add(new JLabel("  " + ad.get(GuiEntityKey.id)), BorderLayout.WEST);
					JTextField tf = new JTextField();
					tf.setText(DustUtilsJava.toString(val));
					pnl.add(tf, BorderLayout.CENTER);
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
					JLabel llbl = new JLabel("  " + ld.get(GuiEntityKey.id) + str);
					linkLabels.put(ld, llbl);
					add(llbl);
				}
			}
		}
//
//		Iterable<GuiEntityInfo> im = (Iterable<GuiEntityInfo>) ei.get(GuiEntityKey.models);
//		if (null != im) {
//			for (GuiEntityInfo m : im) {
//				if (eiEntity == m) {
//					continue;
//				}
//
//			}
//		}

		revalidate();
		repaint();
	}
}