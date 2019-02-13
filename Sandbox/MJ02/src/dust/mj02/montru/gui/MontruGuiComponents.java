package dust.mj02.montru.gui;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
		entity, type, models, id, owner, attDefs, linkDefs, links, title, panel, showFlags
	}

	enum GuiRefKey {
		source, target, linkDef, key, selected
	}

	enum GuiShowFlag {
		hide, showEmpty, showLine
	};

	enum GuiCommands {
		createEntity, deleteEntity, deleteRef, test01
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
		public GuiRefInfo(DustRef ref) {
			super(GuiRefKey.class);

			GuiEntityInfo s = factEntityInfo.get(ref.get(RefKey.source));
			GuiEntityInfo t = factEntityInfo.get(ref.get(RefKey.target));

			put(GuiRefKey.source, s);
			put(GuiRefKey.target, t);
			put(GuiRefKey.linkDef, factEntityInfo.get(ref.get(RefKey.linkDef)));
			
			Object key = ref.get(RefKey.key);
			put(GuiRefKey.key, (key instanceof DustEntity) ? factEntityInfo.get((DustEntity) key) : key);
			
			s.add(GuiEntityKey.links, this);
//			t.add(GuiEntityKey.links, this);
		}

		public DustEntity getEntity(GuiRefKey rk) {
			return ((GuiEntityInfo) get(rk)).get(GuiEntityKey.entity);
		}

		public void remove() {
			Dust.accessEntity(DataCommand.removeRef, getEntity(GuiRefKey.source), getEntity(GuiRefKey.linkDef),
					getEntity(GuiRefKey.target), get(GuiRefKey.key));
			((GuiEntityInfo) get(GuiRefKey.source)).remove(GuiEntityKey.links, this);
//			((GuiEntityInfo) get(GuiRefKey.target)).remove(GuiEntityKey.links, this);
		}
	}

	class GuiEntityInfo extends NodeInfo<GuiEntityKey> {
		public GuiEntityInfo() {
			super(GuiEntityKey.class);
			put(GuiEntityKey.links, new HashSet<GuiRefInfo>());
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
			DustUtilsFactory<Object, StringBuilder> factRefs = new DustUtilsFactory<Object, StringBuilder>(false) {
				@Override
				protected StringBuilder create(Object key, Object... hints) {
					return new StringBuilder();
				}
			};

			for (GuiRefInfo ri : (Iterable<GuiRefInfo>) get(GuiEntityKey.links)) {
				factRefs.get(ri.get(GuiRefKey.linkDef)).append(((GuiEntityInfo) ri.get(GuiRefKey.target)).getTitle())
						.append(", ");
			}

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

				it = (Iterable<GuiEntityInfo>) m.get(GuiEntityKey.linkDefs);
				if (null != it) {
					for (GuiEntityInfo ld : it) {
						DustUtilsJava.sbAppend(sb, " ", true, ld.get(GuiEntityKey.id), " -> {", factRefs.peek(ld), "}, ");
					}
				}

				sb.append("}");
			}

			sb.append("]");

			return sb.toString();
		}

	}

	DustUtilsFactory<DustEntity, GuiEntityInfo> factEntityInfo = new DustUtilsFactory<DustEntity, GuiEntityInfo>(false) {
		@Override
		protected GuiEntityInfo create(DustEntity key, Object... hints) {
			GuiEntityInfo ret = new GuiEntityInfo();
			ret.put(GuiEntityKey.entity, key);

			String id = Dust.accessEntity(DataCommand.getValue, key,
					EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal), null, null);

			ret.put(GuiEntityKey.id, id);

			return ret;
		}
	};

	public static void loadRefsAndEntities(Collection<GuiEntityInfo> arrTypes, DustUtilsFactory<DustRef, GuiRefInfo> factRefs) {
		arrTypes.clear();
//		arrRefs.clear();

		DustEntity eldPrimaryType = EntityResolver.getEntity(DustDataLinks.EntityPrimaryType);
		DustEntity eldEntityModels = EntityResolver.getEntity(DustDataLinks.EntityModels);

		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustRef ref) {
				GuiRefInfo ri = factRefs.get(ref);

				GuiEntityInfo eiSource = ri.get(GuiRefKey.source);
				GuiEntityInfo eiTarget = ri.get(GuiRefKey.target);

				Object ld = ri.getEntity(GuiRefKey.linkDef);				
				if (ld == eldPrimaryType) {
					eiSource.put(GuiEntityKey.type, eiTarget);
				} else if (ld == eldEntityModels) {
					eiSource.add(GuiEntityKey.models, eiTarget);
				}
			}
		}, null, null, null);

		Map<GuiEntityInfo, DustMetaTypes> mapMeta = new HashMap<>();
		for (DustMetaTypes dmt : DustMetaTypes.values()) {
			mapMeta.put(factEntityInfo.get(EntityResolver.getEntity(dmt)), dmt);
		}
		GuiEntityInfo eiGenericOwner = factEntityInfo.get(EntityResolver.getEntity(DustGenericLinks.Owner));

		Dust.processEntities(new EntityProcessor() {
			@Override
			public void processEntity(Object key, DustEntity entity) {
				GuiEntityInfo ei = factEntityInfo.get(entity);
				GuiEntityInfo eiType = ei.get(GuiEntityKey.type);
				DustMetaTypes dmt = mapMeta.get(eiType);

				GuiEntityKey ekInfo = null;

				if (null != dmt) {
					switch (dmt) {
					case Type:
						arrTypes.add(ei);
						break;
					case AttDef:
						ekInfo = GuiEntityKey.attDefs;
						break;
					case LinkDef:
						ekInfo = GuiEntityKey.linkDefs;
						break;
					}
				}

				if (null != ekInfo) {
					for (GuiRefInfo ri : (Iterable<GuiRefInfo>) ei.get(GuiEntityKey.links)) {
						if (eiGenericOwner == ri.get(GuiRefKey.linkDef)) {
							GuiEntityInfo eiOwnerType = ri.get(GuiRefKey.target);
							eiOwnerType.add(ekInfo, ei);
						}
					}
				}
			}
		});
	}
}
