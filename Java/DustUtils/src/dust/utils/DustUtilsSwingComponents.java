package dust.utils;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.JButton;
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
            chgProc.textChanged(((JTextComponent) src).getText(), src, e);
        }
    };

    static abstract class DustSwingCommandManager<CmdType extends Enum<CmdType>> {
        Class<CmdType> cmdClass;

        ActionListener alCmd = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exec(Enum.valueOf(cmdClass, e.getActionCommand()));
            }
        };

        Set<AbstractButton> btns = new HashSet<>();

        public DustSwingCommandManager(Class<CmdType> cc) {
            cmdClass = cc;
        }

        private CmdType cmdOf(String action) {
            return Enum.valueOf(cmdClass, action);
        }

        public final AbstractButton createButton(CmdType cmd) {
            return initButton(cmd, null);
        }

        public final AbstractButton initButton(CmdType cmd, AbstractButton btn) {
            if (null == btn) {
                btn = new JButton();
            }
            btn.setText(cmd.name());
            btn.addActionListener(alCmd);
            btn.setActionCommand(cmd.name());

            btns.add(btn);
            
            initButton(btn, cmd);

            return btn;
        }
        
        public final void loadAll(Container c) {
            for ( CmdType cmd : cmdClass.getEnumConstants() ) {
                c.add(createButton(cmd));
            }
        }

        @SuppressWarnings("unchecked")
        public final void setEnabled(boolean enabled, CmdType... cmds) {
            EnumSet<CmdType> c = EnumSet.noneOf(cmdClass);
            for (CmdType ct : cmds) {
                c.add(ct);
            }
            for (AbstractButton btn : btns) {
                CmdType cmd = cmdOf(btn.getActionCommand());
                
                if (c.contains(cmd)) {
                    if ( btn.isEnabled() != enabled ) {
                        btn.setEnabled(enabled);
                        stateChanged(btn, cmd, enabled);
                    }
                }
            }
        }
        
        public void updateStates() {
            
        }
        
        protected void initButton(AbstractButton btn, CmdType cmd) {
            
        }

        protected void stateChanged(AbstractButton btn, CmdType cmd, boolean enabled) {
            
        }

        protected void processException(CmdType cmd, Exception ex) {
            ex.printStackTrace();
        }

        protected abstract void execute(CmdType cmd) throws Exception;

        public void exec(CmdType cmd) {
            try {
                execute(cmd);
            } catch (Exception e1) {
                processException(cmd, e1);
            }
            updateStates();
        }
    }
}
