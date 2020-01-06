package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.RootPaneContainer;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustGuiSwingMontruMain extends JPanel implements DustGuiSwingMontruComponents, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public static void makeChild() {
	    String title = "Hello " + sdf.format(new Date());
	    
        Window frm;
        
//        frm = new JFrame(title);
//        ((JFrame)frm).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        frm = new JDialog(appFrame, title, false);
//        frm = new JWindow(appFrame);
        
        frm.setPreferredSize(new Dimension(200, 100));

        ((RootPaneContainer)frm).getContentPane().add(new JLabel("Pukk"), BorderLayout.CENTER);

        frm.pack();
        frm.setVisible(true);

	}

	class MainFrame extends JFrame {
		private static final long serialVersionUID = 1L;

		public MainFrame() {
			super("Dust/Montru in Java Swing " + sdf.format(new Date()));
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					saveState();
				}
			});

			setPreferredSize(INIT_FRAME_SIZE);

			getContentPane().add(DustGuiSwingMontruMain.this, BorderLayout.CENTER);

			pack();
			setVisible(true);
		}
	}

	DustEntity eMontruDesktop;
	DustGuiSwingMontruDesktop montruDesktop;
	DustGuiSwingMontruControl pnlControl;
	
	static MainFrame appFrame;

	public DustGuiSwingMontruMain() {
		super(new BorderLayout());
		MontruGuiSwingGen.init();
				
		eMontruDesktop = DustUtils.accessEntity(DataCommand.getEntity, MontruGuiTypes.MontruDesktop, null, "MontruDesktop");
		montruDesktop = DustUtils.getBinary(eMontruDesktop, MontruGuiServices.MontruDesktop);
		
		pnlControl = new DustGuiSwingMontruControl(montruDesktop);
		
		JComponent cmpRight = new JScrollPane(montruDesktop);

		JSplitPane spMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlControl, cmpRight);
		add(spMain, BorderLayout.CENTER);		
	};

	@Override
	public void activeInit() {
		appFrame = new MainFrame();
	}
	
	@Override
	public void activeRelease() throws Exception {
		if ( null != appFrame ) {
			appFrame.dispose();
			appFrame = null;
		}
	}
	
	protected void saveState() {
		
	}
}
