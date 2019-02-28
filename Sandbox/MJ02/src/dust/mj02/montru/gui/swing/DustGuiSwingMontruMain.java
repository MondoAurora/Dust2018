package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustGuiSwingMontruMain extends JPanel implements DustGuiSwingMontruComponents, DustProcComponents.DustProcActive {
	private static final long serialVersionUID = 1L;

	class MainFrame extends JFrame {
		private static final long serialVersionUID = 1L;

		public MainFrame() {
			super("Dust/Montru in Java Swing");
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
	
	MainFrame appFrame;

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
	public void dustProcActiveInit() {
		appFrame = new MainFrame();
	}
	
	@Override
	public void dustProcActiveRelease() throws Exception {
		if ( null != appFrame ) {
			appFrame.dispose();
			appFrame = null;
		}
	}
	
	protected void saveState() {
		
	}
}
