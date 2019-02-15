package dust.mj02.montru.gui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustComponents;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings({ "unchecked" })
public interface MontruGuiComponents
		extends DustComponents, DustMetaComponents, DustProcComponents, DustGenericComponents {

	enum GuiEntityKey {
		entity, type, models, id, owner, attDefs, linkDefs, title, panel, showFlags//, links
	}

	enum GuiRefKey {
		ref, source, target, linkDef, key, selected
	}

	enum GuiShowFlag {
		hide, showEmpty, showLine
	};

	enum GuiCommands {
		createEntity, deleteEntity, deleteRef, test01
	};

	enum WidgetType {
		entityPanel, entityHead, dataLabel, dataEditor
	};
	
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

		public boolean add(NodeKey key, Object val) {
			if (null == val) {
				return false;
			}
			Set<Object> cont = (Set<Object>) content.get(key);
			if (null == cont) {
				content.put(key, cont = new HashSet<>());
			}
			return cont.add(val);
		}

		public boolean contains(NodeKey key, Object val) {
			Set<Object> cont = (Set<Object>) content.get(key);
			return (null == cont) ? false : cont.contains(val);
		}

		public boolean remove(NodeKey key, Object val) {
			Set<Object> cont = (Set<Object>) content.get(key);
			return (null == cont) ? false : cont.remove(val);
		}

		public boolean isTrue(NodeKey k) {
			return Boolean.TRUE.equals(get(k));
		}
	}

	class GuiRefInfo extends NodeInfo<GuiRefKey> {
		public GuiRefInfo(GuiEditorModel em, DustRef ref) {
			super(GuiRefKey.class);

			GuiEntityInfo s = em.getEntityInfo(ref.get(RefKey.source));
			GuiEntityInfo t = em.getEntityInfo(ref.get(RefKey.target));

			put(GuiRefKey.ref, ref);

			put(GuiRefKey.source, s);
			put(GuiRefKey.target, t);
			put(GuiRefKey.linkDef, em.getEntityInfo(ref.get(RefKey.linkDef)));
			
			Object key = ref.get(RefKey.key);
			put(GuiRefKey.key, (key instanceof DustEntity) ? em.getEntityInfo((DustEntity) key) : key);
		}

		public DustEntity getEntity(GuiRefKey rk) {
			return ((GuiEntityInfo) get(rk)).get(GuiEntityKey.entity);
		}

		public void remove() {
			Dust.accessEntity(DataCommand.removeRef, getEntity(GuiRefKey.source), getEntity(GuiRefKey.linkDef),
					getEntity(GuiRefKey.target), get(GuiRefKey.key));
		}
	}

	class GuiEntityInfo extends NodeInfo<GuiEntityKey> {
		public GuiEntityInfo() {
			super(GuiEntityKey.class);
		}

		public String getTitle() {
			String title = get(GuiEntityKey.title);

			if (null == title) {
				GuiEntityInfo tt = get(GuiEntityKey.type);
				title = DustUtilsJava
						.sbAppend(null, " ", true, (null == tt) ? "" : tt.get(GuiEntityKey.id), ":", get(GuiEntityKey.id))
						.toString();
				put(GuiEntityKey.title, title);
			}

			return title;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(getTitle()).append(" [");

			DustEntity entity = get(GuiEntityKey.entity);
//			DustUtilsFactory<Object, StringBuilder> factRefs = new DustUtilsFactory<Object, StringBuilder>(false) {
//				@Override
//				protected StringBuilder create(Object key, Object... hints) {
//					return new StringBuilder();
//				}
//			};
//
//			for (GuiRefInfo ri : (Iterable<GuiRefInfo>) get(GuiEntityKey.links)) {
//				factRefs.get(ri.get(GuiRefKey.linkDef)).append(((GuiEntityInfo) ri.get(GuiRefKey.target)).getTitle())
//						.append(", ");
//			}

			for (GuiEntityInfo m : (Iterable<GuiEntityInfo>) get(GuiEntityKey.models)) {
				DustUtilsJava.sbAppend(sb, " ", true, m.get(GuiEntityKey.id), ": {");

				Iterable<GuiEntityInfo> it = (Iterable<GuiEntityInfo>) m.get(GuiEntityKey.attDefs);
				if (null != it) {
					for (GuiEntityInfo ad : it) {
						DustUtilsJava.sbAppend(sb, " ", true, ad.get(GuiEntityKey.id), "=",
								Dust.accessEntity(DataCommand.getValue, entity, ad.get(GuiEntityKey.entity), null, null),
								", ");
					}
				}

//				it = (Iterable<GuiEntityInfo>) m.get(GuiEntityKey.linkDefs);
//				if (null != it) {
//					for (GuiEntityInfo ld : it) {
//						DustUtilsJava.sbAppend(sb, " ", true, ld.get(GuiEntityKey.id), " -> {", factRefs.peek(ld), "}, ");
//					}
//				}

				sb.append("}");
			}

			sb.append("]");

			return sb.toString();
		}
	}
	
	interface GuiEditorModel {
		void refreshData();
		
		GuiEntityInfo getEntityInfo(DustEntity entity);
		GuiRefInfo getRefInfo(DustRef ref);
		
		Iterable<GuiRefInfo> getAllRefs();
		Iterable<GuiEntityInfo> getAllEntities();
		ArrayList<GuiEntityInfo> getAllTypes();

		Iterable<GuiEntityInfo> dropRefs(Iterable<GuiRefInfo> refs);
	}
	
	interface GuiChangeListener {
		void guiChangedAttribute(GuiEntityInfo entity, GuiEntityInfo att, Object value);
		void guiChangedRef(GuiEntityInfo entity, GuiRefInfo ref, DataCommand cmd);
	}
	
	interface GuiEntityElement extends GuiChangeListener {
		GuiEntityInfo getEntityInfo();
//		void updateDisplay();
	}

	interface GuiEntityDataElement extends GuiEntityElement {
		GuiEntityInfo getDataInfo();
	}
}
