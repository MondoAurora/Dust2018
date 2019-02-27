package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
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
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.DustComponents.DataCommand;
import dust.mj02.dust.DustComponents.DustEntity;
import dust.mj02.dust.DustComponents.DustRef;
import dust.mj02.dust.gui.DustGuiComponents.DustGuiLinks;
import dust.utils.DustUtilsJava;
import dust.utils.DustUtilsJavaSwing;

class DustGuiSwingMontruControl extends JPanel implements DustGuiSwingComponents {
	private static final long serialVersionUID = 1L;
	
	enum GuiCommands {
		createEntity, deleteEntity, deleteRef, test01, test02, test03
	};

	ActionListener cmdListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (GuiCommands.valueOf(e.getActionCommand())) {
			case createEntity:
				break;
			case deleteEntity:
				break;
			case deleteRef:
				desktop.removeSelRefs();
				break;
			case test01:
				desktop.loadFiles(new File("MJ02Boot02.json"));

				break;
			case test02:
				desktop.saveAll();
				break;
			case test03:
				if ( null != eiSelected ) {
					DustUtils.accessEntity(DataCommand.setRef, desktop, DustGuiLinks.MontruDesktopActivePanel, eiSelected);
				}
				break;
			}
		}
	};

	class EntityInfoRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel tc = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			tc.setText(DustUtilsJava.toString(value));
			return tc;
		}
	}

	class EntityInfoListModel extends AbstractListModel<DustEntity> {
		ArrayList<DustEntity> data;

		public EntityInfoListModel(ArrayList<DustEntity> data) {
			super();
			this.data = data;
		}

		@Override
		public DustEntity getElementAt(int index) {
			return data.get(index);
		}

		@Override
		public int getSize() {
			return data.size();
		}

		private void update() {
			fireContentsChanged(DustGuiSwingMontruControl.this, 0, getSize());
		}
	}

	enum TypeTableCols {
		sel, ei
	}

	class TypeTableModel extends AbstractTableModel {
		ArrayList<DustEntity> data;

		public TypeTableModel(ArrayList<DustEntity> data) {
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
			DustEntity type = data.get(rowIndex);

			switch (TypeTableCols.values()[columnIndex]) {
			case ei:
				return type.toString();
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
				DustEntity type = data.get(rowIndex);
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
	Set<DustEntity> setFilterTypes;
	ArrayList<DustEntity> arrSearchResults;
	DustEntity eiSelected;

	TypeTableModel tmTypes;
	EntityInfoListModel lmResults;
	EntityInfoRenderer crTypes;

	JTextField tfSearch;
	JTextArea taSelEntity;
	DustGuiSwingMontruDesktop desktop;

	public DustGuiSwingMontruControl(DustGuiSwingMontruDesktop desktop) {
		super(new BorderLayout(5, 5));
		this.desktop = desktop;

		setFilterTypes = new HashSet<>();
		arrSearchResults = new ArrayList<>();

		tmTypes = new TypeTableModel(desktop.arrTypes);
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

		JList<DustEntity> lstResults = new JList<DustEntity>(lmResults);

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
				if (1 < e.getClickCount()) {
					desktop.activateEditorPanel(eiSelected);
//					DustEntity eSel = pnlControl.eiSelected.get(GuiEntityKey.entity);
//					DustUtils.accessEntity(DataCommand.setRef, eMontruDesktop, DustGuiLinks.MontruDesktopActivePanel,
//							eSel);
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

	private void selectEntity(DustEntity ei) {
		eiSelected = ei;
		taSelEntity.setText(eiSelected.toString());
	}

	private void doSearch() {
		arrSearchResults.clear();

		Dust.processEntities(new EntityProcessor() {
			boolean okModel;
			boolean okValue;

			@Override
			public void processEntity(Object key, DustEntity ei) {
				okModel = setFilterTypes.isEmpty();
				okValue = DustUtilsJava.isEmpty(filterText);
				
				if ( !okModel || !okValue ) {
					DustUtils.accessEntity(DataCommand.processRef, ei, DustDataLinks.EntityModels, null, new RefProcessor() {
						@Override
						public void processRef(DustRef ref) {
							
						}
					});
				
					if ( okModel && okValue ) {
						arrSearchResults.add(ei);
					}
				}
				
//				Set<DustEntity> models = ei.get(GuiEntityKey.models);
//				boolean ok = false;
//
//				if (!setFilterTypes.isEmpty()) {
//					ok = false;
//					for (DustEntity fm : setFilterTypes) {
//						if (models.contains(fm)) {
//							ok = true;
//							break;
//						}
//					}
//
//					if (!ok) {
//						continue;
//					}
//				}
//
//				if (!DustUtilsJava.isEmpty(filterText)) {
//					ok = false;
//
//					for (DustEntity fm : models) {
//						Set<DustEntity> atts = fm.get(GuiEntityKey.attDefs);
//
//						if (null != atts) {
//							for (DustEntity ad : atts) {
//								Object val = Dust.accessEntity(DataCommand.getValue, ei, ad.get(GuiEntityKey.entity), null,
//										null);
//
//								if (val instanceof String) {
//									ok = ((String) val).toLowerCase().contains(filterText);
//								}
//
//								if (ok) {
//									break;
//								}
//							}
//						}
//
//						if (ok) {
//							break;
//						}
//					}
//				}

//				if (ok) {
//					arrSearchResults.add(ei);
//				}
			
			}
		});

		lmResults.update();
	}
}
