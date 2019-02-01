package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustComponents;
import dust.mj02.dust.knowledge.DustKnowledgeGen;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.mj02.dust.tools.DustToolsGen;
import dust.utils.DustUtilsDev;

public class MontruGuiSwingFrame extends JFrame
		implements DustComponents, DustMetaComponents, DustProcComponents, DustGenericComponents, DustProcComponents.DustProcInitable {
	private static final long serialVersionUID = 1L;

	private static final Dimension INIT_FRAME_SIZE = new Dimension(800, 400);

	class EntityWrapper {
		boolean deleteFlag;
		DustEntity entity;

	}

	JPanel pnlMain;

	public MontruGuiSwingFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setPreferredSize(INIT_FRAME_SIZE);

		pnlMain = new JPanel(null);
		pnlMain.setOpaque(false);
		getContentPane().add(pnlMain, BorderLayout.CENTER);

		JButton btn = new JButton("hopp");
		pnlMain.add(btn);
		btn.setBounds(10, 10, 200, 50);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					dustProcInitableInit();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		pack();
		setVisible(true);
	}

	@Override
	public void dustProcInitableInit() throws Exception {
		setTitle(getClass().getSimpleName());

		Map<Object, Object> res = DustKnowledgeGen.resolveAll(null, DustMetaTypes.Type, DustMetaTypes.AttDef,
				DustMetaTypes.LinkDef);
		
		for ( Object k : res.keySet().toArray() ) {
			res.put(k, Dust.getEntity(res.get(k)));
		}
		
		DustToolsGen.resolveAll(res, DustGenericAtts.identifiedIdLocal, DustGenericLinks.Owner, DustGenericLinks.Extends);

		DustUtilsDev.dump("===============");
		
		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustEntity source, DustEntity linkDef, DustEntity target, Object key) {
				if ( res.containsValue(target)) {
					String tid = Dust.accessEntity(DataCommand.getValue, target, res.get(DustGenericAtts.identifiedIdLocal), null, null);
					String id = Dust.accessEntity(DataCommand.getValue, source, res.get(DustGenericAtts.identifiedIdLocal), null, null);
					DustUtilsDev.dump("Found meta information:", tid, id);
				} else {
//					DustUtilsDev.dump("Not type");
				}
			}
		}, null, null, null);
	}

}
