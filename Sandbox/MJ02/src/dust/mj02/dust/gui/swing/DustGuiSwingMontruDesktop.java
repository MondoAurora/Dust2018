package dust.mj02.dust.gui.swing;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustGuiSwingMontruDesktop extends JPanel
		implements DustGuiSwingComponents, DustProcComponents.DustProcListener, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;

	public DustGuiSwingMontruDesktop() {
		super(null);
		setOpaque(true);
		setBackground(Color.white);
		
		setBorder(BorderFactory.createEmptyBorder(ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER));
	}
	
	private void activateEditorPanel(DustEntity e) {
		String id = e.toString();
		
		DustEntity ePanel = DustUtils.accessEntity(DataCommand.getEntity, DustGuiTypes.PropertyPanel, ContextRef.self, id, new EntityProcessor() {
			@Override
			public void processEntity(Object key, DustEntity entity) {
				DustUtils.accessEntity(DataCommand.setRef, entity, DustGuiLinks.PropertyPanelEntity, e);
			}
		});
		
		JPanel pnl = DustUtils.getBinary(ePanel, DustGuiServices.PropertyPanel);
		
		add(pnl);
		
		pnl.setLocation(0, 0);
		pnl.setSize(pnl.getPreferredSize());
		
		revalidate();
		repaint();
	}


	@Override
	public void dustProcListenerProcessChange() throws Exception {
		DustEntity key = DustUtils.getMsgVal(DustProcLinks.ChangeKey, true);
		if ( DustGuiLinks.MontruDesktopActivePanel == EntityResolver.getKey(key)) {
			activateEditorPanel(DustUtils.getMsgVal(DustProcAtts.ChangeNewValue, true));
		}
	}

	@Override
	public void dustProcActiveInit() throws Exception {
		DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextChangeListeners, ContextRef.self);
	}

	@Override
	public void dustProcActiveRelease() throws Exception {
	}
}
