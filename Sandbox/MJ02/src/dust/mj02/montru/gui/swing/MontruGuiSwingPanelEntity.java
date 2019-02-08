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
	EntityInfo ei;
	Map<EntityInfo, JLabel> linkLabels = new HashMap<>();
	JPanel pnlTop;
	
	static EntityInfo eiEntity = factEntityInfo.get(EntityResolver.getEntity(DustDataTypes.Entity));

	public MontruGuiSwingPanelEntity(EntityInfo ei) {
		super(new GridLayout(0, 1));
		this.ei = ei;
		
		pnlTop = new JPanel(new BorderLayout(5, 5));
		pnlTop.add(new JLabel(ei.getTitle(), JLabel.CENTER), BorderLayout.CENTER);
		pnlTop.setBackground(Color.lightGray);
		pnlTop.add(new JLabel(" + ", JLabel.CENTER), BorderLayout.WEST);
		pnlTop.add(new JLabel(" + ", JLabel.CENTER), BorderLayout.EAST);

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

		Iterable<EntityInfo> im = (Iterable<EntityInfo>) ei.get(EntityKey.models);
		if (null != im) {
			for (EntityInfo m : im) {
				if (eiEntity == m) {
					continue;
				}

				JLabel lbMdl = new JLabel((String) m.get(EntityKey.id));
				add(lbMdl);
				if ( m == ei.get(EntityKey.type)) {
					lbMdl.setForeground(Color.RED);
				}

				Iterable<EntityInfo> it = (Iterable<EntityInfo>) m.get(EntityKey.attDefs);
				if (null != it) {
					for (EntityInfo ad : it) {
						Object val = Dust.accessEntity(DataCommand.getValue, entity, ad.get(EntityKey.entity), null,
								null);
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
						if (null != links) {
							JLabel llbl = new JLabel("  " + ld.get(EntityKey.id) + " -> {" + links + "}");
							linkLabels.put(ld, llbl);
							add(llbl);
						}
					}
				}
			}
		}

		revalidate();
		repaint();
	}
}