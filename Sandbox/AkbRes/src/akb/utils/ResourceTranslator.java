package akb.utils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dust.utils.DustUtilsJavaSwing;
import dust.utils.DustUtilsSwingComponents;

public class ResourceTranslator implements DustUtilsSwingComponents {
    enum Language {
        en, hu
    }

    // enum FileRole {
    // Reference, Source, Result
    // }

    enum Commands {
        LoadRef, LoadSrc, ShowValidated, Update, AcceptSrc, Invalidate, Save, Export
    }

    enum Columns {
        OK(Boolean.class), Key(String.class), Text(String.class);

        private final Class<?> cc;

        private Columns(Class<?> cc) {
            this.cc = cc;
        }

        public Class<?> getCc() {
            return cc;
        }
    }

    static class ResFile {
        Language lang;
        String fileName;

        ArrayList<String> lines = new ArrayList<>();

        Map<String, Integer> keyLines = new HashMap<>();
        Set<String> duplicates = new HashSet<>();

        public ResFile(Language lang, String fileName) {
            this.lang = lang;
            this.fileName = fileName;

            File f = new File(fileName);

            try {
                if (f.exists()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));

                    String st;
                    while ((st = br.readLine()) != null) {
                        String k = keyOfLine(st);
                        if (null != k) {
                            if (keyLines.containsKey(k)) {
                                duplicates.add(k);
                            } else {
                                keyLines.put(k, lines.size());
                            }
                        }

                        lines.add(st);
                    }
                    br.close();
                }
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        private String valueOfKey(String k) {
            Integer idx = keyLines.get(k);

            if (null != idx) {
                String st = lines.get(idx);

                int split = st.indexOf("=");
                return st.substring(split + 1);
            }

            return "";
        }

        private String keyOfLine(String st) {
            if (0 < st.length() && !st.startsWith("#")) {
                int split = st.indexOf("=");
                if (-1 != split) {
                    return st.substring(0, split);
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "ResFile " + lang + ", file: " + fileName + ", duplicates: " + duplicates + ", key count: " + keyLines.size();
        }
    }

    class TranslatorPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        JLabel lbCounts;
        JTextField tfFilter;
        JToggleButton tbShowValid;

        JTable tblData;

        JLabel lbKey;

        JTextArea taRef;
        JTextArea taSrc;
        JTextArea taValid;

        DustSwingCommandManager<Commands> cm = new DustSwingCommandManager<ResourceTranslator.Commands>(Commands.class) {
            @Override
            protected void execute(Commands cmd) throws Exception {
                switch (cmd) {
                case LoadRef:
                    taValid.setText(taRef.getText());
                    return;
                case LoadSrc:
                    taValid.setText(taSrc.getText());
                    return;
                case AcceptSrc:
                    for (int idx = 0; idx < alKeys.size(); ++idx) {
                        if (tblData.isRowSelected(idx)) {
                            String key = alKeys.get(idx);
                            text = src.valueOfKey(key);
                            validated.put(key, text);
                        }
                    }
                    break;
                case Update:
                    text = taValid.getText();
                    validated.put(selKey, text);
                    break;
                case Invalidate:
                    for (int idx = 0; idx < alKeys.size(); ++idx) {
                        if (tblData.isRowSelected(idx)) {
                            String key = alKeys.get(idx);
                            validated.remove(key);
                        }
                    }
                    break;
                case ShowValidated:
                    break;
                default:
                    ResourceTranslator.this.execute(cmd);
                    break;
                }

                updateDisplay();
            }

            public void updateStates() {
            };
        };

        DustSwingTextListener tl = new DustSwingTextListener(new DustSwingTextChangeProcessor() {
            @Override
            public void textChanged(String text, Object source, DocumentEvent e) {
                updateDisplay();
            }
        });

        class ResTableModel extends AbstractTableModel {
            private static final long serialVersionUID = 1L;

            @Override
            public int getColumnCount() {
                return Columns.values().length;
            }

            public String getColumnName(int column) {
                return Columns.values()[column].name();
            }

            public Class<?> getColumnClass(int columnIndex) {
                return Columns.values()[columnIndex].getCc();
            }

            @Override
            public int getRowCount() {
                return alKeys.size();
            }

            @Override
            public Object getValueAt(int arg0, int arg1) {
                String key = alKeys.get(arg0);
                switch (Columns.values()[arg1]) {
                case Key:
                    return key;
                case OK:
                    return validated.containsKey(key);
                case Text:
                    String ret = validated.get(key);
                    if (null == ret) {
                        ret = src.valueOfKey(key);
                    }
                    return ret;
                }
                return null;
            };

            void updated() {
                fireTableDataChanged();
            }
        };

        ResTableModel tmRes = new ResTableModel();

        public TranslatorPanel() {
            super(new BorderLayout());

            JPanel pnlLeft = new JPanel(new BorderLayout());
            JPanel pnlChild = new JPanel(new BorderLayout());

            tbShowValid = new JToggleButton();
            cm.initButton(Commands.ShowValidated, tbShowValid);

            pnlChild.add(tbShowValid, BorderLayout.WEST);

            tfFilter = new JTextField();
            tl.listen(tfFilter);
            pnlChild.add(tfFilter, BorderLayout.CENTER);

            lbCounts = new JLabel("            ");
            pnlChild.add(lbCounts, BorderLayout.EAST);

            pnlLeft.add(pnlChild, BorderLayout.NORTH);

            tblData = new JTable(tmRes);

            tblData.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tblData.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int idx = tblData.getSelectionModel().getLeadSelectionIndex();
                    setSelectedKey(((0 <= idx) && (idx < alKeys.size())) ? alKeys.get(idx) : null);
                }
            });

            pnlLeft.add(DustUtilsJavaSwing.setBorderScroll(tblData, "Resources"), BorderLayout.CENTER);

            pnlChild = new JPanel(new FlowLayout());
            pnlChild.add(cm.createButton(Commands.Save));
            pnlChild.add(cm.createButton(Commands.Export));

            pnlLeft.add(pnlChild, BorderLayout.SOUTH);

            JPanel pnlRight = new JPanel(new BorderLayout());

            pnlChild = new JPanel(new BorderLayout());

            lbKey = new JLabel();
            pnlChild.add(lbKey, BorderLayout.CENTER);
            pnlRight.add(pnlChild, BorderLayout.NORTH);

            taRef = createTA(false);
            taSrc = createTA(false);
            taValid = createTA(true);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, DustUtilsJavaSwing.setBorderScroll(taRef, "Reference"),
                    DustUtilsJavaSwing.setBorderScroll(taSrc, "Original"));
            split.setResizeWeight(.5);
            split.setDividerLocation(.5);

            split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, split, DustUtilsJavaSwing.setBorderScroll(taValid, "Translated"));
            split.setResizeWeight(.5);
            split.setDividerLocation(.5);

            pnlRight.add(split, BorderLayout.CENTER);

            pnlChild = new JPanel(new FlowLayout());

            addCmdBtn(pnlChild, Commands.AcceptSrc, Commands.LoadRef, Commands.LoadSrc, Commands.Update, Commands.Invalidate);

            pnlRight.add(pnlChild, BorderLayout.SOUTH);

            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLeft, pnlRight);
            split.setResizeWeight(.5);
            split.setDividerLocation(.5);

            add(split, BorderLayout.CENTER);

            updateDisplay();
        }

        private void addCmdBtn(Container target, Commands... cmds) {
            for (Commands cmd : cmds) {
                target.add(cm.createButton(cmd));
            }
        }

        private JTextArea createTA(boolean editable) {
            JTextArea ta = new JTextArea();
            ta.setEditable(editable);
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            return ta;
        }

        private void updateDisplay() {
            updateList(tbShowValid.isSelected(), tfFilter.getText());

            tmRes.updated();

            int filteredCount = alKeys.size();

            if (0 < filteredCount) {
                tblData.getSelectionModel().setSelectionInterval(0, 0);
            }

            lbCounts.setText(MessageFormat.format("({0}/{1}", filteredCount, src.keyLines.size()));
        }

        private void setSelectedKey(String key) {
            selKey = key;

            if (null == selKey) {
                lbKey.setText("");
                taRef.setText("");
                taSrc.setText("");
                taValid.setText("");
            } else {
                lbKey.setText(selKey);
                taRef.setText(ref.valueOfKey(selKey));
                taSrc.setText(src.valueOfKey(selKey));
                taValid.setText(validated.get(selKey));
            }
        }

    }

    private final Language langRef;
    private final Language langTranslate;

    ResFile ref;
    ResFile src;

    final String workFileName;
    Map<String, String> validated;

    String selKey;
    String text;
    ArrayList<String> alKeys = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ResourceTranslator rt = new ResourceTranslator("Translation", Language.en, Language.hu);

        rt.ref = new ResFile(rt.langRef, "_ApplicationResources_en_final_1.2.properties");
        rt.src = new ResFile(rt.langTranslate, "_ApplicationResources_hu_final_3.4.properties");

        rt.check();

        rt.showGui();

        // reload();
        //
        // writeToFile();
        //
        // createAndShowGUI();
    }

    @SuppressWarnings("unchecked")
    public ResourceTranslator(String workFileName, Language langRef, Language langTranslate) {
        super();
        this.workFileName = workFileName;
        this.langRef = langRef;
        this.langTranslate = langTranslate;

        File f = new File(workFileName + ".json");

        if (f.exists()) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));

                validated = (Map<String, String>) new JSONParser().parse(br);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }

        } else {
            validated = new HashMap<>();
        }
    }

    private void updateList(boolean showValidated, String filter) {
        alKeys.clear();

        for (String line : src.lines) {
            String key = src.keyOfLine(line);

            if (null == key) {
                continue;
            }

            if (!showValidated && validated.containsKey(key)) {
                continue;
            }

            String f = filter.trim().toLowerCase();

            if (0 != f.length()) {
                if (!key.toLowerCase().contains(f) && !src.valueOfKey(key).toLowerCase().contains(f)
                        && !src.valueOfKey(key).toLowerCase().contains(f)) {
                    continue;
                }
            }

            alKeys.add(key);
        }
    }

    private void execute(Commands cmd) {
        try {
            Writer w = null;

            switch (cmd) {
            case Save:
                w = new OutputStreamWriter(new FileOutputStream(workFileName + ".json"), "UTF8");
                JSONObject.writeJSONString(validated, w);
                break;
            case Export:
                for (String st : src.lines) {
                    if (null == w) {
                        w = new OutputStreamWriter(new FileOutputStream(workFileName + ".properties"), "UTF8");
                    } else {
                        w.write("\n");
                    }

                    String k = src.keyOfLine(st);
                    if ((null == k) || !validated.containsKey(k)) {
                        w.write(st);
                    } else {
                        w.write(k);
                        w.write("=");
                        w.write(validated.get(k));
                    }
                }

                break;
            default:
                break;
            }

            if (null != w) {
                w.flush();
                w.close();
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

    }

    private void check() {
        System.out.println(ref);
        System.out.println(src);

        diff(ref, src, "Missing en line for hu key ");
        diff(src, ref, "Missing hu line for en key ");
    }

    private void showGui() {

        // Create and set up the window.
        JFrame frame = new JFrame("TextDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocation(10, 10);

        // JLabel lbl = new JLabel("");
        TranslatorPanel tp = new TranslatorPanel();
        frame.add(tp);

        // Display the window.
        frame.pack();
        frame.setVisible(true);

        updateList(false, "");
    }

    private static void diff(ResFile rf1, ResFile rf2, String diffMsg) {
        System.out.println(" ------- ");
        for (String k : rf2.keyLines.keySet()) {
            if (!rf1.keyLines.containsKey(k)) {
                System.out.println(diffMsg + k);
            }
        }
    }

    // private static void reload() throws Exception {
    // ResFile en = new ResFile(Language.en,
    // "_ApplicationResources_en_final_1.2.properties");
    // ResFile hu = new ResFile(Language.hu,
    // "_ApplicationResources_hu_final_3.4.properties");
    //
    // System.out.println(en);
    // System.out.println(hu);
    //
    // diff(en, hu, "Missing en line for hu key ");
    // diff(hu, en, "Missing hu line for en key ");
    //
    // StringBuilder sb = new StringBuilder(
    // "<html> <head> <meta charset=\"utf-8\"/> </head> " + "<body
    // style=\"font-family:Arial; font-size:20px\"> "
    // // + " <font size = \"8\"> "
    // // + "<table style=\"width:100%\" border=\"1\"> "
    // + "<table border=\"1\" >"
    // // + "<tr> <th style=\"width:20px\">row</th> <th
    // style=\"width:50px\">key</th>
    // // <th width=\"40%\">en</th> <th width=\"40%\">hu</th> </tr> \n");
    // + "<tr> <th style=\"width:20px\">row</th> <th style=\"width:50%\">en</th> <th
    // style=\"width:50%\">hu</th> </tr> \n");
    //
    // int i = 0;
    // for (String st : hu.lines) {
    // String k = hu.keyOfLine(st);
    // if (null == k) {
    // // System.out.println(st);
    // } else {
    // String vHu = hu.valueOfKey(k);
    // String vEn = en.valueOfKey(k);
    //
    // // System.out.println(MessageFormat.format("<tr> <td>{0}</td> <td>{1}</td>
    // // <td><span id=\"{0}\">{2}</span></td> </tr>", k, vEn, vHu));
    // // sb.append(MessageFormat.format("<tr> <td>{3}</td> <td>{0}</td>
    // <td>{1}</td>
    // // <td><span id=\"{0}\">{2}</span></td> </tr>\n",
    // sb.append(MessageFormat.format("<tr> <td>{3}</td> <td><span>{1}</span></td>
    // <td><span id=\\\"{0}\\\">{2}</span></td> </tr>\n", k,
    // StringEscapeUtils.escapeHtml4(vEn), StringEscapeUtils.escapeHtml4(vHu),
    // ++i));
    // }
    // }
    //
    // sb.append("</table> "
    // // + "</font> "
    // + "</body> </html>");
    //
    // }

    // private static void writeToFile() throws Exception {
    // try (OutputStreamWriter writer = new OutputStreamWriter(new
    // FileOutputStream("translate.html"), StandardCharsets.UTF_8)) {
    // writer.write(text);
    // writer.flush();
    // writer.close();
    // }
    // }

    // private static void createAndShowGUI() {
    // // Create and set up the window.
    // JFrame frame = new JFrame("TextDemo");
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //
    // // JLabel lbl = new JLabel("");
    // JEditorPane jp = new JTextPane();
    // // jp.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    //
    // jp.setContentType("text/html; charset=utf-8");
    // // jp.setFont(new Font(lbl.getFont().getFontName(), Font.PLAIN, 24));
    //
    // JPanel pnlMain = new JPanel(new BorderLayout());
    //
    // pnlMain.add(new JScrollPane(jp), BorderLayout.CENTER);
    //
    // JToolBar tb = new JToolBar();
    //
    // ActionListener al = new ActionListener() {
    //
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // try {
    // switch (Commands.valueOf(e.getActionCommand())) {
    // case Export:
    // break;
    // case Reload:
    // reload();
    // jp.setText(text);
    // break;
    // case Save:
    // text = jp.getText();
    // writeToFile();
    // break;
    // case Search:
    // break;
    // }
    // ;
    // } catch (Exception e1) {
    // e1.printStackTrace();
    // }
    //
    // }
    // };
    //
    // for (Commands cmd : Commands.values()) {
    // JButton bt = new JButton(cmd.name());
    // bt.setActionCommand(cmd.name());
    // bt.addActionListener(al);
    // tb.add(bt);
    // }
    //
    // pnlMain.add(tb, BorderLayout.NORTH);
    //
    // // Add contents to the window.
    // frame.add(pnlMain);
    //
    // // Display the window.
    // frame.pack();
    // frame.setVisible(true);
    //
    // jp.setText(text);
    //
    // frame.setSize(1000, 800);
    // frame.setLocation(10, 10);
    // }

}
