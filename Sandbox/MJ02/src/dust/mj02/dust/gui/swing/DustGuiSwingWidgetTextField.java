package dust.mj02.dust.gui.swing;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsDev;
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
//					    String orig = null;
						try {
							changing = (DustGuiSwingWidgetTextField) source;
//                            orig = Dust.accessEntity(DataCommand.getValue, changing.eEntity, changing.eData, null, null);
                            Dust.accessEntity(DataCommand.setValue, changing.eEntity, changing.eData, text, null);
							changing.requestFocus();
//                        } catch (Throwable t) {
//                            changing.setTextDirect(orig);
                        } finally {
							changing = null;
						}
					}
				}
			});

	DustEntity eEntity;
	DustEntity eData;

	public DustGuiSwingWidgetTextField() {
		DOC_LISTENER.listen(this);
	}

	protected void setTextDirect(String t) {
	    super.setText(t);
    }

    @Override
	public void setText(String t) {
		if (this != changing) {
			DustUtilsDev.dump("updating TextField to", t);
			super.setText(t);
		}
	}

	@Override
	public void dustProcListenerProcessChange() throws Exception {
		Object val = DustUtils.getMsgVal(DustProcAtts.ChangeNewValue, false);
		setText(DustUtilsJava.toString(val));
	}

	@Override
	public void activeInit() throws Exception {
		DustRef e = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeEntity);
		DustRef a = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustProcLinks.ChangeKey);

		eEntity = e.get(RefKey.target);
		eData = a.get(RefKey.target);
		String val = DustUtilsJava.toString(DustUtils.accessEntity(DataCommand.getValue, eEntity, eData));

		setText(val);
		
		DustUtils.accessEntity(DataCommand.setRef, ContextRef.session, DustProcLinks.SessionChangeListeners, ContextRef.self);
	}

	@Override
	public void activeRelease() throws Exception {

	}
}
