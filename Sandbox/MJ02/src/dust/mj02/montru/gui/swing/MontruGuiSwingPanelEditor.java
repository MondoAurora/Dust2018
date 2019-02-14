package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dust.mj02.dust.Dust;
import dust.mj02.montru.gui.MontruGuiEditorModel;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings("serial")
class MontruGuiSwingPanelEditor extends JPanel
		implements MontruGuiSwingComponents, MontruGuiSwingComponents.EntitySwingCompResolver {

	GuiEditorModel editorModel = new MontruGuiEditorModel();
	MontruGuiSwingWidgetManager widgetManager = new MontruGuiSwingWidgetManager(editorModel);

//	ArrayList<GuiRefInfo> arrRefs = new ArrayList<>();

	DustUtilsFactory<GuiEntityInfo, JInternalFrame> factIntFrames = new DustUtilsFactory<GuiEntityInfo, JInternalFrame>(
			false) {
		@Override
		protected JInternalFrame create(GuiEntityInfo key, Object... hints) {
			JInternalFrame internal = new JInternalFrame(key.getTitle(), true, false, true, true);
			MontruGuiSwingPanelEntity pnl = new MontruGuiSwingPanelEntity(MontruGuiSwingPanelEditor.this, key);
			key.put(GuiEntityKey.panel, pnl);
			internal.getContentPane().add(pnl, BorderLayout.CENTER);
			internal.setTitle(key.getTitle());
			internal.pack();

			pnlDesktop.add(internal, JDesktopPane.DEFAULT_LAYER);
			internal.setVisible(true);

			pnlDesktop.pnlLinks.followContent(internal);

			return internal;
		}
	};

	class PnlDesktop extends JDesktopPane {
		MontruGuiSwingPanelLinks pnlLinks;

		MouseListener ml = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Iterable<GuiRefInfo> itRef = editorModel.getAllRefs();

				if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
					for (GuiRefInfo ri : itRef) {
						ri.put(GuiRefKey.selected, false);
					}
				}
				pnlLinks.hitTest(e.getPoint());

				for (GuiRefInfo ri : itRef) {
					if (ri.isTrue(GuiRefKey.selected)) {
						activateEntity(ri.get(GuiRefKey.source), false);
						activateEntity(ri.get(GuiRefKey.target), false);
					}
				}

			}

			public void mouseDragged(MouseEvent e) {
				// DustUtilsDev.dump("dragging", e);
			};

			public void mouseReleased(MouseEvent e) {
				if (null != dragging) {
					DustUtilsDev.dump("dropped", dragging);

					Point pt = new Point(e.getPoint());
					SwingUtilities.convertPointToScreen(pt, (Component) e.getSource());
					Point loc = null;

					MontruGuiSwingPanelEntity targetPanel = null;

					for (GuiEntityInfo ei : factIntFrames.keys()) {
						MontruGuiSwingPanelEntity pe = ei.get(GuiEntityKey.panel);

						if (null != pe) {
							loc = SwingUtilities.convertPoint(null, pt, pe);
							if (pe.contains(loc)) {
								targetPanel = pe;
								break;
							}
						}
					}

					if (null != targetPanel) {
						for (Map.Entry<GuiEntityInfo, JComponent> le : targetPanel.linkLabels.entrySet()) {
							JComponent lbl = le.getValue();
							loc = SwingUtilities.convertPoint(null, pt, lbl);
							if (lbl.contains(loc)) {
								DustRef dr = Dust.accessEntity(DataCommand.setRef, targetPanel.ei.get(GuiEntityKey.entity), 
										le.getKey().get(GuiEntityKey.entity), dragging.get(GuiEntityKey.entity), null);

								GuiRefInfo ri = editorModel.getRefInfo(dr);
								ri.put(GuiRefKey.selected, true);

								Set<GuiEntityInfo> uu = new HashSet<>();
								uu.add(dragging);
								uu.add(targetPanel.ei);
								updatePanels(uu);
								break;
							}
						}
					} else if (editorModel.getAllTypes().contains(dragging)) {
						String id = JOptionPane.showInputDialog("Entity id?");

						if (!DustUtilsJava.isEmpty(id)) {
							DustEntity de = Dust.getEntity(id);
							GuiEntityInfo ei = editorModel.getEntityInfo(de);

							ei.add(GuiEntityKey.models, dragging);

							JInternalFrame jif = activateEntity(ei, true);
							jif.setVisible(true);
							jif.setLocation(e.getPoint());
						}
					}

					dragging = null;
				}
			};
		};

		MouseMotionListener mml = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (null == dragging) {
					dragging = ((MontruGuiSwingPanelEntity.EntityHeader) e.getSource()).getEi();
					DustUtilsDev.dump("dragging", e.getPoint(), dragging);
				}
			}
		};

		GuiEntityInfo dragging;

		public PnlDesktop() {
			pnlLinks = new MontruGuiSwingPanelLinks(MontruGuiSwingPanelEditor.this);
			pnlLinks.followParent(this);

			addMouseListener(ml);
		}

		public void reloadData() {
			editorModel.refreshData();
			pnlMeta.lmTypes.update();

			GuiEntityInfo eiEntity = editorModel.getEntityInfo(EntityResolver.getEntity(DustDataTypes.Entity));
			eiEntity.add(GuiEntityKey.showFlags, GuiShowFlag.hide);

			removeAll();

			add(pnlLinks, JDesktopPane.POPUP_LAYER);

			for (GuiEntityInfo ei : editorModel.getAllEntities()) {
				factIntFrames.get(ei);
			}

			revalidate();
			repaint();
		}

		public void updatePanels(Iterable<GuiEntityInfo> toUpdate) {
			for (GuiEntityInfo pe : toUpdate) {
				JInternalFrame pf = activateEntity(pe, false);
				if (null != pf) {
					MontruGuiSwingPanelEntity ep = pe.get(GuiEntityKey.panel);
					ep.reloadData();

					pf.pack();
				}
			}
			pnlLinks.refreshLines();
		}
	}

	class TypelistModel extends AbstractListModel<GuiEntityInfo> {
		@Override
		public GuiEntityInfo getElementAt(int index) {
			return editorModel.getAllTypes().get(index);
		}

		@Override
		public int getSize() {
			return editorModel.getAllTypes().size();
		}

		private void update() {
			fireContentsChanged(pnlMeta, 0, getSize());
		}
	}

	class TypeRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel tc = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			tc.setText(((GuiEntityInfo) value).getTitle());
			return tc;
		}
	}

	class PnlMetaControl extends JPanel {
		TypelistModel lmTypes = new TypelistModel();
		TypeRenderer crTypes = new TypeRenderer();

		public PnlMetaControl() {
			super(new BorderLayout(5, 5));

			JList<GuiEntityInfo> lstTypes = new JList<GuiEntityInfo>(lmTypes);
			JScrollPane scpTypes = new JScrollPane(lstTypes);
			scpTypes.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Types"));

			lstTypes.setCellRenderer(crTypes);
			lstTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lstTypes.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					eiSelType = lstTypes.getSelectedValue();
				}
			});

			add(scpTypes, BorderLayout.CENTER);

			JToolBar tbButtons = new JToolBar();

			for (Enum<?> m : GuiCommands.values()) {
				JButton btn = new JButton(m.name());
				btn.setActionCommand(m.name());
				tbButtons.add(btn);
				btn.addActionListener(cmdListener);
			}

			add(tbButtons, BorderLayout.SOUTH);
		}
	}

	PnlDesktop pnlDesktop;
	PnlMetaControl pnlMeta;
	GuiEntityInfo eiSelType;

	Map<Enum<?>, AbstractButton> buttons = new HashMap<>();
	ActionListener cmdListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (GuiCommands.valueOf(e.getActionCommand())) {
			case createEntity:
				break;
			case deleteEntity:
				break;
			case deleteRef:
				Set<GuiRefInfo> toDel = new HashSet<>();

				for (GuiRefInfo ri : editorModel.getAllRefs()) {
					if (ri.isTrue(GuiRefKey.selected)) {
						toDel.add(ri);
					}
				}

				Iterable<GuiEntityInfo> toUpdate = editorModel.dropRefs(toDel);

				pnlDesktop.updatePanels(toUpdate);

				break;
			case test01:
				pnlDesktop.reloadData();
				break;
			}
		}
	};

	public MontruGuiSwingPanelEditor() {
		super(new BorderLayout());

		pnlDesktop = new PnlDesktop();
		pnlMeta = new PnlMetaControl();

		JSplitPane spMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlMeta, new JScrollPane(pnlDesktop));
		add(spMain, BorderLayout.CENTER);
	}

	@Override
	public JComponent getEntityPanel(GuiEntityInfo ei) {
		return factIntFrames.peek(ei);
	}

	public JInternalFrame activateEntity(GuiEntityInfo ei, boolean forced) {
		JInternalFrame pf = forced ? factIntFrames.get(ei) : factIntFrames.peek(ei);
		if (null != pf) {
			try {
				pf.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return pf;
	}
}