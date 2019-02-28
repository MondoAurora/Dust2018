package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.sandbox.DustSandboxJsonLoader;
import dust.mj02.sandbox.DustSandboxListenerDump;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustGuiSwingMontruDesktop extends JDesktopPane
		implements DustGuiSwingComponents, DustProcComponents.DustProcListener, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;
	
	class EntityDocWindow {
		DustEntity eEntity;
		
		DustEntity ePanel;
		JInternalFrame iFrame;
		DustGuiSwingPanelEntity pnl;
		
		public EntityDocWindow(DustEntity eEntity) {
			this.eEntity = eEntity;
			
			ePanel = DustUtils.accessEntity(DataCommand.getEntity, DustGuiTypes.PropertyPanel, ContextRef.self, null, new EntityProcessor() {
				@Override
				public void processEntity(Object key, DustEntity entity) {
					DustUtils.accessEntity(DataCommand.setRef, entity, DustGuiLinks.PropertyPanelEntity, eEntity);
				}
			});
			
			pnl = DustUtils.getBinary(ePanel, DustGuiServices.PropertyPanel);
			
			iFrame = new JInternalFrame(DustUtilsJava.toString(eEntity), true, true, false, false);			
			iFrame.getContentPane().add(pnl, BorderLayout.CENTER);
			iFrame.pack();
			iFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

			add(iFrame, JDesktopPane.DEFAULT_LAYER);
			iFrame.setVisible(true);
			
			iFrame.setLocation(posOffset, posOffset);
			
			posOffset += 30;
			
			if ( (posOffset > getWidth()) || (posOffset > getHeight()) ) {
				posOffset = 0;
			}
			
			links.followContent(iFrame);
		}
		
		void setSelected() {
			iFrame.setVisible(true);
			try {
				iFrame.setSelected(true);
			} catch (PropertyVetoException e) {
			}
		}

		public void updateTitle() {
			iFrame.setTitle(DustUtilsJava.toString(eEntity));
		}
	}
	
	DustUtilsFactory<DustEntity, EntityDocWindow> factDocWindows = new DustUtilsFactory<DustEntity, EntityDocWindow>(
			false) {
		@Override
		protected EntityDocWindow create(DustEntity key, Object... hints) {
			return new EntityDocWindow(key);
		}
	};

	DustGuiSwingMontruActionControl mac = new DustGuiSwingMontruActionControl() {
		@Override
		protected void activateEntities(DustEntity... entities) {
			for ( DustEntity de : entities ) {
				activateEditorPanel(de);
			}
		}
	};
	
	MouseListener ml = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			links.hitTest(e);
		}
	};

	int posOffset = 0;
	
	DustGuiSwingMontruLinks links;
	DustGuiSwingMontruControl control;

	public ArrayList<DustEntity> arrTypes = new ArrayList<>();

	public DustGuiSwingMontruDesktop() {
//		super(null);
		setOpaque(true);
		setBackground(Color.white);
		
		setBorder(BorderFactory.createEmptyBorder(ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER));

		links = new DustGuiSwingMontruLinks(this);
		
		add(links, JDesktopPane.POPUP_LAYER);
		
		addMouseListener(ml);
	}
	
	public void removeSelRefs() {
		links.removeSelRefs();
	}
	
	public void saveAll() {
		DustSandboxJsonLoader.init();

		Set<DustEntity> stores = new HashSet<>();
		DustEntity msg = Dust.getEntity(null);
		DustUtils.accessEntity(DataCommand.setRef, msg, DustDataLinks.MessageCommand, DustCommMessages.StoreSave);

		for (EntityDocWindow edw : factDocWindows.values()) {
			DustRef ref = DustUtils.accessEntity(DataCommand.getValue, edw.eEntity, DustCommLinks.TermStore);
			if (null != ref) {
				DustEntity store = ref.get(RefKey.target);

				if (stores.add(store)) {
					Dust.accessEntity(DataCommand.tempSend, store, msg, null, null);
				}
			}
		}
	}

	public void loadFiles(Object... cont) {
		DustSandboxListenerDump.init();
		DustSandboxJsonLoader.init();
		DustEntity msg = Dust.getEntity(null);
		DustUtils.accessEntity(DataCommand.setRef, msg, DustDataLinks.MessageCommand, DustCommMessages.StoreLoad);

		for (Object of : cont) {
			File f = (File) of;
			String fp = f.getAbsolutePath();
			
			DustEntity store = DustUtils.accessEntity(DataCommand.getEntity, null, null, "Store: " + fp, new EntityProcessor() {
				@Override
				public void processEntity(Object key, DustEntity entity) {
					DustUtils.accessEntity(DataCommand.setValue, entity, DustGenericAtts.streamFileName, fp);
					DustUtils.accessEntity(DataCommand.setRef, entity, DustDataLinks.EntityServices, DustCommServices.Store);
				}
			});

			DustUtils.accessEntity(DataCommand.tempSend, store, msg, null, null);
		}
	}
	
	public void activateEditorPanel(DustEntity e) {
		if ( null == e ) {
			return;
		}
		
		factDocWindows.get(e).setSelected();
	}

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		DustEntity eKey = DustUtils.getMsgVal(DustProcLinks.ChangeKey, true);
		Object key = EntityResolver.getKey(eKey);
		
		if ( DustGuiLinks.MontruDesktopActivePanel == key) {
			activateEditorPanel(DustUtils.getMsgVal(DustProcAtts.ChangeNewValue, true));
		} else if ( DustGenericAtts.identifiedIdLocal == key) {
			DustEntity eChg = DustUtils.getMsgVal(DustProcLinks.ChangeEntity, true);
			EntityDocWindow edw = factDocWindows.peek(eChg);
			if ( null != edw ) {
				edw.updateTitle();
			}
		} else if ( DustDataLinks.EntityPrimaryType == key) {
			DustEntity eType = DustUtils.getMsgVal(DustProcAtts.ChangeNewValue, true);
			if ( !arrTypes.contains(eType) ) {
				arrTypes.add(eType);
				if ( null != control ) {
					control.tmTypes.update();
				}				
			}
		}
		
		DustEntity cc = DustUtils.getMsgVal(DustProcLinks.ChangeCmd, true);
		key = EntityResolver.getKey(cc);
		
		if (-1 != DustUtilsJava.indexOf(key, DataCommand.setRef, DataCommand.removeRef)) {
			links.refreshLines();
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
