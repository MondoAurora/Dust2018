package dust.mj02.dust.gui.swing;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsJava;

public class DustGuiSwingWidgetTextField extends JTextField
		implements DustGuiSwingComponents, DustProcComponents.DustProcListener, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;

	private static DustGuiSwingWidgetTextField changing;

	private static final DustSwingTextListener DOC_LISTENER = new DustSwingTextListener(
			new DustSwingTextChangeProcessor() {
				@Override
				public void textChanged(String text, Object source, DocumentEvent e) {
					if (null == changing) {
						try {
							changing = (DustGuiSwingWidgetTextField) source;
							Dust.accessEntity(DataCommand.setValue, changing.eEntity, changing.eData, text, null);
							changing.requestFocus();
						} finally {
							changing = null;
						}
					}
				}
			});

	public static DustGuiSwingWidgetTextField createWidget(DustEntity eEntity, DustEntity eData) {
		DustEntity eWidget = Dust.getEntity(null);
		DustEntity eSvc = EntityResolver.getEntity(DustGuiServices.TextField);

		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustProcLinks.ChangeEntity, eEntity);
		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustProcLinks.ChangeKey, eData);
		DustUtils.accessEntity(DataCommand.setRef, eWidget, DustDataLinks.EntityServices, eSvc);

		DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextChangeListeners, eWidget);

		return DustUtils.getBinary(eWidget, eSvc);
	}

	DustEntity eEntity;
	DustEntity eData;

	public DustGuiSwingWidgetTextField() {
		DOC_LISTENER.listen(this);
	}

	@Override
	public void setText(String t) {
		if (this != changing) {
			super.setText(t);
		}
	}

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		Object val = DustUtils.getMsgVal(DustProcAtts.ChangeNewValue, false);
		// DustUtilsDev.dump("updating label to", DustUtilsJava.toString(val));
		setText(DustUtilsJava.toString(val));
	}

	@Override
	public void dustProcActiveInit() throws Exception {
		DustRef e = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeEntity);
		DustRef a = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeKey);

		eEntity = e.get(RefKey.target);
		eData = a.get(RefKey.target);
		String val = DustUtilsJava.toString(DustUtils.accessEntity(DataCommand.getValue, eEntity, eData));

		setText(val);
	}

	@Override
	public void dustProcActiveRelease() throws Exception {

	}
}
