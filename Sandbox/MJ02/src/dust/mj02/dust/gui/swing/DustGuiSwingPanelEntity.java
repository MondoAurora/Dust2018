package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustGuiSwingPanelEntity extends JPanel
		implements DustGuiSwingComponents, DustProcComponents.DustProcListener, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;

	public static JPanel createComponent(DustEntity eEntity) {
		DustEntity eWidget = Dust.getEntity(null);
		DustEntity eSvc = EntityResolver.getEntity(DustGuiServices.PropertyPanel);

		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustGuiLinks.PropertyPanelEntity, eEntity);
		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustDataLinks.EntityServices, eSvc);

		DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextChangeListeners, eWidget);

		return DustUtils.getBinary(eWidget, eSvc);
	}

	private static final DustEntity DATT_ID = EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal);

	DustEntity eEntity;
	DustGuiSwingWidgetAnchor.Connector anchorConnector = new DustGuiSwingWidgetAnchor.Connector();

	DustUtilsFactory<DustEntity, JLabel> factLabel = new DustUtilsFactory<DustEntity, JLabel>(false) {
		@Override
		protected JLabel create(DustEntity key, Object... hints) {
			return DustGuiSwingWidgetLabel.createWidget((null == key) ? eEntity : key, DATT_ID);
		}
	};

	DustUtilsFactory<DustEntity, JComponent> factData = new DustUtilsFactory<DustEntity, JComponent>(false) {
		@Override
		protected JComponent create(DustEntity key, Object... hints) {
			return DustGuiSwingWidgetLabel.createWidget(eEntity, key);
		}
	};

	DustUtilsFactory<DustEntity, DustGuiSwingWidgetAnchor.AnchoredPanel> factAnchored = new DustUtilsFactory<DustEntity, DustGuiSwingWidgetAnchor.AnchoredPanel>(false) {
		@Override
		protected DustGuiSwingWidgetAnchor.AnchoredPanel create(DustEntity key, Object... hints) {
			JComponent comp = factLabel.get(key);
			if ( null == key ) {
				comp.setBackground(Color.LIGHT_GRAY);
				((JLabel)comp).setHorizontalAlignment(JLabel.CENTER);
				comp.setOpaque(true);
			} else {
				JPanel pnl = new JPanel(new BorderLayout(HR, 0));

				pnl.add(comp, BorderLayout.WEST);
				pnl.add(factData.get(key), BorderLayout.CENTER);
				
				comp = pnl;
			}
			return DustGuiSwingWidgetAnchor.anchorPanel(comp, anchorConnector, eEntity, key);
		}
	};

	public DustGuiSwingPanelEntity() {
		super(new GridLayout(0, 1));
		setBorder(BorderFactory.createEmptyBorder(ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER));
	}

	private void updatePanel() {
		removeAll();

		DustRef ref;

		ref = DustUtils.accessEntity(DataCommand.getValue, eEntity, DustDataLinks.EntityPrimaryType);
		DustEntity ePrimType = (null == ref) ? null : ref.get(RefKey.target);

		JComponent top = factAnchored.get(null);
		add(top);

		DustUtils.accessEntity(DataCommand.processRef, eEntity, DustDataLinks.EntityModels, new RefProcessor() {
			@Override
			public void processRef(DustRef ref) {
				DustEntity mType = ref.get(RefKey.target);

				JComponent head = factLabel.get(mType);
				if (mType == ePrimType) {
					head.setForeground(Color.RED);
				}
				add(head);

				DustUtils.accessEntity(DataCommand.processRef, mType, DustMetaLinks.TypeAttDefs, new RefProcessor() {
					@Override
					public void processRef(DustRef ref) {
						JPanel pnl = new JPanel(new BorderLayout(HR, 0));

						DustEntity att = ref.get(RefKey.target);
						pnl.add(factLabel.get(att), BorderLayout.WEST);
						pnl.add(factData.get(att), BorderLayout.CENTER);
						
						JPanel pnlRow = new JPanel(new BorderLayout(2*HR, 0));
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
		});

		revalidate();
		repaint();
		
//		Window w = SwingUtilities.getWindowAncestor(this);
//		if (null != w) {
//			w.pack();
//		}
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

		updatePanel();
	}

	@Override
	public void dustProcActiveRelease() throws Exception {
	}
}
