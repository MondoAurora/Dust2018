package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import dust.mj02.dust.knowledge.DustProcComponents;

public class MontruGuiSwingFrame implements MontruGuiSwingComponents, DustProcComponents.DustProcInitable {

	class Frame extends JFrame {
		private static final long serialVersionUID = 1L;

		public Frame() {
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					saveState();
				}
			});

			setPreferredSize(INIT_FRAME_SIZE);

			pnlEditor = new MontruGuiSwingPanelEditor();

			getContentPane().add(pnlEditor, BorderLayout.CENTER);

			pack();
			setVisible(true);
		}
	}

	Frame frame;
	MontruGuiSwingPanelEditor pnlEditor;

	public MontruGuiSwingFrame() {
		this.frame = new Frame();

		dustProcInitableInit();
	};

	@Override
	public void dustProcInitableInit() {
		frame.setTitle(getClass().getSimpleName());

		pnlEditor.pnlDesktop.reloadData();
	}
	
	protected void saveState() {
		
	}


}
