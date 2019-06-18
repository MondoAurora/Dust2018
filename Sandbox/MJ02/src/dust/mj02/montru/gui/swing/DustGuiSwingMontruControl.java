package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustTempHacks;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.java.DustJavaGen;
import dust.mj02.sandbox.DustSandboxJson;
import dust.mj02.sandbox.persistence.DustPersistence;
import dust.mj02.sandbox.persistence.DustPersistenceComponents;
import dust.utils.DustUtilsJava;
import dust.utils.DustUtilsJavaSwing;

@SuppressWarnings("serial")
class DustGuiSwingMontruControl extends JPanel implements DustGuiSwingMontruComponents, DustPersistenceComponents {
    private static final long serialVersionUID = 1L;

    enum GuiCommands {
        RESTORE, deleteEntity, deleteRef, update, commit, // setMaster, setSlave, // saveAll, //loadReflection, // createEntity, loadFile,
                                                 // test03
        clean,
    };

    ActionListener cmdListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (GuiCommands.valueOf(e.getActionCommand())) {
            // case createEntity:
            // break;
            case deleteEntity:
                desktop.deleteSelected();
                break;
            case deleteRef:
                desktop.removeSelRefs();
                break;
            case update:
                String name = DustSandboxJson.configGet(CFG_MONTRU, CFG_LASTUNIT);

                name = JOptionPane.showInputDialog(DustGuiSwingMontruControl.this, "Unit names (comma separated list)?", name);
                if (DustUtilsJava.isEmpty(name)) {
                    // name = "TextTest";
//                    name = "SrcGenCSharp";
//                    name = "FleetManagement";
                }
                DustPersistence.update(PERS_STORAGE_DEF_MULTI, name);
                desktop.refreshData();
                
                DustSandboxJson.configSet(name, CFG_MONTRU, CFG_LASTUNIT);
                
                final String n = name;
                try {
                    DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, true);

                    Dust.processRefs(new RefProcessor() {
                        @Override
                        public void processRef(DustRef ref) {
                            DustEntity s = ref.get(RefKey.source);
                            String id = DustUtils.accessEntity(DataCommand.getValue, s, DustGenericAtts.IdentifiedIdLocal);
                            if (n.equals(id)) {

                                desktop.activateEditorPanel(ref.get(RefKey.target));
                            }
                        }
                    }, null, EntityResolver.getEntity(DustCommLinks.UnitMainEntities), null);
                } finally {
                    DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, false);                                
                }
                
                tfSearch.setText("test");
                break;
            case commit:
                try {
                    DustPersistence.commit(PERS_STORAGE_DEF_MULTI);
                } catch (Throwable ex) {
                    Dust.wrapAndRethrowException("Commit exception", ex);
                    JOptionPane.showMessageDialog(DustGuiSwingMontruControl.this, ex.getMessage(), "Commit exception", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case RESTORE:
                String timestamp = JOptionPane.showInputDialog(DustGuiSwingMontruControl.this, "Restore timestamp?", "20190618_121717_717");
                if (!DustUtilsJava.isEmpty(timestamp)) {
                    DustPersistence.restoreFromHistory(PERS_STORAGE_DEF_MULTI, timestamp);
                }
                break;
            case clean:
                desktop.closeUnselected();
                break;
            // case setMaster:
            // DustPersistence.commit(PERS_STORAGE_DEF_SINGLE);
            // break;
            // case setSlave:
            // DustPersistence.update(PERS_STORAGE_DEF_SINGLE, "Text");
            // desktop.refreshData();
            // break;
            // case saveAll:
            // desktop.saveAll();
            // break;
            // case loadFile:
            // desktop.loadFiles(new File("MJ02Boot02.json"));
            //
            // break;
            // case test03:
            // if ( null != eiSelected ) {
            // DustUtils.accessEntity(DataCommand.setRef, desktop,
            // MontruGuiLinks.MontruDesktopActivePanel, eiSelected);
            // }
            // break;
            // case loadReflection:
            // loadReflection();
            // break;
            }
        }
    };

    enum TypeTableCols {
        sel, ei
    }

    class TypeTableModel extends EntityTableModelBase {
        public TypeTableModel(ArrayList<DustEntity> data) {
            super(data, TypeTableCols.values());
        }

        @Override
        public String getColumnName(int column) {
            return TypeTableCols.values()[column].name();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DustEntity type = data.get(rowIndex);

            switch (TypeTableCols.values()[columnIndex]) {
            case ei:
                return ( null == type ) ? "???" : type.toString();
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
    EntityListModelDefault lmResults;
    EntityRendererDefault crTypes;

    JTextField tfSearch;
    JTextArea taSelEntity;
    DustGuiSwingMontruDesktop desktop;
    private JList<DustEntity> lstResults;

    public DustGuiSwingMontruControl(DustGuiSwingMontruDesktop desktop) {
        super(new BorderLayout(5, 5));
        this.desktop = desktop;
        desktop.setControl(this);

        setFilterTypes = new HashSet<>();
        arrSearchResults = new ArrayList<>();

        tmTypes = new TypeTableModel((ArrayList<DustEntity>) desktop.eac.getAllTypes());
        lmResults = new EntityListModelDefault(arrSearchResults);
        crTypes = new EntityRendererDefault();

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

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tmTypes);
        tblTypes.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        
        lstResults = new JList<DustEntity>(lmResults);

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
                if ((null != eiSelected) && (1 < e.getClickCount())) {
                    desktop.activateEditorPanel(eiSelected);
                }
            }
        });

        taSelEntity = new JTextArea();

        JSplitPane splResults = new JSplitPane(JSplitPane.VERTICAL_SPLIT, DustUtilsJavaSwing.setBorderScroll(lstResults, "Matching entities"),
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

        DustJavaGen.init();

        loadReflection();
    }

    private void loadReflection() {
        desktop.refreshData();
        DustTempHacks.detectMetaConnections();
        desktop.refreshData();
    }

    private void selectEntity(DustEntity ei) {
        eiSelected = ei;
        taSelEntity.setText((null == ei) ? "<no selected item>" : eiSelected.toString());
    }

    private void doSearch() {
        arrSearchResults.clear();

        Dust.processEntities(new EntityProcessor() {
            boolean okModel;
            boolean okValue;

            @Override
            public void processEntity(DustEntity ei) {
                okModel = setFilterTypes.isEmpty();
                okValue = DustUtilsJava.isEmpty(filterText);

                if (!okModel || !okValue) {
                    DustUtils.accessEntity(DataCommand.processRef, ei, DustDataLinks.EntityModels, new RefProcessor() {
                        @Override
                        public void processRef(DustRef ref) {
                            DustEntity eModel = ref.get(RefKey.target);

                            if (!okModel) {
                                okModel = setFilterTypes.contains(eModel);
                            }

                            if (!okValue) {
                                DustUtils.accessEntity(DataCommand.processRef, eModel, DustMetaLinks.TypeAttDefs, new RefProcessor() {
                                    @Override
                                    public void processRef(DustRef ref) {
                                        DustEntity eAtt = ref.get(RefKey.target);

                                        if (!okValue) {
                                            Object val = Dust.accessEntity(DataCommand.getValue, ei, eAtt, null, null);
                                            if (val instanceof String) {
                                                okValue = ((String) val).toLowerCase().contains(filterText);
                                            }
                                        }
                                    }
                                });

                            }
                        }
                    });

                    if (okModel && okValue) {
                        arrSearchResults.add(ei);
                    }
                }
            }
        });
        
        arrSearchResults.sort(new Comparator<DustEntity>() {
            @Override
            public int compare(DustEntity o1, DustEntity o2) {
                String s1 = o1.toString();
                String s2 = o2.toString();
                return s1.compareTo(s2);
            }
        });

        lmResults.update();

        lstResults.setSelectedIndices(new int[] {});
        if (0 < lmResults.getSize()) {
            lstResults.setSelectedIndex(0);
        }
    }
}
