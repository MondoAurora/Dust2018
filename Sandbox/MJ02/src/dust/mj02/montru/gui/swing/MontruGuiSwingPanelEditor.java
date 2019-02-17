package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
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
import java.util.ArrayList;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import dust.mj02.dust.Dust;
import dust.mj02.dust.knowledge.DustCommComponents;
import dust.mj02.dust.knowledge.DustCommDiscussion;
import dust.mj02.dust.knowledge.DustCommJsonLoader;
import dust.mj02.montru.gui.MontruGuiEditorModel;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;
import dust.utils.DustUtilsJavaSwing;

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

	class DesktopPanel extends JDesktopPane {
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

		public DesktopPanel() {
			pnlLinks = new MontruGuiSwingPanelLinks(MontruGuiSwingPanelEditor.this);
			pnlLinks.followParent(this);
			add(pnlLinks, JDesktopPane.POPUP_LAYER);

			addMouseListener(ml);
		}

		public void reloadData() {
			getEditorModel().refreshData();
			pnlControl.tmTypes.update();

			GuiEntityInfo eiEntity = getEditorModel().getEntityInfo(EntityResolver.getEntity(DustDataTypes.Entity));
			eiEntity.add(GuiEntityKey.showFlags, GuiShowFlag.hide);

			// removeAll();

			// add(pnlLinks, JDesktopPane.POPUP_LAYER);

			Iterable<GuiEntityInfo> allEntities = getEditorModel().getAllEntities();
			for (GuiEntityInfo ei : allEntities) {
				ei.put(GuiEntityKey.title, null);
				String t = ei.getTitle();
				activateEntity(ei, true).setTitle(t);
				// factIntFrames.get(ei);
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

	class EntityInfoRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel tc = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			tc.setText(((GuiEntityInfo) value).getTitle());
			return tc;
		}
	}

	class EntityInfoListModel extends AbstractListModel<GuiEntityInfo> {
		ArrayList<GuiEntityInfo> data;

		public EntityInfoListModel(ArrayList<GuiEntityInfo> data) {
			super();
			this.data = data;
		}

		@Override
		public GuiEntityInfo getElementAt(int index) {
			return data.get(index);
		}

		@Override
		public int getSize() {
			return data.size();
		}

		private void update() {
			fireContentsChanged(pnlControl, 0, getSize());
		}
	}

	enum TypeTableCols {
		sel, ei
	}

	class EditorControlPanel extends JPanel {

		class TypeTableModel extends AbstractTableModel {
			ArrayList<GuiEntityInfo> data;

			public TypeTableModel(ArrayList<GuiEntityInfo> data) {
				this.data = data;
			}

			private void update() {
				fireTableDataChanged();
			}

			@Override
			public int getColumnCount() {
				return TypeTableCols.values().length;
			}
			
			@Override
			public String getColumnName(int column) {
				return TypeTableCols.values()[column].name();
			}
			
			@Override
			public int getRowCount() {
				return data.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				GuiEntityInfo type = data.get(rowIndex);

				switch (TypeTableCols.values()[columnIndex]) {
				case ei:
					return type.getTitle();
				case sel:
					return setFilterTypes.contains(type);
				}
				return null;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return 0 == columnIndex;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				switch (TypeTableCols.values()[columnIndex]) {
				case sel:
					GuiEntityInfo type = data.get(rowIndex);
					if ((Boolean) aValue) {
						setFilterTypes.add(type);
					} else {
						setFilterTypes.remove(type);
					}
					doSearch();
				default:
					break;
				}
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (TypeTableCols.values()[columnIndex]) {
				case ei:
					return String.class;
				case sel:
					return Boolean.class;
				}
				return null;
			}
		}

		String filterText;
		Set<GuiEntityInfo> setFilterTypes;
		ArrayList<GuiEntityInfo> arrSearchResults;
		GuiEntityInfo eiSelected;

		TypeTableModel tmTypes;
		EntityInfoListModel lmResults;
		EntityInfoRenderer crTypes;

		JTextField tfSearch;
		JTextArea taSelEntity;

		public EditorControlPanel() {
			super(new BorderLayout(5, 5));

			setFilterTypes = new HashSet<>();
			arrSearchResults = new ArrayList<>();

			tmTypes = new TypeTableModel(editorModel.getAllTypes());
			lmResults = new EntityInfoListModel(arrSearchResults);
			crTypes = new EntityInfoRenderer();

			JPanel pnlSearch = new JPanel(new BorderLayout());

			tfSearch = new JTextField();
			DustSwingTextListener tl = new DustSwingTextListener(new DustSwingTextChangeProcessor() {
				@Override
				public void textChanged(String text, Object source, DocumentEvent e) {
					filterText = text.toLowerCase();
					doSearch();
				}
			});
			tl.listen(tfSearch);

			pnlSearch.add(DustUtilsJavaSwing.setBorder(tfSearch, "Search in content"), BorderLayout.NORTH);

			JTable tblTypes = new JTable(tmTypes);
			pnlSearch.add(DustUtilsJavaSwing.setBorderScroll(tblTypes, "Type filter"), BorderLayout.CENTER);

			JList<GuiEntityInfo> lstResults = new JList<GuiEntityInfo>(lmResults);

			lstResults.setCellRenderer(crTypes);
			lstResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lstResults.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					selectEntity(lstResults.getSelectedValue());
				}
			});
			lstResults.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if ( 1 < e.getClickCount() ) {
						activateEntity(eiSelected, true);
					}
				}
			});

			taSelEntity = new JTextArea();

			JSplitPane splResults = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					DustUtilsJavaSwing.setBorderScroll(lstResults, "Matching entities"),
					DustUtilsJavaSwing.setBorderScroll(taSelEntity, "Selected entity"));
			
			splResults.setDividerLocation(.5);
			splResults.setResizeWeight(.5);

			JSplitPane splMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlSearch, splResults);
			splMain.setDividerLocation(.5);
			splMain.setResizeWeight(0.2);

			add(splMain, BorderLayout.CENTER);

			JToolBar tbButtons = new JToolBar();

			for (Enum<?> m : GuiCommands.values()) {
				JButton btn = new JButton(m.name());
				btn.setActionCommand(m.name());
				tbButtons.add(btn);
				btn.addActionListener(cmdListener);
			}

			add(tbButtons, BorderLayout.SOUTH);
		}

		private void selectEntity(GuiEntityInfo ei) {
			eiSelected = ei;
			taSelEntity.setText(eiSelected.toString());
		}

		private void doSearch() {
			arrSearchResults.clear();
			
			for (GuiEntityInfo ei : editorModel.getAllEntities()) {
				Set<GuiEntityInfo> models = ei.get(GuiEntityKey.models);
				boolean ok = false;
				
				if (!setFilterTypes.isEmpty()) {
					ok = false;
					for (GuiEntityInfo fm : setFilterTypes ) {
						if ( models.contains(fm) ) {
							ok = true;
							break;
						}
					}
					
					if ( !ok ) { 
						continue;
					}
				}
				
				if ( !DustUtilsJava.isEmpty(filterText) ) {
					ok = false;
					DustEntity e = ei.get(GuiEntityKey.entity);
					
					for (GuiEntityInfo fm : models ) {
						Set<GuiEntityInfo> atts = fm.get(GuiEntityKey.attDefs);

						if ( null != atts ) {
							for (GuiEntityInfo ad : atts ) {
								Object val = Dust.accessEntity(DataCommand.getValue, e, ad.get(GuiEntityKey.entity), null, null);
								
								if ( val instanceof String ) {
									ok = ((String)val).toLowerCase().contains(filterText);
								}
								
								if ( ok ) {
									break;
								}
							}
						}
						
						if ( ok ) {
							break;
						}
					}
				}
				
				if ( ok ) {
					arrSearchResults.add(ei);
				}
			}

			lmResults.update();
		}
	}

	DesktopPanel pnlDesktop;
	EditorControlPanel pnlControl;

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

		pnlDesktop = new DesktopPanel();
		pnlControl = new EditorControlPanel();

		JSplitPane spMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlControl, new JScrollPane(pnlDesktop));
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