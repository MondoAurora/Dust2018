package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import dust.mj02.montru.gui.MontruGuiComponents;
import dust.utils.DustUtilsFactory;

@SuppressWarnings("serial")
class MontruGuiSwingPanelEditor extends JPanel
		implements MontruGuiSwingComponents, MontruGuiSwingComponents.EntityInfoResolver {
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

			pnlDesktop.pnlLinks.followContent(internal);

			return internal;
		}
	};

	class PnlDesktop extends JDesktopPane {
		MontruGuiSwingPanelLinks pnlLinks;

		MouseListener ml = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
					for (RefInfo ri : arrRefs) {
						ri.put(RefKey.selected, false);
					}
				}
				pnlLinks.hitTest(e.getPoint());
			}
		};

		public PnlDesktop() {
			pnlLinks = new MontruGuiSwingPanelLinks(MontruGuiSwingPanelEditor.this, arrRefs);
			pnlLinks.followParent(this);

			addMouseListener(ml);
		}

		public void reloadData() {
			MontruGuiComponents.loadRefsAndEntities(arrTypes, arrRefs);
			pnlMeta.lmTypes.update();

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
	
	class TypelistModel extends AbstractListModel<EntityInfo> {
		@Override
		public EntityInfo getElementAt(int index) {
			return arrTypes.get(index);
		}

		@Override
		public int getSize() {
			return arrTypes.size();
		}
		
		private void update() {
			fireContentsChanged(pnlMeta, 0, getSize());
		}
	}
	
	class TypeRenderer extends JLabel implements ListCellRenderer<EntityInfo> {
	    @Override
	    public Component getListCellRendererComponent(JList<? extends EntityInfo> list, EntityInfo item, int index,
	        boolean isSelected, boolean cellHasFocus) {
	        setText(item.getTitle());
	        return this;
	    }
	}
	
	class PnlMetaControl extends JPanel {
		TypelistModel lmTypes = new TypelistModel();
		TypeRenderer crTypes = new TypeRenderer();

		public PnlMetaControl() {
			super(new BorderLayout(5, 5));
			
			JList<EntityInfo> lstTypes = new JList<EntityInfo>(lmTypes);
			JScrollPane scpTypes = new JScrollPane(lstTypes);
			scpTypes.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Types"));
			
			lstTypes.setCellRenderer(crTypes);
			
			add(scpTypes, BorderLayout.CENTER);
			
			JButton btn = new JButton("Test!");
			add(btn, BorderLayout.SOUTH);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					pnlDesktop.reloadData();
				}
			});
		}
	}

	PnlDesktop pnlDesktop;
	PnlMetaControl pnlMeta;

	public MontruGuiSwingPanelEditor() {
		super(new BorderLayout());

		pnlDesktop = new PnlDesktop();
		pnlMeta = new PnlMetaControl();

		JSplitPane spMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlMeta, new JScrollPane(pnlDesktop));
		add(spMain, BorderLayout.CENTER);
	}

	@Override
	public JComponent getEntityPanel(EntityInfo ei) {
		return factIntFrames.peek(ei);
	}

}