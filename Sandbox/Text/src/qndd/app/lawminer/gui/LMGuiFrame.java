package qndd.app.lawminer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dust.qnd.util.QnDDUtils;

public class LMGuiFrame extends JFrame implements LMGuiComponents {
	private static final long serialVersionUID = 1L;

	private static final Dimension INIT_FRAME_SIZE = new Dimension(800, 400);

	private JDesktopPane dpMain;
	
	class LineLayer extends JLabel {
		private static final long serialVersionUID = 1L;
		Rectangle rctSelf = new Rectangle();
		
		public LineLayer() {
//			setOpaque(false);
		}
		
		@Override
		public void paint(Graphics g) {
			getBounds(rctSelf);
			
			g.drawLine(rctSelf.x, rctSelf.y, rctSelf.x + rctSelf.width, rctSelf.y + rctSelf.height);
			g.drawLine(rctSelf.x, rctSelf.y + rctSelf.height, rctSelf.x + rctSelf.width, rctSelf.y);
		}
	}
	
	LineLayer ll = new LineLayer();

	public LMGuiFrame() {
		super(QnDDUtils.formatEnum(LMGuiTexts.AppTitle));

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setPreferredSize(INIT_FRAME_SIZE);

		dpMain = new JDesktopPane();
		dpMain.setOpaque(false);
		getContentPane().add(dpMain, BorderLayout.CENTER);
		
		JPanel pnlLl = new JPanel(new BorderLayout());
		pnlLl.add(ll, BorderLayout.CENTER);
		pnlLl.setVisible(true);
		dpMain.add(ll, JDesktopPane.DEFAULT_LAYER);

		JInternalFrame internal = new JInternalFrame("Search", true, false, true, true);
		internal.getContentPane().add(new LMGuiPanelSearchLaw(), BorderLayout.CENTER);
		internal.pack();
		dpMain.add(internal, JDesktopPane.DEFAULT_LAYER);
		internal.setVisible(true);

		pack();
		setVisible(true);
	}

}
