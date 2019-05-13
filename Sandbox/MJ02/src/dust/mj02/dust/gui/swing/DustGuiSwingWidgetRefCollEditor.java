package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.gui.DustGuiComponents;

public class DustGuiSwingWidgetRefCollEditor extends JPanel implements DustGuiComponents {
	
	private static final long serialVersionUID = 1L;
	
	DustEntity entity;
	JLabel lblHead = new JLabel();

	DefaultListModel<DustEntity> lmLinks = new DefaultListModel<>();
	
	public DustGuiSwingWidgetRefCollEditor() {
        super(new BorderLayout());
                
        add(lblHead, BorderLayout.NORTH);
        
        JList<DustEntity> lst = new JList<>(lmLinks);
        
        lst.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        add(new JScrollPane(lst), BorderLayout.CENTER);
    }
    
    public void setEntity(DustEntity entity) {
        this.entity = entity;
    }
    
    public void setLinkDef(DustEntity eLinkDef) {
	    lblHead.setText((null == eLinkDef) ? "Select a link" : eLinkDef.toString());
	    
	    lmLinks.clear();
	    
	    DustUtils.accessEntity(DataCommand.processRef, entity, eLinkDef, new RefProcessor() {
            
            @Override
            public void processRef(DustRef ref) {
                lmLinks.addElement(ref.get(RefKey.target));
            }
        });
	}

}
