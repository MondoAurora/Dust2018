package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dust.mj02.dust.knowledge.DustProcComponents;

public class MontruGuiSwingFrame implements MontruGuiSwingComponents, DustProcComponents.DustProcInitable {

	class Frame extends JFrame {
		private static final long serialVersionUID = 1L;

		public Frame() {
			setDefaultCloseOperation(EXIT_ON_CLOSE);

			setPreferredSize(INIT_FRAME_SIZE);

			pnlEditor = new MontruGuiSwingPanelEditor();

			JPanel pnlCmds = new JPanel(new FlowLayout(FlowLayout.LEFT));

			JButton btn = new JButton("Test!");
			pnlCmds.add(btn);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dustProcInitableInit();
				}
			});

			JPanel pnlMain = new JPanel(new BorderLayout());

			pnlMain.add(new JScrollPane(pnlEditor), BorderLayout.CENTER);
			pnlMain.add(pnlCmds, BorderLayout.SOUTH);

			getContentPane().add(pnlMain, BorderLayout.CENTER);

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

		pnlEditor.reloadData();
	}

}
