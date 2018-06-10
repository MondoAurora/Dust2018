package qndd.app.lawminer.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

import dust.qnd.util.QnDDUtils;

public class LMGuiFrame extends JFrame implements LMGuiComponents {
	private static final long serialVersionUID = 1L;
	
	private static final Dimension INIT_FRAME_SIZE = new Dimension(800, 400);

	public LMGuiFrame() {
		super(QnDDUtils.formatEnum(LMGuiTexts.AppTitle));
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setPreferredSize(INIT_FRAME_SIZE);
		
		getContentPane().add(new LMGuiPanelSearchLaw());
		
		pack();
		setVisible(true);
	}

}
