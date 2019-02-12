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
		EntityInfo ei;

		public EntityHeader(EntityInfo ei) {
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

		public EntityInfo getEi() {
			return ei;
		}
	}

	EntityInfo ei;
	Map<EntityInfo, JLabel> linkLabels = new HashMap<>();
	EntityHeader pnlTop;

	MontruGuiSwingPanelEditor editor;
	

	public MontruGuiSwingPanelEntity(MontruGuiSwingPanelEditor editor, EntityInfo ei) {
		super(new GridLayout(0, 1));
		this.ei = ei;
		this.editor = editor;

		pnlTop = new EntityHeader(ei);

		reloadData();
	}

	public void reloadData() {
		removeAll();
		linkLabels.clear();

		DustEntity entity = ei.get(EntityKey.entity);
		DustUtilsFactory<Object, StringBuilder> factRefs = new DustUtilsFactory<Object, StringBuilder>(false) {
			@Override
			protected StringBuilder create(Object key, Object... hints) {
				return new StringBuilder();
			}
		};

		for (RefInfo ri : (Iterable<RefInfo>) ei.get(EntityKey.links)) {
			factRefs.get(ri.get(RefKey.linkDef)).append(((EntityInfo) ri.get(RefKey.target)).getTitle()).append(", ");
		}

		add(pnlTop);

		for (EntityInfo m : editor.arrTypes) {
			if (m.contains(EntityKey.showFlags, ShowFlag.hide) || !ei.contains(EntityKey.models, m)) {
				continue;
			}

			JLabel lbMdl = new JLabel((String) m.get(EntityKey.id));
			add(lbMdl);
			if (m == ei.get(EntityKey.type)) {
				lbMdl.setForeground(Color.RED);
			}

			Iterable<EntityInfo> it = (Iterable<EntityInfo>) m.get(EntityKey.attDefs);
			if (null != it) {
				for (EntityInfo ad : it) {
					Object val = Dust.accessEntity(DataCommand.getValue, entity, ad.get(EntityKey.entity), null, null);
					JPanel pnl = new JPanel(new BorderLayout(10, 0));
					pnl.add(new JLabel("  " + ad.get(EntityKey.id)), BorderLayout.WEST);
					JTextField tf = new JTextField();
					tf.setText(DustUtilsJava.toString(val));
					pnl.add(tf, BorderLayout.CENTER);
					add(pnl);
				}
			}

			it = (Iterable<EntityInfo>) m.get(EntityKey.linkDefs);
			if (null != it) {
				for (EntityInfo ld : it) {
					StringBuilder links = factRefs.peek(ld);
					String str;
					if (null == links) {
						str = " - ";
					} else {
						str = " -> {" + links + "}";
					}
					JLabel llbl = new JLabel("  " + ld.get(EntityKey.id) + str);
					linkLabels.put(ld, llbl);
					add(llbl);
				}
			}
		}
//
//		Iterable<EntityInfo> im = (Iterable<EntityInfo>) ei.get(EntityKey.models);
//		if (null != im) {
//			for (EntityInfo m : im) {
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