package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustComponents;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings({ "unchecked", "serial" })
public class MontruGuiSwingFrame implements DustComponents, DustMetaComponents, DustProcComponents,
		DustGenericComponents, DustProcComponents.DustProcInitable {

	private static final Dimension INIT_FRAME_SIZE = new Dimension(800, 400);
	private static final int HR = 6;
	public static final Color COL_REF_SEL = Color.RED;
	public static final Color COL_REF_NORMAL = Color.BLACK;

	enum EntityKey {
		entity, type, models, id, owner, attDefs, linkDefs, links// , atts
	}

	enum RefKey {
		source, target, linkDef, key
	}

	interface PanelProvider {
		JPanel getPanel();
		String getTitle();
	}

	class NodeInfo<NodeKey extends Enum<NodeKey>> {
		private EnumMap<NodeKey, Object> content;

		public NodeInfo(Class<NodeKey> kc) {
			content = new EnumMap<>(kc);
		}

		public <RetVal> RetVal put(NodeKey key, Object val) {
			return (RetVal) content.put(key, val);
		}

		public <RetVal> RetVal get(NodeKey key) {
			return (RetVal) content.get(key);
		}

		public void add(NodeKey key, Object val) {
			if (null == val) {
				return;
			}
			Set<Object> cont = (Set<Object>) content.get(key);
			if (null == cont) {
				content.put(key, cont = new HashSet<>());
			}
			cont.add(val);
		}
	}

	class RefInfo extends NodeInfo<RefKey> {
		public RefInfo(DustEntity source, DustEntity linkDef, DustEntity target, Object key) {
			super(RefKey.class);

			put(RefKey.source, factEntityInfo.get(source));
			put(RefKey.target, factEntityInfo.get(target));
			put(RefKey.linkDef, factEntityInfo.get(linkDef));

			put(RefKey.key, (key instanceof DustEntity) ? factEntityInfo.get((DustEntity) key) : key);
		}
	}

	class EntityInfo extends NodeInfo<EntityKey> implements PanelProvider {
		PnlEntity pnl;

		public EntityInfo() {
			super(EntityKey.class);
			put(EntityKey.links, new HashSet<RefInfo>());
		}

		@Override
		public JPanel getPanel() {
			return (null == pnl) ? (pnl = new PnlEntity(this)) : pnl;
		}

		@Override
		public String getTitle() {
			EntityInfo tt = get(EntityKey.type);
			return DustUtilsJava
					.sbAppend(null, " ", true, (null == tt) ? "" : tt.get(EntityKey.id), ":", get(EntityKey.id))
					.toString();
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(getTitle()).append(" [");

			DustEntity entity = get(EntityKey.entity);
			DustUtilsFactory<Object, StringBuilder> factRefs = new DustUtilsFactory<Object, StringBuilder>(false) {
				@Override
				protected StringBuilder create(Object key, Object... hints) {
					return new StringBuilder();
				}
			};

			for (RefInfo ri : (Iterable<RefInfo>) get(EntityKey.links)) {
				factRefs.get(ri.get(RefKey.linkDef)).append(((EntityInfo) ri.get(RefKey.target)).getTitle())
						.append(", ");
			}

			for (EntityInfo m : (Iterable<EntityInfo>) get(EntityKey.models)) {
				DustUtilsJava.sbAppend(sb, " ", true, m.get(EntityKey.id), ": {");

				Iterable<EntityInfo> it = (Iterable<EntityInfo>) m.get(EntityKey.attDefs);
				if (null != it) {
					for (EntityInfo ad : it) {
						DustUtilsJava.sbAppend(sb, " ", true, ad.get(EntityKey.id), "=",
								Dust.accessEntity(DataCommand.getValue, entity, ad.get(EntityKey.entity), null, null),
								", ");
					}
				}

				it = (Iterable<EntityInfo>) m.get(EntityKey.linkDefs);
				if (null != it) {
					for (EntityInfo ld : it) {
						DustUtilsJava.sbAppend(sb, " ", true, ld.get(EntityKey.id), " -> {", factRefs.peek(ld), "}, ");
					}
				}

				sb.append("}");
			}

			sb.append("]");

			return sb.toString();
		}

	}

	class PnlEntity extends JPanel {
		EntityInfo ei;
		Map<EntityInfo, JLabel> linkLabels = new HashMap<>();

		public PnlEntity(EntityInfo ei) {
			super(new GridLayout(0, 1));
			this.ei = ei;

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
				factRefs.get(ri.get(RefKey.linkDef)).append(((EntityInfo) ri.get(RefKey.target)).getTitle())
						.append(", ");
			}

			for (EntityInfo m : (Iterable<EntityInfo>) ei.get(EntityKey.models)) {
				add(new JLabel((String) m.get(EntityKey.id)));

				Iterable<EntityInfo> it = (Iterable<EntityInfo>) m.get(EntityKey.attDefs);
				if (null != it) {
					for (EntityInfo ad : it) {
						add(new JLabel("  " + ad.get(EntityKey.id) + "=" + Dust.accessEntity(DataCommand.getValue,
								entity, ad.get(EntityKey.entity), null, null)));
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

			revalidate();
			repaint();
		}
	}

	DustUtilsFactory<PanelProvider, JInternalFrame> factIntFrames = new DustUtilsFactory<PanelProvider, JInternalFrame>(
			false) {
		@Override
		protected JInternalFrame create(PanelProvider key, Object... hints) {
			JInternalFrame internal = new JInternalFrame(key.getTitle(), true, false, true, true);
			internal.getContentPane().add(key.getPanel(), BorderLayout.CENTER);
			internal.setTitle(key.getTitle());
			internal.pack();

			pnlLinks.followContent(internal);

			return internal;
		}
	};

	class PnlLinks extends JPanel {
		Map<RefInfo, Line2D> lines = new HashMap<>();

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

		public PnlLinks() {
			setOpaque(false);
		}

		public void followContent(JComponent comp) {
			comp.addComponentListener(painter);
		}

		public void followParent(JComponent comp) {
			comp.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					setSize(e.getComponent().getSize());
					refreshLines();
				}
			});
		}

		private void hitTest(Point pt) {
			Rectangle hit = new Rectangle(pt.x - HR, pt.y - HR, 2 * HR, 2 * HR);

			for (Map.Entry<RefInfo, Line2D> e : lines.entrySet()) {
				if (e.getValue().intersects(hit)) {
					RefInfo ri = e.getKey();
					if (selRefs.contains(ri)) {
						selRefs.remove(ri);
					} else {
						selRefs.add(ri);
					}
				}
			}

			repaint();
		}

		private void refreshLines() {
			lines.clear();

			for (RefInfo ri : arrRefs) {
				EntityInfo eiSrc = ri.get(RefKey.source);
				JInternalFrame frmSource = factIntFrames.peek(eiSrc);
				JInternalFrame frmTarget = factIntFrames.peek(ri.get(RefKey.target));

				if ((null != frmSource) && (null != frmTarget)) {
					JComponent comp = eiSrc.pnl.linkLabels.get(ri.get(RefKey.linkDef));

					if (null != comp) {
						Point ptSource = new Point(0, comp.getHeight() / 2);
						SwingUtilities.convertPointToScreen(ptSource, comp);
						Point ptTarget = new Point(0, 0);
						SwingUtilities.convertPointToScreen(ptTarget, frmTarget);

						SwingUtilities.convertPointFromScreen(ptSource, this);
						SwingUtilities.convertPointFromScreen(ptTarget, this);

						lines.put(ri, new Line2D.Float(ptSource, ptTarget));
					}
				}
			}

			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Color col = g.getColor();

			for (Map.Entry<RefInfo, Line2D> e : lines.entrySet()) {
				Line2D line = e.getValue();
				g.setColor(selRefs.contains(e.getKey()) ? COL_REF_SEL : COL_REF_NORMAL);
				g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
			}

			g.setColor(col);
		}
	};

	class PnlEditor extends JDesktopPane {
		MouseListener ml = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
					selRefs.clear();
				}
				pnlLinks.hitTest(e.getPoint());
			}
		};

		public PnlEditor() {
			pnlLinks = new PnlLinks();
			pnlLinks.followParent(this);

			addMouseListener(ml);
		}

		public void reloadData() {
			removeAll();

			add(pnlLinks, JDesktopPane.POPUP_LAYER);

			for (DustEntity k : factEntityInfo.keys()) {
				JInternalFrame jif = factIntFrames.get(factEntityInfo.peek(k));
				add(jif, JDesktopPane.DEFAULT_LAYER);
				jif.setVisible(true);
			}

			revalidate();
			repaint();
		}
	}

	class Frame extends JFrame {
		private static final long serialVersionUID = 1L;

		public Frame() {
			setDefaultCloseOperation(EXIT_ON_CLOSE);

			setPreferredSize(INIT_FRAME_SIZE);

			pnlEditor = new PnlEditor();

			JPanel pnlCmds = new JPanel(new FlowLayout(FlowLayout.LEFT));

			JButton btn = new JButton("Test!");
			pnlCmds.add(btn);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dustProcInitableInit();
				}
			});

			JPanel pnlMain = new JPanel(new BorderLayout());

			pnlMain.add(new JScrollPane(pnlEditor), BorderLayout.CENTER);
			pnlMain.add(pnlCmds, BorderLayout.SOUTH);

			getContentPane().add(pnlMain, BorderLayout.CENTER);

			pack();
			setVisible(true);
		}
	}

	DustUtilsFactory<DustEntity, EntityInfo> factEntityInfo = new DustUtilsFactory<DustEntity, EntityInfo>(false) {
		@Override
		protected EntityInfo create(DustEntity key, Object... hints) {
			EntityInfo ret = new EntityInfo();
			ret.put(EntityKey.entity, key);

			String id = Dust.accessEntity(DataCommand.getValue, key,
					EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal), null, null);

			ret.put(EntityKey.id, id);

			return ret;
		}
	};

	ArrayList<RefInfo> arrRefs = new ArrayList<>();
	ArrayList<EntityInfo> arrTypes = new ArrayList<>();

	Set<RefInfo> selRefs = new HashSet<>();

	Frame frame;
	PnlEditor pnlEditor;
	PnlLinks pnlLinks;

	public MontruGuiSwingFrame() {
		this.frame = new Frame();

		dustProcInitableInit();
	};

	@Override
	public void dustProcInitableInit() {
		frame.setTitle(getClass().getSimpleName());

		arrTypes.clear();
		arrRefs.clear();

		DustEntity eldPrimaryType = EntityResolver.getEntity(DustDataLinks.EntityPrimaryType);
		DustEntity eldEntityModels = EntityResolver.getEntity(DustDataLinks.EntityModels);

		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustEntity source, DustEntity linkDef, DustEntity target, Object key) {
				RefInfo ri = new RefInfo(source, linkDef, target, key);
				arrRefs.add(ri);

				EntityInfo eiObj = factEntityInfo.get(source);
				((Set<RefInfo>) eiObj.get(EntityKey.links)).add(ri);

				if (linkDef == eldPrimaryType) {
					eiObj.put(EntityKey.type, factEntityInfo.get(target));
				} else if (linkDef == eldEntityModels) {
					eiObj.add(EntityKey.models, factEntityInfo.get(target));
				}
			}
		}, null, null, null);

		Map<EntityInfo, DustMetaTypes> mapMeta = new HashMap<>();
		for (DustMetaTypes dmt : DustMetaTypes.values()) {
			mapMeta.put(factEntityInfo.get(EntityResolver.getEntity(dmt)), dmt);
		}
		EntityInfo eiGenericOwner = factEntityInfo.get(EntityResolver.getEntity(DustGenericLinks.Owner));

		Dust.processEntities(new EntityProcessor() {
			@Override
			public void processEntity(Object key, DustEntity entity) {
				EntityInfo ei = factEntityInfo.get(entity);
				EntityInfo eiType = ei.get(EntityKey.type);
				DustMetaTypes dmt = mapMeta.get(eiType);

				EntityKey ekInfo = null;

				if (null != dmt) {
					switch (dmt) {
					case Type:
						arrTypes.add(ei);
						break;
					case AttDef:
						ekInfo = EntityKey.attDefs;
						break;
					case LinkDef:
						ekInfo = EntityKey.linkDefs;
						break;
					}
				}

				if (null != ekInfo) {
					for (RefInfo ri : (Iterable<RefInfo>) ei.get(EntityKey.links)) {
						if (eiGenericOwner == ri.get(RefKey.linkDef)) {
							EntityInfo eiOwnerType = ri.get(RefKey.target);
							eiOwnerType.add(ekInfo, ei);
						}
					}
				}
			}
		});

		pnlEditor.reloadData();
	}

}
