package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dust.mj02.dust.knowledge.DustCommComponents;
import dust.mj02.dust.knowledge.DustCommDiscussion;
import dust.mj02.dust.knowledge.DustCommJsonLoader;
import dust.mj02.montru.gui.MontruGuiEditorModel;
import dust.utils.DustUtilsFactory;

@SuppressWarnings("serial")
class MontruGuiSwingPanelEditor extends JPanel implements MontruGuiSwingComponents {

	private final GuiEditorModel editorModel;
	private final MontruGuiSwingWidgetManager widgetManager;

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
				Iterable<GuiRefInfo> itRef = getEditorModel().getAllRefs();

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
		};

		public PnlDesktop() {
			pnlLinks = new MontruGuiSwingPanelLinks(MontruGuiSwingPanelEditor.this);
			pnlLinks.followParent(this);
			add(pnlLinks, JDesktopPane.POPUP_LAYER);

			addMouseListener(ml);
		}

		public void reloadData() {
			getEditorModel().refreshData();
			pnlMeta.lmTypes.update();

			GuiEntityInfo eiEntity = getEditorModel().getEntityInfo(EntityResolver.getEntity(DustDataTypes.Entity));
			eiEntity.add(GuiEntityKey.showFlags, GuiShowFlag.hide);

//			removeAll();

//			add(pnlLinks, JDesktopPane.POPUP_LAYER);

			Iterable<GuiEntityInfo> allEntities = getEditorModel().getAllEntities();
			for (GuiEntityInfo ei : allEntities) {
				ei.put(GuiEntityKey.title, null);
				String t = ei.getTitle();
				activateEntity(ei, true).setTitle(t);
//				factIntFrames.get(ei);
			}
			updatePanels(allEntities);

			revalidate();
			repaint();
		}

		public void updatePanels(Iterable<GuiEntityInfo> toUpdate) {
			for (GuiEntityInfo pe : toUpdate) {
				updatePanel(pe);
			}
			pnlLinks.refreshLines();
		}

		public void updatePanels(GuiEntityInfo... toUpdate) {
			for (GuiEntityInfo pe : toUpdate) {
				updatePanel(pe);
			}
			pnlLinks.refreshLines();
		}

		public void updatePanel(GuiEntityInfo pe) {
			JInternalFrame pf = activateEntity(pe, false);
			if (null != pf) {
				MontruGuiSwingPanelEntity ep = pe.get(GuiEntityKey.panel);
				ep.reloadData();

				pf.pack();
			}
		}
	}

	class TypelistModel extends AbstractListModel<GuiEntityInfo> {
		@Override
		public GuiEntityInfo getElementAt(int index) {
			return getEditorModel().getAllTypes().get(index);
		}

		@Override
		public int getSize() {
			return getEditorModel().getAllTypes().size();
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

				for (GuiRefInfo ri : getEditorModel().getAllRefs()) {
					if (ri.isTrue(GuiRefKey.selected)) {
						toDel.add(ri);
					}
				}

				Iterable<GuiEntityInfo> toUpdate = getEditorModel().dropRefs(toDel);

				pnlDesktop.updatePanels(toUpdate);

				break;
			case test01:
				DustCommComponents.DustCommSource rdr = new DustCommJsonLoader();
				DustCommDiscussion disc = new DustCommDiscussion();
				
				try {
					disc.load(rdr, "MJ02Boot02.json");
					pnlDesktop.reloadData();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				break;
			}
		}
	};

	public MontruGuiSwingPanelEditor() {
		super(new BorderLayout());

		editorModel = new MontruGuiEditorModel();
		widgetManager = new MontruGuiSwingWidgetManager(this);

		pnlDesktop = new PnlDesktop();
		pnlMeta = new PnlMetaControl();

		JSplitPane spMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlMeta, new JScrollPane(pnlDesktop));
		add(spMain, BorderLayout.CENTER);
	}

	public GuiEditorModel getEditorModel() {
		return editorModel;
	}

	public MontruGuiSwingWidgetManager getWidgetManager() {
		return widgetManager;
	}

	public MontruGuiSwingPanelEntity hitTestPanel(MouseEvent evt) {
		return hitTestPanel(new Point(evt.getLocationOnScreen()));
	}

	public MontruGuiSwingPanelEntity hitTestPanel(Point screenPoint) {
		Rectangle rct = new Rectangle();

		for (GuiEntityInfo ei : getEditorModel().getAllEntities()) {
			MontruGuiSwingPanelEntity pe = ei.get(GuiEntityKey.panel);

			if (null != pe) {
				pe.getBounds(rct);
				rct.setLocation(pe.getLocationOnScreen());
				if (rct.contains(screenPoint)) {
					return pe;
				}
			}
		}

		return null;
	}

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