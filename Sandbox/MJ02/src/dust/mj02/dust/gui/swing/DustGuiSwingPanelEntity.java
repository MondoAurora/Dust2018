package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.text.DustTextComponents;
import dust.mj02.montru.gui.MontruGuiComponents.MontruGuiServices;
import dust.mj02.montru.gui.swing.DustGuiSwingMontruDesktop;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustGuiSwingPanelEntity extends JPanel
        implements DustGuiSwingComponents, DustTextComponents, DustProcComponents.DustProcListener, DustProcComponents.DustProcActive {
    private static final long serialVersionUID = 1L;

    private static final Object RB_LINK_KEY = new Object();

    private static final DustEntity DATT_ID = EntityResolver.getEntity(DustGenericAtts.IdentifiedIdLocal);

    static abstract class TextPanelBase extends JPanel {
        private static final long serialVersionUID = 1L;

        DustEntity entity;
        JTextArea textArea;

        public TextPanelBase(DustEntity entity, boolean editable) {
            super(new BorderLayout());

            this.entity = entity;

            textArea = new JTextArea();
            textArea.setEditable(editable);

            add(new JScrollPane(textArea), BorderLayout.CENTER);
        }
    }

    static class TextSpanPanel extends TextPanelBase implements DustSwingTextChangeProcessor {
        private static final long serialVersionUID = 1L;

        DustSwingTextListener al = new DustSwingTextListener(this);

        public TextSpanPanel(DustEntity entity) {
            super(entity, true);
            
            String text = DustUtils.accessEntity(DataCommand.getValue, entity, DustTextAtts.TextSpanString);
            textArea.setText(text);

            al.listen(textArea);
        }

        @Override
        public void textChanged(String text, Object source, DocumentEvent e) {
            text = textArea.getText();
            DustUtils.accessEntity(DataCommand.setValue, entity, DustTextAtts.TextSpanString, text);
        }
    }

    enum RendererPanelCmds {
        Refresh
    }

    static class TextRendererPanel extends TextPanelBase {
        private static final long serialVersionUID = 1L;
        
        DustEntity eMgsEval;

        DustSwingCommandManager<RendererPanelCmds> cm = new DustSwingCommandManager<RendererPanelCmds>(RendererPanelCmds.class) {
            @Override
            protected void execute(RendererPanelCmds cmd) throws Exception {
                switch (cmd) {
                case Refresh:
                    if (null == eMgsEval) {
                        eMgsEval = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);
                        DustUtils.accessEntity(DataCommand.setRef, eMgsEval, DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);
                    }

                    DustUtils.accessEntity(DataCommand.tempSend, entity, eMgsEval);

                    String txt = DustUtils.accessEntity(DataCommand.getValue, eMgsEval, DustDataAtts.MessageReturn);

                    textArea.setText(txt);
                    textArea.setCaretPosition(0);
                    
                    break;
                }
            }
        };

        public TextRendererPanel(DustEntity entity) {
            super(entity, false);

            JPanel pnlBtns = new JPanel(new FlowLayout());
            cm.loadAll(pnlBtns);

            add(pnlBtns, BorderLayout.SOUTH);
        }
    }

    private static final Map<DustEntity, Class<? extends TextPanelBase>> SPEC_PANELS = new HashMap<>();

    static {
        SPEC_PANELS.put(EntityResolver.getEntity(DustTextTypes.TextSpan), TextSpanPanel.class);
        SPEC_PANELS.put(EntityResolver.getEntity(DustTextTypes.TextRenderer), TextRendererPanel.class);
    }

    DustEntity eEntity;
    DustGuiSwingEntityActionControl eac;
    // DustGuiSwingMontruDesktop desktop;

    DustUtilsFactory<DustEntity, JCheckBox> factModelSelector = new DustUtilsFactory<DustEntity, JCheckBox>(false) {
        @Override
        protected JCheckBox create(DustEntity key, Object... hints) {
            return new JCheckBox();
        }
    };

    DustUtilsFactory<DustEntity, JLabel> factLabel = new DustUtilsFactory<DustEntity, JLabel>(false) {
        @Override
        protected JLabel create(DustEntity key, Object... hints) {
            DustEntity eLabel = DustUtils.accessEntity(DataCommand.getEntity, DustGuiTypes.Label, ContextRef.self, null, new EntityProcessor() {
                @Override
                public void processEntity(DustEntity entity) {
                    DustUtils.accessEntity(DataCommand.setRef, entity, DustProcLinks.ChangeEntity, (null == key) ? eEntity : key);
                    DustUtils.accessEntity(DataCommand.setRef, entity, DustProcLinks.ChangeKey, DATT_ID);
                }
            });

            DustGuiSwingWidgetLabel lbl = DustUtils.getBinary(eLabel, DustGuiServices.Label);

            if (null != key) {
                eac.setLabel(lbl);
            }

            return lbl;
        }
    };

    DustUtilsFactory<DustEntity, JComponent> factData = new DustUtilsFactory<DustEntity, JComponent>(false) {
        @SuppressWarnings("unchecked")
        @Override
        protected JComponent create(DustEntity key, Object... hints) {
            boolean attValue = (boolean) hints[0];
            boolean txt = attValue;

            if (attValue) {
                if (DustUtils.tag(key, TagCommand.test, DustMetaTags.AttRaw)) {
                    txt = false;
                }
            }

            DustEntity eData = DustUtils.accessEntity(DataCommand.getEntity, txt ? DustGuiTypes.TextField : DustGuiTypes.Label, ContextRef.self, null,
                    new EntityProcessor() {
                        @Override
                        public void processEntity(DustEntity entity) {
                            DustUtils.accessEntity(DataCommand.setRef, entity, DustProcLinks.ChangeEntity, eEntity);
                            DustUtils.accessEntity(DataCommand.setRef, entity, DustProcLinks.ChangeKey, key);
                        }
                    });

            JComponent bin = DustUtils.getBinary(eData, txt ? DustGuiServices.TextField : DustGuiServices.Label);

            if (!attValue) {
                eac.setRefList((GuiDataWrapper<? extends JComponent>) bin);
            }

            if (!txt && DustUtils.isMultiLink(key)) {
                JPanel pnl = new JPanel(new BorderLayout());
                pnl.add(bin, BorderLayout.CENTER);
                JRadioButton rbEdit = new JRadioButton();
                rbEdit.putClientProperty(RB_LINK_KEY, key);
                grpMultiEdit.add(rbEdit);
                rbEdit.addActionListener(alMultiEdit);
                pnl.add(rbEdit, BorderLayout.EAST);
                bin = pnl;
            }

            return bin;
        }
    };

    DustUtilsFactory<DustEntity, DustGuiSwingWidgetAnchor.AnchoredPanel> factAnchored = new DustUtilsFactory<DustEntity, DustGuiSwingWidgetAnchor.AnchoredPanel>(
            false) {
        @Override
        protected DustGuiSwingWidgetAnchor.AnchoredPanel create(DustEntity key, Object... hints) {
            JComponent comp = factLabel.get(key);
            JPanel pnl = new JPanel(new BorderLayout(HR, 0));
            AnchorType at;

            if (null == key) {
                ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                comp.setOpaque(true);
                lblHead = comp;

                pnl.add(cbSelEntity, BorderLayout.WEST);
                pnl.add(comp, BorderLayout.CENTER);
                pnl.add(btDelModels, BorderLayout.EAST);

                DustEntity ePt = DustUtils.toEntity(DustUtils.accessEntity(DataCommand.getValue, eEntity, DustDataLinks.EntityPrimaryType));

                at = EntityResolver.getEntity(DustDataTypes.Message) == ePt ? AnchorType.MessageHead : AnchorType.EntityHead;
            } else {
                pnl.add(comp, BorderLayout.WEST);
                pnl.add(factData.get(key, false), BorderLayout.CENTER);
                at = AnchorType.Link;
            }
            return DustGuiSwingWidgetAnchor.anchorPanel(pnl, eac, eEntity, key, at);
        }
    };

    ButtonGroup grpMultiEdit = new ButtonGroup();
    ActionListener alMultiEdit = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            JRadioButton btn = (JRadioButton) arg0.getSource();
            if (btn.isSelected()) {
                DustEntity eLinkDef = (DustEntity) btn.getClientProperty(RB_LINK_KEY);
                refCollEd.setLinkDef(eLinkDef);
            }
        }
    };

    JComponent lblHead;
    JButton btDelModels;
    JCheckBox cbSelEntity;

    JPanel pnlGeneric = new JPanel(new GridLayout(0, 1));
    JTabbedPane tpCenter = new JTabbedPane();
    DustGuiSwingWidgetRefCollEditor refCollEd;

    public DustGuiSwingPanelEntity() {
        super(new BorderLayout());
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER)));

        btDelModels = new JButton("Delete models");
        btDelModels.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<DustEntity> s = new HashSet<>();
                for (DustEntity em : factModelSelector.keys()) {
                    if (factModelSelector.get(em).isSelected()) {
                        s.add(em);
                    }
                }
                if (!s.isEmpty()) {
                    DustUtils.accessEntity(DataCommand.removeRef, eEntity, DustDataLinks.EntityModels, s);
                    updatePanel();
                }
            }
        });

        cbSelEntity = new JCheckBox("Select Entity");
        cbSelEntity.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                if (eac.select(cbSelEntity.isSelected() ? CollectionAction.add : CollectionAction.remove, eEntity)) {
                    updateHead();
                }
            }
        });

        refCollEd = new DustGuiSwingWidgetRefCollEditor() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void process(Cmds cmd, Collection<DustRef> refs) {
                switch (cmd) {
                case Show:
                    Set<DustEntity> e = new HashSet<>();
                    for (DustRef r : refs) {
                        e.add(r.get(RefKey.target));
                    }
                    eac.activateEntities(e.toArray(new DustEntity[e.size()]));
                    break;
                case Del:
                    for (DustRef r : refs) {
                        Dust.accessEntity(DataCommand.removeRef, eEntity, eLinkDef, r.get(RefKey.target), null);
                    }
                    break;
                default:
                    break;
                }
            }
        };

        JSplitPane spMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tpCenter, refCollEd);

        spMain.setResizeWeight(1.0);

        add(spMain, BorderLayout.CENTER);
    }

    public void setEntityActionControl(DustGuiSwingEntityActionControl eac) {
        this.eac = eac;
    }

    public DustGuiSwingWidgetAnchor.AnchoredPanel peekAnchored(DustEntity entity) {
        return factAnchored.peek(entity);
    }

    private void updateHead() {
        lblHead.setBackground(eac.select(CollectionAction.contains, eEntity) ? COL_ENTITY_HEAD_SEL : COL_ENTITY_HEAD_NORM);
    }

    private void updatePanel() {
        if (0 == tpCenter.getTabCount()) {
            JComponent top = factAnchored.get(null);
            add(top, BorderLayout.NORTH);

            tpCenter.addTab("Generic", new JScrollPane(pnlGeneric));

            refCollEd.setEntity(eEntity);
        }

        pnlGeneric.removeAll();

        DustRef ref;

        ref = DustUtils.accessEntity(DataCommand.getValue, eEntity, DustDataLinks.EntityPrimaryType);
        DustEntity ePrimType = (null == ref) ? null : ref.get(RefKey.target);

        // JComponent top = factAnchored.get(null);
        // pnlGeneric.add(top);

        updateHead();
        cbSelEntity.setSelected(eac.select(CollectionAction.contains, eEntity));

        DustRef models = DustUtils.accessEntity(DataCommand.getValue, eEntity, DustDataLinks.EntityModels);

        for (DustEntity mType : eac.getAllTypes()) {
            if ((null != models) && models.contains(mType)) {
                // JPanel pnlModel = new JPanel(new GridLayout(0, 1));
                // pnlGeneric.add(pnlModel);

                JPanel pnl = new JPanel(new BorderLayout(HR, 0));
                JComponent head = factLabel.get(mType);
                pnl.add(head, BorderLayout.CENTER);
                boolean pm = (mType == ePrimType);

                if (pm) {
                    head.setForeground(Color.RED);
                } else {
                    pnl.add(factModelSelector.get(mType), BorderLayout.EAST);
                }
                pnl.add(new DustGuiSwingWidgetAnchor(eac, eEntity, mType, !pm, false, pm ? AnchorType.PrimaryModel : AnchorType.Model),
                        BorderLayout.WEST);
                pnl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                pnlGeneric.add(pnl);

                Class<? extends TextPanelBase> ct = SPEC_PANELS.get(mType);
                if (null != ct) {
                    String lbl = ct.getSimpleName();
                    for (int i = tpCenter.getTabCount(); i-- > 0;) {
                        if (lbl.equals(tpCenter.getTitleAt(i))) {
                            lbl = null;
                            break;
                        }
                    }

                    if (null != lbl) {
                        try {
                            TextPanelBase tpb = ct.getConstructor(DustEntity.class).newInstance(eEntity);
                            tpCenter.addTab(lbl, tpb);
                        } catch (Exception e) {
                            Dust.wrapAndRethrowException("", e);
                        }
                    }
                }

                DustUtils.accessEntity(DataCommand.processRef, mType, DustMetaLinks.TypeAttDefs, new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        DustEntity att = ref.get(RefKey.target);
                        if (DustUtils.tag(att, TagCommand.test, DustGuiTags.ItemHidden)) {
                            return;
                        }

                        if (DustUtils.tag(att, TagCommand.test, DustMetaTags.AttRaw)) {
                            return;
                        }

                        JPanel pnl = new JPanel(new BorderLayout(HR, 0));

                        pnl.add(factLabel.get(att), BorderLayout.WEST);

                        JComponent compData = factData.get(att, true);
                        if (null != compData) {
                            pnl.add(compData, BorderLayout.CENTER);
                        } else {
                            pnl.add(new JLabel("what?"), BorderLayout.CENTER);
                        }

                        JPanel pnlRow = new JPanel(new BorderLayout(2 * HR, 0));
                        pnlRow.add(Box.createRigidArea(ANCHOR_SIZE), BorderLayout.WEST);
                        pnlRow.add(pnl, BorderLayout.CENTER);
                        // pnlRow.add(Box.createRigidArea(ANCHOR_SIZE), BorderLayout.EAST);

                        pnlGeneric.add(pnlRow);
                    }
                });

                DustUtils.accessEntity(DataCommand.processRef, mType, DustMetaLinks.TypeLinkDefs, new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        DustEntity link = ref.get(RefKey.target);
                        JPanel pnl = factAnchored.get(link);
                        pnlGeneric.add(pnl);
                    }
                });
            }
        }

        revalidate();
        repaint();

        refCollEd.updateListContent();

        for (Container c = getParent(); null != c; c = c.getParent()) {
            if (c instanceof JInternalFrame) {
                ((JInternalFrame) c).pack();
                break;
            }
        }
    }

    @Override
    public void dustProcListenerProcessChange() throws Exception {
        DustEntity eChanged = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg, DustProcLinks.ChangeEntity)).get(RefKey.target);

        if (eChanged == eEntity) {
            updatePanel();
            return;
        } else {
            DustRef kr = DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg, DustProcLinks.ChangeKey);
            int ti = DustUtilsJava.indexOf(EntityResolver.getKey(kr.get(RefKey.target)), DustMetaLinks.TypeAttDefs, DustMetaLinks.TypeLinkDefs);
            if (-1 != ti) {
                DustRef rModels = DustUtils.accessEntity(DataCommand.getValue, eEntity, DustDataLinks.EntityModels);
                if ((null != rModels) && rModels.contains(eChanged)) {
                    updatePanel();
                }
            }
        }
    }

    @Override
    public void activeInit() throws Exception {
        eEntity = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustGuiLinks.PropertyPanelEntity)).get(RefKey.target);

        DustEntity eParent = DustUtils.getCtxVal(ContextRef.self, DustGenericLinks.ConnectedOwner, true);
        DustGuiSwingMontruDesktop desktop = DustUtils.getBinary(eParent, MontruGuiServices.MontruDesktop);

        eac = desktop.getEac();

        updatePanel();

        DustUtils.accessEntity(DataCommand.setRef, ContextRef.session, DustProcLinks.SessionChangeListeners, ContextRef.self);
    }

    @Override
    public void activeRelease() throws Exception {
    }
}
