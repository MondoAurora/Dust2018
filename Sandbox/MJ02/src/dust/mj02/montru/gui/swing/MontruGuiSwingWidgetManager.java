package dust.mj02.montru.gui.swing;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import dust.mj02.dust.Dust;
import dust.mj02.montru.gui.MontruGuiWidgetManager;
import dust.utils.DustUtilsJava;

@SuppressWarnings({"serial", "unchecked"})
public class MontruGuiSwingWidgetManager extends MontruGuiWidgetManager<JComponent>
		implements MontruGuiSwingComponents {
	
	private static final String DOC_EDIT_PROP = "MontruDocEditComp";

	class EntityDataLabel extends JLabel implements GuiEntityDataElement {
		private final GuiEntityInfo eiEntity;
		private final GuiEntityInfo eiData;

		public EntityDataLabel(GuiEntityInfo eiEntity, GuiEntityInfo eiData) {
			super();

			this.eiEntity = eiEntity;
			this.eiData = eiData;
			
			guiChangedAttribute(eiEntity, eiData, null);
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
	}

	class EntityDataEditorText extends JTextField implements GuiEntityDataElement {
		private final GuiEntityInfo eiEntity;
		private final GuiEntityInfo eiData;

		public EntityDataEditorText(GuiEntityInfo eiEntity_, GuiEntityInfo eiData_) {
			super();

			this.eiEntity = eiEntity_;
			this.eiData = eiData_;

			guiChangedAttribute(eiEntity, eiData, null);
			Document doc = getDocument();
			doc.addDocumentListener(dl);
			doc.putProperty(DOC_EDIT_PROP, this);
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
			Dust.accessEntity(DataCommand.setValue, eiEntity.get(GuiEntityKey.entity), eiData.get(GuiEntityKey.entity), val, null);
			sendUpdateAtt(this, eiEntity, eiData, val);
		}
	}

	Object updateSource;

	DocumentListener dl = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			((EntityDataEditorText)e.getDocument().getProperty(DOC_EDIT_PROP)).sendUpdate();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			((EntityDataEditorText)e.getDocument().getProperty(DOC_EDIT_PROP)).sendUpdate();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			((EntityDataEditorText)e.getDocument().getProperty(DOC_EDIT_PROP)).sendUpdate();
		}
	};
	
	public MontruGuiSwingWidgetManager(GuiEditorModel editorModel) {
		super(editorModel);
	}


	@Override
	protected <WT> WT createWidgetInt(WidgetType wt, GuiEntityInfo eEntity, GuiEntityInfo eData) {
		switch (wt) {
		case dataEditor:
			return (WT) new EntityDataEditorText(eEntity, eData);
		case dataLabel:
			return (WT) new EntityDataLabel(eEntity, eData);
		case entityHead:
			break;
		case entityPanel:
			break;
		default:
			break;
		}
		return null;
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
