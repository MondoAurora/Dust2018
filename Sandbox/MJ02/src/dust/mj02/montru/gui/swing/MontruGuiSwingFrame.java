package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustComponents;
import dust.mj02.dust.knowledge.DustKnowledgeGen;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.mj02.dust.tools.DustToolsGen;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings("unchecked")
public class MontruGuiSwingFrame implements DustComponents, DustMetaComponents, DustProcComponents,
		DustGenericComponents, DustProcComponents.DustProcInitable {
	
	private static final Dimension INIT_FRAME_SIZE = new Dimension(800, 400);

	enum InfoKey {
		entity, type, id, owner, attributes, links
	}
	
	class EntityInfo extends EnumMap<InfoKey, Object> {
		private static final long serialVersionUID = 1L;

		public EntityInfo() {
			super(InfoKey.class);
		}
		
		public void add(InfoKey key, Object val) {
			Set<Object> cont = (Set<Object>) get(key);
			if ( null == cont ) {
				put(key, cont = new HashSet<>());
			}
			cont.add(val);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = null;
			sb = DustUtilsJava.sbAppend(sb, ": ", true, get(InfoKey.type) + ": " + get(InfoKey.id));
			
			StringBuilder sbc = DustUtilsJava.toStringBuilder(null, (Iterable<?>) get(InfoKey.attributes), false, "atts");
			
			DustUtilsJava.sbAppend(sb, " ", false, sbc);
			
			sbc = DustUtilsJava.toStringBuilder(null, (Iterable<?>) get(InfoKey.links), false, "links");
			
			DustUtilsJava.sbAppend(sb, " ", false, sbc);
			
			return sb.toString();
		}
	}
	
	class PnlEntity extends JPanel {
		private static final long serialVersionUID = 1L;
		
		EntityInfo ei;

		public PnlEntity(EntityInfo ei) {
			super(new BorderLayout());
			this.ei = ei;
			
			add(new JLabel(ei.toString()));
		}
	}
	
	class PnlEditor extends JPanel {
		private static final long serialVersionUID = 1L;

		public PnlEditor() {
			super(null);
			BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
			
			setLayout(bl);
		}
		

		public void reloadData() {
			removeAll();
			
			for ( DustEntity k : factEntityInfo.keys() ) {
				add(new PnlEntity(factEntityInfo.peek(k)));
			}
			
			revalidate();
			repaint();
		}
	}
	
	class Frame extends JFrame {
		private static final long serialVersionUID = 1L;

		public Frame() {
			setDefaultCloseOperation(EXIT_ON_CLOSE);

			setPreferredSize(INIT_FRAME_SIZE);

			pnlEditor = new PnlEditor();
			
			JPanel pnlCmds = new JPanel(new FlowLayout(FlowLayout.LEFT));
			
			JButton btn = new JButton("Test!");
			pnlCmds.add(btn);
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
			
			
			JPanel pnlMain = new JPanel(new BorderLayout());
			
			pnlMain.add(new JScrollPane(pnlEditor), BorderLayout.CENTER);
			pnlMain.add(pnlCmds, BorderLayout.SOUTH);
			
			getContentPane().add(pnlMain, BorderLayout.CENTER);

			pack();
			setVisible(true);
		}
	}


	DustUtilsFactory<DustEntity, EntityInfo> factEntityInfo = new DustUtilsFactory<DustEntity, EntityInfo>(false) {
		@Override
		protected EntityInfo create(DustEntity key, Object... hints) {
			EntityInfo ret = new EntityInfo();
			ret.put(InfoKey.entity, key);
			return ret;
		}
	};
	
	Frame frame;
	PnlEditor pnlEditor;

	public MontruGuiSwingFrame() {
		this.frame = new Frame();
	};

	@Override
	public void dustProcInitableInit() throws Exception {
		frame.setTitle(getClass().getSimpleName());

		Map<Object, Object> resId = DustKnowledgeGen.resolveAll(null, DustMetaTypes.Type, DustMetaTypes.AttDef,
				DustMetaTypes.LinkDef, DustDataLinks.EntityPrimaryType);
		DustToolsGen.resolveAll(resId, DustGenericAtts.identifiedIdLocal, DustGenericLinks.Owner,
				DustGenericLinks.Extends);

		Map<Object, Object> resEntity = new HashMap<Object, Object>();

		for (Object k : resId.keySet().toArray()) {
			resEntity.put(k, Dust.getEntity(resId.get(k)));
		}

		Map<Object, Object> resEntityRev = new HashMap<Object, Object>();

		for (Map.Entry<Object, Object> ee : resEntity.entrySet()) {
			resEntityRev.put(ee.getValue(), ee.getKey());
		}

		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustEntity source, DustEntity linkDef, DustEntity target, Object key) {
				if (resEntity.containsValue(target)) {
					if (linkDef == resEntity.get(DustDataLinks.EntityPrimaryType)) {
						EntityInfo eiObj = factEntityInfo.get(source);
						
						String id = Dust.accessEntity(DataCommand.getValue, source,
								resId.get(DustGenericAtts.identifiedIdLocal), null, null);
						
						eiObj.put(InfoKey.id, id);
						eiObj.put(InfoKey.type, resEntityRev.get(target));	
					}
				} else if (linkDef == resEntity.get(DustGenericLinks.Owner)) {
					EntityInfo eiType = factEntityInfo.get(target);
					EntityInfo eiObj = factEntityInfo.get(source);
					
					eiObj.put(InfoKey.owner, target);
					eiType.add((DustMetaTypes.AttDef == eiObj.get(InfoKey.type)) ? InfoKey.attributes : InfoKey.links, eiObj);
				}
			}
		}, null, null, null);
		

		pnlEditor.reloadData();
	}

}
