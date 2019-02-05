package dust.mj02.montru.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings({"unchecked", "serial"})
public class MontruGuiSwingFrame implements DustComponents, DustMetaComponents, DustProcComponents,
		DustGenericComponents, DustProcComponents.DustProcInitable {
	
	private static final Dimension INIT_FRAME_SIZE = new Dimension(800, 400);

	enum EntityKey {
		entity, type, models, id, owner, attDefs, linkDefs, atts, links
	}
	
	enum RefKey {
		source, target, linkDef, key
	}
	
	class NodeInfo<NodeKey extends Enum<NodeKey>> {
		private EnumMap<NodeKey, Object> content;

		public NodeInfo(Class<NodeKey> kc) {
			content = new EnumMap<>(kc);
		}
		
		public <RetVal> RetVal put(NodeKey key, Object val) {
			return (RetVal) content.put(key, val);
		}
		
		public <RetVal> RetVal get(NodeKey key) {
			return (RetVal) content.get(key);
		}
		
		public void add(NodeKey key, Object val) {
			if ( null == val ) {
				return;
			}
			Set<Object> cont = (Set<Object>) content.get(key);
			if ( null == cont ) {
				content.put(key, cont = new HashSet<>());
			}
			cont.add(val);
		}
	}
	
	class RefInfo extends NodeInfo<RefKey> {
		public RefInfo(DustEntity source, DustEntity linkDef, DustEntity target, Object key) {
			super(RefKey.class);
			
			put(RefKey.source, factEntityInfo.get(source));
			put(RefKey.target, factEntityInfo.get(target));
			put(RefKey.linkDef, factEntityInfo.get(linkDef));
			
			put(RefKey.key, (key instanceof DustEntity) ? factEntityInfo.get((DustEntity)key) : key );
		}
	}
	
	class EntityInfo extends NodeInfo<EntityKey> {
		public EntityInfo() {
			super(EntityKey.class);
			put(EntityKey.atts, new HashMap<EntityInfo, Object>());
			put(EntityKey.links, new HashSet<RefInfo>());
		}
		
		@Override
		public String toString() {
			StringBuilder sb = null;
			sb = DustUtilsJava.sbAppend(sb, ": ", true, get(EntityKey.type) + ": " + get(EntityKey.id));
			
			StringBuilder sbc = DustUtilsJava.toStringBuilder(null, (Iterable<?>) get(EntityKey.models), false, "Models");
			DustUtilsJava.sbAppend(sb, " ", false, sbc);
			
			sbc = DustUtilsJava.toStringBuilder(null, (Iterable<?>) get(EntityKey.attDefs), false, "atts");
			DustUtilsJava.sbAppend(sb, " ", false, sbc);
			
			sbc = DustUtilsJava.toStringBuilder(null, (Iterable<?>) get(EntityKey.linkDefs), false, "linkDefs");
			DustUtilsJava.sbAppend(sb, " ", false, sbc);
			
			return sb.toString();
		}

	}
	
	class PnlEntity extends JPanel {
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
			ret.put(EntityKey.entity, key);
			
			String id = Dust.accessEntity(DataCommand.getValue, key,
					EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal), null, null);
			
			ret.put(EntityKey.id, id);

			return ret;
		}
	};
	
	ArrayList<RefInfo> arrRefs = new ArrayList<>();
	
//	Map<Object, Object> resId = new HashMap<Object, Object>();
//	Map<Object, DustEntity> resEntity = new HashMap<Object, DustEntity>();
//	Map<DustEntity, Object> resEntityRev = new HashMap<DustEntity, Object>();
	
	Set<EntityInfo> allAtts = new HashSet<>();

	
	Frame frame;
	PnlEditor pnlEditor;

	public MontruGuiSwingFrame() {
		this.frame = new Frame();
	};

	@Override
	public void dustProcInitableInit() throws Exception {
		frame.setTitle(getClass().getSimpleName());
		
//		resId.clear();
//		resEntity.clear();
//		resEntityRev.clear();

		allAtts.clear();

//		DustKnowledgeGen.resolveAll(resId, DustMetaTypes.Type, DustMetaTypes.AttDef,
//				DustMetaTypes.LinkDef, DustDataLinks.EntityPrimaryType);
//		DustToolsGen.resolveAll(resId, DustGenericAtts.identifiedIdLocal, DustGenericLinks.Owner,
//				DustGenericLinks.Extends);
//
//		for (Object k : resId.keySet().toArray()) {
//			resEntity.put(k, Dust.getEntity(resId.get(k)));
//		}
//
//		for (Map.Entry<Object, DustEntity> ee : resEntity.entrySet()) {
//			resEntityRev.put(ee.getValue(), ee.getKey());
//		}
		
		arrRefs.clear();

		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustEntity source, DustEntity linkDef, DustEntity target, Object key) {
				RefInfo ri = new RefInfo(source, linkDef, target, key);
				arrRefs.add(ri);
				
				EntityInfo eiObj = factEntityInfo.get(source);	
				((Set<RefInfo>)eiObj.get(EntityKey.links)).add(ri);
				
				if (linkDef == EntityResolver.getEntity(DustDataLinks.EntityPrimaryType)) {
					eiObj.put(EntityKey.type, EntityResolver.getKey(target));
				} else if (linkDef == EntityResolver.getEntity(DustDataLinks.EntityModels)) {
					eiObj.add(EntityKey.models, EntityResolver.getKey(target));
				} 

//				if (resEntity.containsValue(target)) {
//					if (linkDef == resEntity.get(DustDataLinks.EntityPrimaryType)) {
//						eiObj.put(EntityKey.type, resEntityRev.get(target));	
//					}
//				} 
//				else if (linkDef == resEntity.get(DustGenericLinks.Owner)) {
//					EntityInfo eiType = factEntityInfo.get(target);
//					
//					eiObj.put(EntityKey.owner, eiType);
//					if (DustMetaTypes.AttDef == eiObj.get(EntityKey.type)) {
//						eiType.add(EntityKey.attDefs, eiObj);
//						allAtts.add(eiObj);
//					} else {
//						eiType.add(EntityKey.linkDefs, eiObj);
//					}
//				}
			}
		}, null, null, null);
		
		Dust.processEntities(new EntityProcessor() {
			@Override
			public void processEntity(Object key, DustEntity entity) {
				EntityInfo ei = factEntityInfo.get(entity);
				
				for ( EntityInfo eiAtt : allAtts ) {
					Object val = Dust.accessEntity(DataCommand.getValue, entity, eiAtt.get(EntityKey.entity), null, null);
					if ( null != val ) {
//						EntityInfo eiType = (EntityInfo) eiAtt.get(EntityKey.owner);
						((Map<EntityInfo, Object>)ei.get(EntityKey.atts)).put(eiAtt, val);
					}
				}
			}
		});
		

		pnlEditor.reloadData();
	}

}
