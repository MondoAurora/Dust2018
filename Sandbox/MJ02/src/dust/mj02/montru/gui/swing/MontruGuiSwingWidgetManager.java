package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;

import dust.mj02.dust.Dust;
import dust.mj02.montru.gui.MontruGuiWidgetManager;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsJava;

@SuppressWarnings({ "serial", "unchecked" })
public class MontruGuiSwingWidgetManager extends MontruGuiWidgetManager<JComponent>
		implements MontruGuiSwingComponents {

	class EntityDataAnchor extends JLabel implements GuiEntityDataElement {
		private final GuiEntityInfo eiEntity;
		private final GuiEntityInfo eiData;

		public EntityDataAnchor(GuiEntityInfo eiEntity, GuiEntityInfo eiData) {
			super(new ImageIcon("images/btn_blue-t.png"));

			if (null == eiData) {
				addMouseMotionListener(mmlDragSource);
				addMouseListener(mlDragSource);
				addMouseListener(mlDragTarget);
				
				eiData = editorModel.getEntityInfo(EntityResolver.getEntity(DustDataLinks.EntityModels));
			} else {
				addMouseListener(mlDragTarget);
			}

			this.eiEntity = eiEntity;
			this.eiData = eiData;
		}

		public GuiEntityInfo getEntityInfo() {
			return eiEntity;
		}

		public GuiEntityInfo getDataInfo() {
			return eiData;
		}

		@Override
		public void guiChangedAttribute(GuiEntityInfo entity, GuiEntityInfo att, Object value) {
			// Anchors do not change
		}

		@Override
		public void guiChangedRef(GuiEntityInfo entity, GuiRefInfo ref, DataCommand cmd) {
			// Anchors do not change
		}
	}

	class AnchoredPanel extends JPanel {
		final GuiEntityElement element;

		EnumMap<AnchorLocation, EntityDataAnchor> anchors = new EnumMap<>(AnchorLocation.class);

		public AnchoredPanel(JComponent comp, GuiEntityElement center) {
			super(new BorderLayout(10, 10));
			this.element = center;

			GuiEntityInfo eiEntity = center.getEntityInfo();
			GuiEntityInfo eiData = (center instanceof GuiEntityDataElement)
					? ((GuiEntityDataElement) center).getDataInfo()
					: null;

			add(comp, BorderLayout.CENTER);
			
			addAnchor(AnchorLocation.Left, eiEntity, eiData);
			addAnchor(AnchorLocation.Right, eiEntity, eiData);
		}

		private void addAnchor(AnchorLocation al, GuiEntityInfo eiEntity, GuiEntityInfo eiData) {
			EntityDataAnchor a = new EntityDataAnchor(eiEntity, eiData);
			add(a, al.getSwingConst());
			anchors.put(al, a);
		}

		public GuiEntityElement getElement() {
			return element;
		}

		public void getAnchorCentersOnScreen(EnumMap<AnchorLocation, Point> target) {
			for ( Map.Entry<AnchorLocation, EntityDataAnchor> ea : anchors.entrySet() ) {
				JComponent comp = ea.getValue();

				Point ret = comp.getLocationOnScreen();
				Dimension d = comp.getSize();
				ret.translate(d.width / 2, d.height / 2);	
				target.put(ea.getKey(), ret);
			}
		}
	}

	class EntityHeader extends JLabel implements GuiEntityDataElement {
		GuiEntityInfo ei;

		public EntityHeader(GuiEntityInfo ei) {
			super(ei.getTitle(), JLabel.CENTER);
			this.ei = ei;
			setBackground(Color.lightGray);
			setOpaque(true);
		}

		@Override
		public GuiEntityInfo getEntityInfo() {
			return ei;
		}
		
		@Override
		public GuiEntityInfo getDataInfo() {
			return null;
		}

		@Override
		public void guiChangedAttribute(GuiEntityInfo entity, GuiEntityInfo att, Object value) {
			setText(ei.getTitle());
		}

		@Override
		public void guiChangedRef(GuiEntityInfo entity, GuiRefInfo ref, DataCommand cmd) {

		}
	}

	class EntityDataLabel extends JLabel implements GuiEntityDataElement {
		private final GuiEntityInfo eiEntity;
		private final GuiEntityInfo eiData;

		public EntityDataLabel(GuiEntityInfo eiEntity, GuiEntityInfo eiData) {
			super();

			this.eiEntity = eiEntity;
			this.eiData = eiData;

			guiChangedAttribute(eiEntity, eiData, null);

			addMouseListener(mlLabelActivator);
		}

		public GuiEntityInfo getEntityInfo() {
			return eiEntity;
		}

		public GuiEntityInfo getDataInfo() {
			return eiData;
		}

		@Override
		public void guiChangedAttribute(GuiEntityInfo entity, GuiEntityInfo att, Object value) {
			Object val = Dust.accessEntity(DataCommand.getValue, entity.get(GuiEntityKey.entity),
					att.get(GuiEntityKey.entity), null, null);
			
			setText(DustUtilsJava.toString(val));
		}

		@Override
		public void guiChangedRef(GuiEntityInfo entity, GuiRefInfo ref, DataCommand cmd) {
			setText(DustUtilsJava.toString(ref));
		}
	}

	class EntityDataEditorText extends JTextField implements GuiEntityDataElement {
		private final GuiEntityInfo eiEntity;
		private final GuiEntityInfo eiData;

		public EntityDataEditorText(GuiEntityInfo eiEntity_, GuiEntityInfo eiData_) {
			super();

			this.eiEntity = eiEntity_;
			this.eiData = eiData_;

			guiChangedAttribute(eiEntity, eiData, null);
			
			txtListener.listen(this);
		}

		public GuiEntityInfo getEntityInfo() {
			return eiEntity;
		}

		public GuiEntityInfo getDataInfo() {
			return eiData;
		}

		@Override
		public void guiChangedAttribute(GuiEntityInfo entity, GuiEntityInfo att, Object value) {
			Object val = Dust.accessEntity(DataCommand.getValue, entity.get(GuiEntityKey.entity),
					att.get(GuiEntityKey.entity), null, null);
			setText(DustUtilsJava.toString(val));
		}

		@Override
		public void guiChangedRef(GuiEntityInfo entity, GuiRefInfo ref, DataCommand cmd) {
			// TODO Auto-generated method stub

		}

		void sendUpdate() {
			String val = getText();
			Dust.accessEntity(DataCommand.setValue, eiEntity.get(GuiEntityKey.entity), eiData.get(GuiEntityKey.entity),
					val, null);
			sendUpdateAtt(this, eiEntity, eiData, val);
		}
	}

	private final DustSwingTextListener txtListener = new DustSwingTextListener(new DustSwingTextChangeProcessor() {
		@Override
		public void textChanged(String text, Object source, DocumentEvent e) {
			((EntityDataEditorText) source).sendUpdate();
		}
	});

	private final MouseListener mlLabelActivator = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (1 < e.getClickCount()) {
				editorPanel.activateEntity(((GuiEntityElement) e.getSource()).getEntityInfo(), true);
			}
		}
	};

	private final MouseMotionListener mmlDragSource = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (null == dragEntityInfo) {
				dragEntityInfo = ((GuiEntityElement) e.getSource()).getEntityInfo();
				dragIsType = editorModel.getAllTypes().contains(dragEntityInfo);
				DustUtilsDev.dump("dragEntityInfo", e.getPoint(), dragIsType, dragEntityInfo);
			}
		}
	};

	private final MouseListener mlDragSource = new MouseAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (null == dragEntityInfo) {
				dragEntityInfo = ((GuiEntityElement) e.getSource()).getEntityInfo();
				dragIsType = editorModel.getAllTypes().contains(dragEntityInfo);
				DustUtilsDev.dump("dragEntityInfo", e.getPoint(), dragIsType, dragEntityInfo);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			MontruGuiSwingPanelEntity pnlTarget = editorPanel.hitTestPanel(e);

			if (null == pnlTarget) {
				if (dragIsType) {
					String id = JOptionPane.showInputDialog("Entity id?");

					if (!DustUtilsJava.isEmpty(id)) {
						DustEntity de = Dust.getEntity(id);
						Dust.accessEntity(DataCommand.setValue, de,
								EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal), id, null);
						Dust.accessEntity(DataCommand.setRef, de,
								EntityResolver.getEntity(DustDataLinks.EntityPrimaryType),
								dragEntityInfo.get(GuiEntityKey.entity), null);
						GuiEntityInfo ei = editorModel.getEntityInfo(de);

						// ei.put(GuiEntityKey.id, id);
						ei.put(GuiEntityKey.type, dragEntityInfo);
						ei.add(GuiEntityKey.models, dragEntityInfo);
						ei.getTitle();

						JInternalFrame jif = editorPanel.activateEntity(ei, true);
						jif.setVisible(true);
						Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), editorPanel.pnlDesktop);
						jif.setLocation(pt);
					}
				}
			} else if (null != dragTarget) {
				GuiEntityElement src = dragTarget;
				GuiEntityInfo eiEntity = src.getEntityInfo();

				DustEntity eLink = (src instanceof GuiEntityDataElement)
						? ((GuiEntityDataElement) src).getDataInfo().get(GuiEntityKey.entity)
						: EntityResolver.getEntity(DustDataLinks.EntityModels);

				DustRef dr = Dust.accessEntity(DataCommand.setRef, eiEntity.get(GuiEntityKey.entity), eLink,
						dragEntityInfo.get(GuiEntityKey.entity), null);

				GuiRefInfo ri = editorModel.getRefInfo(dr);
				ri.put(GuiRefKey.selected, true);
				eiEntity.add(GuiEntityKey.models, dragEntityInfo);
				editorPanel.pnlDesktop.updatePanels(eiEntity, dragEntityInfo);
			}

			dragEntityInfo = null;

		}
	};

	private final MouseListener mlDragTarget = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			if (null != dragEntityInfo) {
				dragTarget = (GuiEntityElement) e.getSource();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (null != dragEntityInfo) {
				if (dragTarget == e.getSource()) {
					dragTarget = null;
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
		};
	};

	private final MontruGuiSwingPanelEditor editorPanel;

	public MontruGuiSwingWidgetManager(MontruGuiSwingPanelEditor editorPanel) {
		super(editorPanel.getEditorModel());
		this.editorPanel = editorPanel;
	}

	@Override
	protected <WT> WT createWidgetInt(WidgetType wt, GuiEntityInfo eEntity, GuiEntityInfo eData) {
		switch (wt) {
		case dataEditor:
			return (WT) new EntityDataEditorText(eEntity, eData);
		case dataLabel:
			return (WT) new EntityDataLabel(eEntity, eData);
		case entityHead:
			return (WT) new EntityHeader(eEntity);
		case entityPanel:
			break;
		default:
			break;
		}
		return null;
	}

	public AnchoredPanel anchorPanel(JComponent comp, GuiEntityElement element) {
		return new AnchoredPanel(comp, element);
	}

	void sendUpdateAtt(Object updateSource, GuiEntityInfo eEntity, GuiEntityInfo eData, Object value) {
		try {
			this.updateSource = updateSource;
			guiChangedAttribute(eEntity, eData, value);
		} finally {
			this.updateSource = null;
		}
	}

	@Override
	protected void guiChangedAttributeInt(boolean panel, GuiEntityElement e, GuiEntityInfo entity, GuiEntityInfo att,
			Object value) {
		if (updateSource != e) {
			super.guiChangedAttributeInt(panel, e, entity, att, value);
		}
	}

}
