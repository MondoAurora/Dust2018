package dust.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public interface DustUtilsSwingComponents extends DustUtilsComponents {

	interface DustSwingTextChangeProcessor {
		void textChanged(String text, Object source, DocumentEvent e);
	}

	class DustSwingTextListener {
		private static final String DOC_EDIT_PROP = "DustDocEditComp";

		private final DustSwingTextChangeProcessor chgProc;

		private final DocumentListener dl = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				reportEvent(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				reportEvent(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				reportEvent(e);
			}
		};

		public DustSwingTextListener(DustSwingTextChangeProcessor chgProc) {
			super();
			this.chgProc = chgProc;
		}
		
		public DustSwingTextListener(DustSwingTextChangeProcessor chgProc, JTextComponent tc) {
			this(chgProc);
			listen(tc);
		}

		public void listen(JTextComponent tc) {
			Document doc = tc.getDocument();
			doc.addDocumentListener(dl);
			doc.putProperty(DOC_EDIT_PROP, tc);
		}

		private void reportEvent(DocumentEvent e) {
			Document doc = e.getDocument();
			Object src = doc.getProperty(DOC_EDIT_PROP);
			chgProc.textChanged(((JTextComponent)src).getText(), src, e);
		}
	};
}
