package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import dust.mj02.montru.gui.MontruGuiComponents;
import dust.utils.DustUtilsFactory;

class MontruGuiSwingPanelEditor extends JDesktopPane implements MontruGuiSwingComponents, MontruGuiSwingComponents.EntityInfoResolver {
	private static final long serialVersionUID = 1L;
	
	ArrayList<RefInfo> arrRefs = new ArrayList<>();
	ArrayList<EntityInfo> arrTypes = new ArrayList<>();

	DustUtilsFactory<EntityInfo, JInternalFrame> factIntFrames = new DustUtilsFactory<EntityInfo, JInternalFrame>(
			false) {
		@Override
		protected JInternalFrame create(EntityInfo key, Object... hints) {
			JInternalFrame internal = new JInternalFrame(key.getTitle(), true, false, true, true);
			MontruGuiSwingPanelEntity pnl = new MontruGuiSwingPanelEntity(key);
			key.put(EntityKey.panel, pnl);
			internal.getContentPane().add(pnl, BorderLayout.CENTER);
			internal.setTitle(key.getTitle());
			internal.pack();

			pnlLinks.followContent(internal);

			return internal;
		}
	};

	MouseListener ml = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {

			if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
				for ( RefInfo ri : arrRefs ) {
					ri.put(RefKey.selected, false);
				}
			}
			pnlLinks.hitTest(e.getPoint());
		}
	};
	
	MontruGuiSwingPanelLinks pnlLinks;

	public MontruGuiSwingPanelEditor() {
		pnlLinks = new MontruGuiSwingPanelLinks(this, arrRefs);
		pnlLinks.followParent(this);

		addMouseListener(ml);
	}

	@Override
	public JComponent getEntityPanel(EntityInfo ei) {
		return factIntFrames.peek(ei);
	}
	
	public void reloadData() {
		MontruGuiComponents.loadRefsAndEntities(arrTypes, arrRefs);

		removeAll();

		add(pnlLinks, JDesktopPane.POPUP_LAYER);

		for (DustEntity k : MontruGuiSwingFrame.factEntityInfo.keys()) {
			JInternalFrame jif = factIntFrames.get(MontruGuiSwingFrame.factEntityInfo.peek(k));
			add(jif, JDesktopPane.DEFAULT_LAYER);
			jif.setVisible(true);
		}

		revalidate();
		repaint();
	}
}