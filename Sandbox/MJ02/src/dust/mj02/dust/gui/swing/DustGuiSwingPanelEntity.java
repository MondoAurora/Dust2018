package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.gui.DustGuiEntityActionControl.CollectionAction;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.montru.gui.MontruGuiComponents.MontruGuiServices;
import dust.mj02.montru.gui.swing.DustGuiSwingMontruDesktop;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustGuiSwingPanelEntity extends JPanel
		implements DustGuiSwingComponents, DustProcComponents.DustProcListener, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;

	private static final DustEntity DATT_ID = EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal);

	DustEntity eEntity;
	DustGuiSwingEntityActionControl eac;
//	DustGuiSwingMontruDesktop desktop;
	
	DustUtilsFactory<DustEntity, JCheckBox> factModelSelector = new DustUtilsFactory<DustEntity, JCheckBox>(false) {
		@Override
		protected JCheckBox create(DustEntity key, Object... hints) {
			return new JCheckBox();
		}
	};
	
	DustUtilsFactory<DustEntity, JLabel> factLabel = new DustUtilsFactory<DustEntity, JLabel>(false) {
		@Override
		protected JLabel create(DustEntity key, Object... hints) {
			DustEntity eLabel = DustUtils.accessEntity(DataCommand.getEntity, DustGuiTypes.Label, ContextRef.self, null,
					new EntityProcessor() {
						@Override
						public void processEntity(Object gid, DustEntity entity) {
							DustUtils.accessEntity(DataCommand.setRef, entity, DustProcLinks.ChangeEntity,
									(null == key) ? eEntity : key);
							DustUtils.accessEntity(DataCommand.setRef, entity, DustProcLinks.ChangeKey, DATT_ID);
						}
					});

			DustGuiSwingWidgetLabel lbl = DustUtils.getBinary(eLabel, DustGuiServices.Label);
			
			if ( null != key ) {
				eac.setLabel(lbl);
			}
			
			return lbl;
		}
	};

	DustUtilsFactory<DustEntity, JComponent> factData = new DustUtilsFactory<DustEntity, JComponent>(false) {
		@SuppressWarnings("unchecked")
		@Override
		protected JComponent create(DustEntity key, Object... hints) {
			boolean txt = (boolean) hints[0];

			DustEntity eData = DustUtils.accessEntity(DataCommand.getEntity,
					txt ? DustGuiTypes.TextField : DustGuiTypes.Label, ContextRef.self, null, new EntityProcessor() {
						@Override
						public void processEntity(Object gid, DustEntity entity) {
							DustUtils.accessEntity(DataCommand.setRef, entity, DustProcLinks.ChangeEntity, eEntity);
							DustUtils.accessEntity(DataCommand.setRef, entity, DustProcLinks.ChangeKey, key);
						}
					});

			JComponent bin = DustUtils.getBinary(eData, txt ? DustGuiServices.TextField : DustGuiServices.Label);
			
			if ( !txt ) {
				eac.setRefList((GuiDataWrapper<? extends JComponent>) bin);
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

			if (null == key) {
//				comp.setBackground(Color.LIGHT_GRAY);
				((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
				comp.setOpaque(true);
				lblHead = comp;
				
				pnl.add(cbSelEntity, BorderLayout.WEST);
				pnl.add(comp, BorderLayout.CENTER);
				pnl.add(btDelModels, BorderLayout.EAST);
			} else {
				pnl.add(comp, BorderLayout.WEST);
				pnl.add(factData.get(key, false), BorderLayout.CENTER);
			}
			return DustGuiSwingWidgetAnchor.anchorPanel(pnl, eac, eEntity, key);
		}
	};
	
	JComponent lblHead;
	JButton btDelModels;
	JCheckBox cbSelEntity;

	public DustGuiSwingPanelEntity() {
		super(new GridLayout(0, 1));
		setOpaque(true);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
				BorderFactory.createEmptyBorder(ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER,
						ENTITY_PANEL_BORDER)));
		
		btDelModels = new JButton("Delete models");
		btDelModels.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<DustEntity> s = new HashSet<>();
				for ( DustEntity em : factModelSelector.keys() ) {
					if ( factModelSelector.get(em).isSelected() ) {
						s.add(em);
					}
				}
				if ( !s.isEmpty() ) {
					DustUtils.accessEntity(DataCommand.clearRefs, eEntity, DustDataLinks.EntityModels, s);
					updatePanel();
				}
			}
		});
		
		cbSelEntity = new JCheckBox("Select Entity");
		cbSelEntity.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if ( eac.select(cbSelEntity.isSelected() ? CollectionAction.add : CollectionAction.remove, eEntity) ) {
					updateHead();
				}
			}
		});
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
		removeAll();

		DustRef ref;

		ref = DustUtils.accessEntity(DataCommand.getValue, eEntity, DustDataLinks.EntityPrimaryType);
		DustEntity ePrimType = (null == ref) ? null : ref.get(RefKey.target);

		JComponent top = factAnchored.get(null);
		add(top);
		
		updateHead();
		cbSelEntity.setSelected(eac.select(CollectionAction.contains, eEntity));

		DustRef models = DustUtils.accessEntity(DataCommand.getValue, eEntity, DustDataLinks.EntityModels);
		
		for ( DustEntity mType : eac.getAllTypes() ) {
			if ( models.contains(mType) ) {
				JComponent head = factLabel.get(mType);
				if (mType == ePrimType) {
					head.setForeground(Color.RED);
				} else {
					JPanel pnl = new JPanel(new BorderLayout(HR, 0));
					pnl.add(factModelSelector.get(mType), BorderLayout.EAST);
					pnl.add(head, BorderLayout.CENTER);
					head = pnl;
				}
				add(head);

				DustUtils.accessEntity(DataCommand.processRef, mType, DustMetaLinks.TypeAttDefs, new RefProcessor() {
					@Override
					public void processRef(DustRef ref) {
						JPanel pnl = new JPanel(new BorderLayout(HR, 0));

						DustEntity att = ref.get(RefKey.target);
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

						add(pnlRow);
					}
				});

				DustUtils.accessEntity(DataCommand.processRef, mType, DustMetaLinks.TypeLinkDefs, new RefProcessor() {
					@Override
					public void processRef(DustRef ref) {
						DustEntity link = ref.get(RefKey.target);
						JPanel pnl = factAnchored.get(link);
						add(pnl);
					}
				});
			}
		}

		revalidate();
		repaint();

		for (Container c = getParent(); null != c; c = c.getParent()) {
			if (c instanceof JInternalFrame) {
				((JInternalFrame) c).pack();
				break;
			}
		}
	}

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		DustEntity eChanged = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg,
				DustProcLinks.ChangeEntity)).get(RefKey.target);

		if (eChanged == eEntity) {
			updatePanel();
			return;
		} else {
			int ti = DustUtilsJava.indexOf(
					DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg, DustProcLinks.ChangeKey),
					DustMetaLinks.TypeAttDefs, DustMetaLinks.TypeLinkDefs);
			if (-1 != ti) {
				DustRef rModels = DustUtils.accessEntity(DataCommand.getValue, eChanged, DustDataLinks.EntityModels);
				rModels.processAll(new RefProcessor() {
					@Override
					public void processRef(DustRef ref) {
						if (eChanged == ref.get(RefKey.target)) {
							updatePanel();
						}
					}
				});
			}
		}
	}

	@Override
	public void dustProcActiveInit() throws Exception {
		eEntity = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, ContextRef.self,
				DustGuiLinks.PropertyPanelEntity)).get(RefKey.target);
		
		DustEntity eParent = DustUtils.getCtxVal(ContextRef.self, DustGenericLinks.Owner, true);
		DustGuiSwingMontruDesktop desktop = DustUtils.getBinary(eParent, MontruGuiServices.MontruDesktop);
		
		eac = desktop.getEac();

		updatePanel();

		DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextChangeListeners,
				ContextRef.self);
	}

	@Override
	public void dustProcActiveRelease() throws Exception {
	}
}
