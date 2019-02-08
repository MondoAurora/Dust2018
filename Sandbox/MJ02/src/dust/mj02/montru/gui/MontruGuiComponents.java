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

	enum EntityKey {
		entity, type, models, id, owner, attDefs, linkDefs, links, title, panel
	}

	enum RefKey {
		source, target, linkDef, key, selected
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
			if (null == val) {
				return;
			}
			Set<Object> cont = (Set<Object>) content.get(key);
			if (null == cont) {
				content.put(key, cont = new HashSet<>());
			}
			cont.add(val);
		}
		
		public boolean isTrue(NodeKey k) {
			return Boolean.TRUE.equals(get(k));
		}
	}

	class RefInfo extends NodeInfo<RefKey> {
		public RefInfo(DustEntity source, DustEntity linkDef, DustEntity target, Object key) {
			super(RefKey.class);

			put(RefKey.source, factEntityInfo.get(source));
			put(RefKey.target, factEntityInfo.get(target));
			put(RefKey.linkDef, factEntityInfo.get(linkDef));

			put(RefKey.key, (key instanceof DustEntity) ? factEntityInfo.get((DustEntity) key) : key);
		}		
	}

	class EntityInfo extends NodeInfo<EntityKey> {
		public EntityInfo() {
			super(EntityKey.class);
			put(EntityKey.links, new HashSet<RefInfo>());
		}

		public String getTitle() {
			String title = get(EntityKey.title);

			if (null == title) {
				EntityInfo tt = get(EntityKey.type);
				title = DustUtilsJava
						.sbAppend(null, " ", true, (null == tt) ? "" : tt.get(EntityKey.id), ":", get(EntityKey.id))
						.toString();
				put(EntityKey.title, title);
			}

			return title;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(getTitle()).append(" [");

			DustEntity entity = get(EntityKey.entity);
			DustUtilsFactory<Object, StringBuilder> factRefs = new DustUtilsFactory<Object, StringBuilder>(false) {
				@Override
				protected StringBuilder create(Object key, Object... hints) {
					return new StringBuilder();
				}
			};

			for (RefInfo ri : (Iterable<RefInfo>) get(EntityKey.links)) {
				factRefs.get(ri.get(RefKey.linkDef)).append(((EntityInfo) ri.get(RefKey.target)).getTitle())
						.append(", ");
			}

			for (EntityInfo m : (Iterable<EntityInfo>) get(EntityKey.models)) {
				DustUtilsJava.sbAppend(sb, " ", true, m.get(EntityKey.id), ": {");

				Iterable<EntityInfo> it = (Iterable<EntityInfo>) m.get(EntityKey.attDefs);
				if (null != it) {
					for (EntityInfo ad : it) {
						DustUtilsJava.sbAppend(sb, " ", true, ad.get(EntityKey.id), "=",
								Dust.accessEntity(DataCommand.getValue, entity, ad.get(EntityKey.entity), null, null),
								", ");
					}
				}

				it = (Iterable<EntityInfo>) m.get(EntityKey.linkDefs);
				if (null != it) {
					for (EntityInfo ld : it) {
						DustUtilsJava.sbAppend(sb, " ", true, ld.get(EntityKey.id), " -> {", factRefs.peek(ld), "}, ");
					}
				}

				sb.append("}");
			}

			sb.append("]");

			return sb.toString();
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

	public static void loadRefsAndEntities(Collection<EntityInfo> arrTypes, Collection<RefInfo> arrRefs) {
		arrTypes.clear();
		arrRefs.clear();

		DustEntity eldPrimaryType = EntityResolver.getEntity(DustDataLinks.EntityPrimaryType);
		DustEntity eldEntityModels = EntityResolver.getEntity(DustDataLinks.EntityModels);

		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustEntity source, DustEntity linkDef, DustEntity target, Object key) {
				RefInfo ri = new RefInfo(source, linkDef, target, key);
				arrRefs.add(ri);

				EntityInfo eiObj = factEntityInfo.get(source);
				((Set<RefInfo>) eiObj.get(EntityKey.links)).add(ri);

				if (linkDef == eldPrimaryType) {
					eiObj.put(EntityKey.type, factEntityInfo.get(target));
				} else if (linkDef == eldEntityModels) {
					eiObj.add(EntityKey.models, factEntityInfo.get(target));
				}
			}
		}, null, null, null);

		Map<EntityInfo, DustMetaTypes> mapMeta = new HashMap<>();
		for (DustMetaTypes dmt : DustMetaTypes.values()) {
			mapMeta.put(factEntityInfo.get(EntityResolver.getEntity(dmt)), dmt);
		}
		EntityInfo eiGenericOwner = factEntityInfo.get(EntityResolver.getEntity(DustGenericLinks.Owner));

		Dust.processEntities(new EntityProcessor() {
			@Override
			public void processEntity(Object key, DustEntity entity) {
				EntityInfo ei = factEntityInfo.get(entity);
				EntityInfo eiType = ei.get(EntityKey.type);
				DustMetaTypes dmt = mapMeta.get(eiType);

				EntityKey ekInfo = null;

				if (null != dmt) {
					switch (dmt) {
					case Type:
						arrTypes.add(ei);
						break;
					case AttDef:
						ekInfo = EntityKey.attDefs;
						break;
					case LinkDef:
						ekInfo = EntityKey.linkDefs;
						break;
					}
				}

				if (null != ekInfo) {
					for (RefInfo ri : (Iterable<RefInfo>) ei.get(EntityKey.links)) {
						if (eiGenericOwner == ri.get(RefKey.linkDef)) {
							EntityInfo eiOwnerType = ri.get(RefKey.target);
							eiOwnerType.add(ekInfo, ei);
						}
					}
				}
			}
		});
	}
}
