package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.gui.DustGuiComponents;

public class DustGuiSwingWidgetRefCollEditor extends JPanel implements DustGuiComponents {

    private static final long serialVersionUID = 1L;

    enum Cmds {
        Show, Up, Down, Del
    }

    DustSwingCommandManager<Cmds> cm = new DustSwingCommandManager<Cmds>(Cmds.class) {
        @Override
        protected void execute(Cmds cmd) throws Exception {
            int[] si = lst.getSelectedIndices();
            int l = si.length;

            Collection<DustRef> selRefs = lst.getSelectedValuesList();
            switch (cmd) {
            case Down:
                for (int ii = l; ii-- > 0;) {
                    int idx = si[ii];
                    Dust.accessEntity(DataCommand.updateRef, entity, eLinkDef, lmLinks.getElementAt(idx).get(RefKey.target), idx + 1);
                }
                break;
            case Up:
                for (int idx : si) {
                    Dust.accessEntity(DataCommand.updateRef, entity, eLinkDef, lmLinks.getElementAt(idx).get(RefKey.target), idx - 1);
                }
                break;
            case Show:
                process(cmd, selRefs);
                break;
            case Del:
                process(cmd, selRefs);
                break;
            }

            updateListContent();
        }

        @Override
        public void updateStates() {
            int[] si = lst.getSelectedIndices();
            int l = si.length;

            if (0 < l) {
                setEnabled(true, Cmds.Show, Cmds.Del);
                if (DustMetaLinkDefTypeValues.LinkDefArray == lt) {
                    setEnabled(0 < si[0], Cmds.Up);
                    setEnabled(lmLinks.size() - 1 > si[l - 1], Cmds.Down);
                } else {
                    setEnabled(false, Cmds.Up, Cmds.Down);
                }
            } else {
                setEnabled(false, Cmds.values());
            }
        };
    };

    DustEntity entity;
    DustEntity eLinkDef;
    DustMetaLinkDefTypeValues lt;

    JLabel lblHead = new JLabel();
    JList<DustRef> lst;
    DefaultListModel<DustRef> lmLinks = new DefaultListModel<>();

    public DustGuiSwingWidgetRefCollEditor() {
        super(new BorderLayout());

        add(lblHead, BorderLayout.NORTH);

        lst = new JList<>(lmLinks);

        lst.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        lst.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cm.updateStates();
                }
            }
        });

        DefaultListCellRenderer lcr = new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ((DustRef) value).get(RefKey.target), index, isSelected, cellHasFocus);
            }
        };

        lst.setCellRenderer(lcr);

        add(new JScrollPane(lst), BorderLayout.CENTER);

        JToolBar tbCmds = new JToolBar();

        cm.loadAll(tbCmds);
        add(tbCmds, BorderLayout.SOUTH);
    }

    public void setEntity(DustEntity entity) {
        this.entity = entity;
    }

    public void setLinkDef(DustEntity eLinkDef) {
        if (this.eLinkDef != eLinkDef) {
            this.eLinkDef = eLinkDef;
            lt = DustUtils.getLinkType(eLinkDef);

            lblHead.setText((null == eLinkDef) ? "Select a link" : eLinkDef.toString());

            lst.clearSelection();
            updateListContent();

            cm.updateStates();
        }
    }

    public void updateListContent() {
        Collection<DustRef> refs = new HashSet<>(lst.getSelectedValuesList());

        lmLinks.clear();
        if (null != eLinkDef) {
            DustUtils.accessEntity(DataCommand.processRef, entity, eLinkDef, new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    lmLinks.addElement(ref);
                }
            });

            for (DustRef r : refs) {
                lst.setSelectedValue(r, false);
            }
        }
    }

    protected void process(Cmds cmd, Collection<DustRef> refs) {

    }

}
