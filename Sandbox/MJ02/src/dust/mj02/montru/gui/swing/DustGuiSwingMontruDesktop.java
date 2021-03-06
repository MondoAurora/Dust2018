package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.gui.swing.DustGuiSwingEntityActionControl;
import dust.mj02.dust.gui.swing.DustGuiSwingPanelEntity;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.sandbox.DustSandbox;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;
import dust.utils.DustUtilsMuteManager;
import dust.utils.DustUtilsMuteManager.MutableModule;

public class DustGuiSwingMontruDesktop extends JDesktopPane implements DustGuiSwingMontruComponents,
		DustProcComponents.DustProcListener, DustProcComponents.DustProcAgent, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;

	class EntityDocWindow {
		DustEntity eEntity;

		DustEntity ePanel;
		JInternalFrame iFrame;
		DustGuiSwingPanelEntity pnl;

		public EntityDocWindow(DustEntity eEntity) {
			this.eEntity = eEntity;

			ePanel = DustUtils.accessEntity(DataCommand.getEntity, DustGuiTypes.PropertyPanel, ContextRef.self, null,
					new EntityProcessor() {
						@Override
						public void processEntity(DustEntity entity) {
							DustUtils.accessEntity(DataCommand.setRef, entity, DustGuiLinks.PropertyPanelEntity,
									eEntity);
						}
					});

			pnl = DustUtils.getBinary(ePanel, DustGuiServices.PropertyPanel);
			pnl.setEntityActionControl(eac);

			iFrame = new JInternalFrame(DustUtilsJava.toString(eEntity), true, true, false, true);
			iFrame.getContentPane().add(pnl, BorderLayout.CENTER);
			iFrame.pack();
			iFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

			add(iFrame, JDesktopPane.DEFAULT_LAYER);
			iFrame.setVisible(true);

			iFrame.setLocation(posOffset, posOffset);

			posOffset += 30;

			if ((posOffset > getWidth()) || (posOffset > getHeight())) {
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
			
			links.refreshLines();
		}

		public void updateTitle() {
			iFrame.setTitle(DustUtilsJava.toString(eEntity));
		}

		public void setPos(int xScreen, int yScreen) {
            Point pt = SwingUtilities.convertPoint(null, xScreen, yScreen, null);
            SwingUtilities.convertPointFromScreen(pt, DustGuiSwingMontruDesktop.this);
			iFrame.setLocation(pt);
		}
	}

	Point dragStart;
	Point dragEnd;
	JPanel dragLayer = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			if (null != dragStart) {
				Color col = g.getColor();

				g.setColor(COL_DRAGLINE);
				g.drawLine(dragStart.x, dragStart.y, dragEnd.x, dragEnd.y);
				g.setColor(col);
				
//				DustUtilsDev.dump("should draw line", dragLine);
			}
		}
	};

	DustUtilsFactory<DustEntity, EntityDocWindow> factDocWindows = new DustUtilsFactory<DustEntity, EntityDocWindow>(
			false) {
		@Override
		protected EntityDocWindow create(DustEntity key, Object... hints) {
			return new EntityDocWindow(key);
		}
	};

	DustGuiSwingEntityActionControl eac = new DustGuiSwingEntityActionControl() {
		DustEntity eCreated;
		
		@Override
        public void activateEntities(DustEntity... entities) {
            try {
                DustUtilsMuteManager.mute(MutableModule.GUI, true);
                DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, true);

                for (DustEntity de : entities) {
                    activateEditorPanel(de);
                }
            } finally {
                DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, false);
                DustUtilsMuteManager.mute(MutableModule.GUI, false);
            }
        }

		@Override
		protected void dragItemChanged(DragItem item, GuiDataWrapper<JComponent> gdwOld,
				GuiDataWrapper<JComponent> gdwNew) {
			switch (item) {
			case source:
				DustGuiSwingMontruDesktop.this.setCursor(
						Cursor.getPredefinedCursor((null == gdwNew) ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
				break;
			case target:
				break;
			}
		}

		@Override
		protected void handleDragEvent(MouseEvent me) {
			if ( null == me ) {
				dragLayer.setVisible(false);
				dragStart = dragEnd = null;
			} else {
				if ( null == dragStart ) {
					dragStart = dragEnd = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), dragLayer);
					dragLayer.setVisible(true);
				} else {
					dragEnd = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), dragLayer);
				}
				
				dragLayer.repaint();
			}
		}
		
		@Override
		protected void dropped(EnumSet<CtrlStatus> ctrlStatus, GuiDataWrapper<JComponent> gdwSource,
				GuiDataWrapper<JComponent> gdwTarget, int xScreen, int yScreen) {
			DustEntity ePt;
			
			if (null == gdwTarget) {
				DustEntity eTypeType = EntityResolver.getEntity(DustMetaTypes.Type);
				DustEntity eDropped = gdwSource.getEntity();
				eCreated = null;
				
				if (ctrlStatus.contains(CtrlStatus.ctrl)) {
					eCreated = DustUtils.accessEntity(DataCommand.cloneEntity, eDropped);
				} else {
					ePt = DustUtils.toEntity(DustUtils.accessEntity(DataCommand.getValue, eDropped, DustDataLinks.EntityPrimaryType));
					
					if ( eTypeType == ePt) {
						eCreated = DustUtils.accessEntity(DataCommand.getEntity, eDropped);
					} else {
						DustEntity eData = gdwSource.getData();
						ePt = DustUtils.toEntity(DustUtils.accessEntity(DataCommand.getValue, eData, DustDataLinks.EntityPrimaryType));
						
						if ( eTypeType == ePt) {
							eCreated = DustUtils.accessEntity(DataCommand.getEntity, eData);
							DustUtils.accessEntity(DataCommand.processRef, eData, DustMetaLinks.TypeAttDefs, new RefProcessor() {
								@Override
								public void processRef(DustRef ref) {
									DustEntity eAtt = ref.get(RefKey.target);
									Object val = DustUtils.accessEntity(DataCommand.getValue, eDropped, eAtt);
									DustUtils.accessEntity(DataCommand.setValue, eCreated, eAtt, val);
								}
							});
							DustUtils.accessEntity(DataCommand.processRef, eData, DustMetaLinks.TypeLinkDefs, new RefProcessor() {
								@Override
								public void processRef(DustRef ref) {
									DustEntity eLink = ref.get(RefKey.target);
									DustUtils.accessEntity(DataCommand.processRef, eDropped, eLink, new RefProcessor() {
										@Override
										public void processRef(DustRef ref1) {
											DustUtils.accessEntity(DataCommand.setRef, eCreated, eLink, ref1.get(RefKey.target));
										}
									});
								}
							});
							DustUtils.accessEntity(DataCommand.removeRef, eDropped, DustDataLinks.EntityModels, eData);
						} else {
							JOptionPane.showMessageDialog(DustGuiSwingMontruDesktop.this, "Create works for Type entities only, keep Ctrl pressed to clone");
						}
					}
				}
				
				if ( null != eCreated ) {
					EntityDocWindow edw = activateEditorPanel(eCreated);
					edw.setPos(xScreen, yScreen);
					
					ePt = DustUtils.toEntity(DustUtils.accessEntity(DataCommand.getValue, eCreated, DustDataLinks.EntityPrimaryType));
					if ( eTypeType == ePt ) {
						if ( eac.types(CollectionAction.add, eCreated)) {
							control.tmTypes.update();
						}
					}
				}
			} else {
				links.refreshLines();
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
	private DustGuiSwingMontruControl control;

	public DustGuiSwingMontruDesktop() {
		setOpaque(true);
		setBackground(Color.white);

		setBorder(BorderFactory.createEmptyBorder(ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER, ENTITY_PANEL_BORDER,
				ENTITY_PANEL_BORDER));

		links = new DustGuiSwingMontruLinks(this);

		add(links, JDesktopPane.POPUP_LAYER);
		
		dragLayer.setOpaque(false);
		dragLayer.setVisible(false);
		add(dragLayer, JDesktopPane.DRAG_LAYER);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				dragLayer.setSize(e.getComponent().getSize());
				dragLayer.repaint();
			}
		});
		dragLayer.setSize(getSize());

		setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = 1L;

            @Override
    		public boolean canImport(TransferSupport support) {
                DataFlavor[] dataFlavors = support.getDataFlavors();
                boolean ok = -1 != DustUtilsJava.indexInArr(DataFlavor.javaFileListFlavor, dataFlavors);
                
                return ok;
    		}
            
            @Override
            public boolean importData(TransferSupport support) {
                try {
                    Object o = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    
                    for ( Object of : (Collection<?>)o ) {
                        File f = (File) of;
                        String id = f.toURI().toURL().toString();
                        
                        DustEntity e = DustUtils.accessEntity(DataCommand.getEntity, DustGenericTypes.Stream, null, id, new EntityProcessor() {
                            @Override
                            public void processEntity(DustEntity entity) {
                                DustUtils.accessEntity(DataCommand.setValue, entity, DustGenericAtts.IdentifiedIdLocal, id);
                                DustUtils.accessEntity(DataCommand.setValue, entity, DustGenericAtts.StreamFileName, f.getAbsolutePath());
                            }
                        });
                        
                        activateEditorPanel(e);                        
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
		}); 

		addMouseListener(ml);
	}

	public void removeSelRefs() {
		links.removeSelRefs();
	}

	public void refreshData() {
       DustSandbox.init();

		DustEntity tt = EntityResolver.getEntity(DustMetaTypes.Type);
		Set<DustEntity> toDel = new HashSet<>(eac.getAllTypes());
//		eac.types(CollectionAction.clear, null);
		
		Dust.processEntities(new EntityProcessor() {			
			@Override
			public void processEntity(DustEntity entity) {
				DustEntity pt = DustUtils.toEntity(DustUtils.accessEntity(DataCommand.getValue, entity, DustDataLinks.EntityPrimaryType));
				boolean add = tt == pt;
				if ( !add ) {
					DustRef rm = DustUtils.accessEntity(DataCommand.getValue, entity, DustDataLinks.EntityModels);
					add = (null != rm) && rm.contains(tt);
				}
				if ( add ) {
					eac.types(CollectionAction.add, entity);
					toDel.remove(entity);
//					if (eac.addType(entity)) {
//						newTypes.add(entity);
//					}
				}
			}
		});
		
		for ( DustEntity ed : toDel ) {
			eac.types(CollectionAction.remove, ed);
		}
		
//		if ( !newTypes.isEmpty() ) {
			control.tmTypes.update();
//		}
			links.refreshLines();
	}

	public EntityDocWindow activateEditorPanel(DustEntity e) {
		if (null == e) {
			return null;
		}

		EntityDocWindow edw = factDocWindows.get(e);
		edw.setSelected();
		return edw;
	}
	
	@Override
	public void dustProcAgentProcessStatement() throws Exception {
	    links.refreshLines();
	}

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		Object key;
		
		DustEntity eKey = DustUtils.getMsgVal(DustCommLinks.ChangeItemKey, true);
		key = EntityResolver.getKey(eKey);

		if (MontruGuiLinks.MontruDesktopActivePanel == key) {
			activateEditorPanel(DustUtils.getMsgVal(DustCommAtts.ChangeItemNewValue, true));
		} else if (DustGenericAtts.IdentifiedIdLocal == key) {
			DustEntity eChg = DustUtils.getMsgVal(DustCommLinks.ChangeItemEntity, true);
			DustEntity ePt = DustUtils.toEntity(DustUtils.accessEntity(DataCommand.getValue, eChg, DustDataLinks.EntityPrimaryType));
			if ( DustMetaTypes.Type == EntityResolver.getKey(ePt)) {
				for ( EntityDocWindow edw : factDocWindows.values() ) {
					edw.updateTitle();
				}
                if (null != control) {
                    control.tmTypes.update();
                }
			} else {
				EntityDocWindow edw = factDocWindows.peek(eChg);
				if (null != edw) {
					edw.updateTitle();
				}
			}
		} else if (DustDataLinks.EntityPrimaryType == key) {
			DustEntity eType = DustUtils.getMsgVal(DustCommAtts.ChangeItemNewValue, true);
			if ( eac.types(CollectionAction.add, eType)) {
				if (null != control) {
					control.tmTypes.update();
				}
			}
		}
		
//		DustEntity cc = DustUtils.getMsgVal(DustCommLinks.ChangeItemCmd, true);
//		key = EntityResolver.getKey(cc);
//
//		if (-1 != DustUtilsJava.indexOf(key, DataCommand.setRef, DataCommand.removeRef)) {
//			links.refreshLines();
//			return;
//		}
	}

	@Override
	public void activeInit() throws Exception {
        DustUtils.accessEntity(DataCommand.setRef, ContextRef.session, DustProcLinks.SessionChangeListeners, ContextRef.self);
        DustUtils.accessEntity(DataCommand.setRef, ContextRef.session, DustProcLinks.SessionChangeAgents, ContextRef.self);
	}

	@Override
	public void activeRelease() throws Exception {
	}

	public DustGuiSwingEntityActionControl getEac() {
		return eac;
	}

	public void setControl(DustGuiSwingMontruControl control) {
		this.control = control;
	}

    public void closeUnselected() {
        Collection<DustEntity> sel = eac.getAllSelected();
        Set<DustEntity> toClose = new HashSet<>();
        
        for ( DustEntity e : factDocWindows.keys() ) {
            if ( !sel.contains(e) ) {
                toClose.add(e);
            }
        }
        
        if ( toClose.isEmpty() ) {
            return;
        }
        
        for ( DustEntity ee : toClose ) {
            EntityDocWindow dw = factDocWindows.peek(ee);
            dw.iFrame.dispose();
            factDocWindows.drop(dw);
        }
        
        posOffset = 0;
        refreshData();
        links.refreshLines();
    }

    public void deleteSelected() {
        if ( eac.getAllSelected().isEmpty() ) {
            return;
        }
        
        for ( Object o : eac.getAllSelected().toArray() ) {
            DustEntity ee = (DustEntity)o;
            DustUtils.accessEntity(DataCommand.dropEntity, ee);
            eac.select(CollectionAction.clear, null);
            EntityDocWindow dw = factDocWindows.peek(ee);
            dw.iFrame.dispose();
            factDocWindows.drop(dw);
        }
        
        refreshData();
        links.refreshLines();
    }
}
