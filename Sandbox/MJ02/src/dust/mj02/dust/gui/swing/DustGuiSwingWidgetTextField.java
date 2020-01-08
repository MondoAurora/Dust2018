package dust.mj02.dust.gui.swing;

import javax.swing.JFormattedTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.DefaultFormatter;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsDev;

public class DustGuiSwingWidgetTextField extends JFormattedTextField
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
							DustUtils.AttConverter.setAttFromString(changing.eEntity, changing.eData, text);
//                            Dust.accessEntity(DataCommand.setValue, changing.eEntity, changing.eData, text, null);
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
	    setText(DustUtils.AttConverter.getAttAsString(eEntity, eData));
//		Object val = DustUtils.getMsgVal(DustCommAtts.ChangeItemNewValue, false);
//		setText(DustUtilsJava.toString(val));
	}

	@Override
	public void activeInit() throws Exception {
		DustRef e = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustCommLinks.ChangeItemEntity);
		DustRef a = DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustCommLinks.ChangeItemKey);

		eEntity = e.get(RefKey.target);
		eData = a.get(RefKey.target);
		
		Class<?> valClass = DustUtils.AttConverter.getAttClass(eData);
		
        if (String.class != valClass) {
            DefaultFormatter fmt = new DefaultFormatter();
            fmt.setValueClass(valClass);
            fmt.setAllowsInvalid(false);
            setFormatter(fmt);
        }
		
//		String val = DustUtilsJava.toString(DustUtils.accessEntity(DataCommand.getValue, eEntity, eData));
//		setText(val);
		setText(DustUtils.AttConverter.getAttAsString(eEntity, eData));

		DustUtils.accessEntity(DataCommand.setRef, ContextRef.session, DustProcLinks.SessionChangeListeners, ContextRef.self);
	}

	@Override
	public void activeRelease() throws Exception {

	}
}
