package dust.persistence.jsonsimple;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

import dust.gen.DustComponents.DustEntity;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents.DustAttributeKnowledgeInfoEntity;
import dust.gen.tools.persistence.DustToolsPersistenceComponents.DustStatusToolsPersistence;
import dust.pub.DustException;
import dust.pub.DustUtilsDev;
import dust.pub.DustUtilsJava;
import dust.utils.DustUtilsFactory;

class DustJsonHandler1 implements ContentHandler {
	private static final String CONTENT_HANDLER = "DustJsonReader";
	private static final String CONTENT_VERSION = "1";	

	enum JsonTag {
		Root(null),
		DustStreamInfo(Root), idStore(DustStreamInfo), handler(DustStreamInfo), version(DustStreamInfo),
		Entities(Root), 
		MetaExt(Entities), globalId(MetaExt), alias(MetaExt), ownerType(MetaExt),
		StoreInfo(Entities), idGlobal(StoreInfo), idLocal(StoreInfo),
		Models(Entities);
		
		JsonTag parent;

		private JsonTag(JsonTag parent) {
			this.parent = parent;
		}
		
	};

	int TARGET_NOT_STORED = -1;

		class MetaInfo {
			EnumMap<JsonTag, String> info = new EnumMap<>(JsonTag.class);
			Map<String, MetaInfo> attrMap = new TreeMap<>();
		}

		String storeId;
		Map<Object, DustEntity> mapEntities = new HashMap<>();

		JsonTag state = JsonTag.Root;
		DustUtilsFactory<String, MetaInfo> metaMap = new DustUtilsFactory<String, MetaInfo>(true) {
			@Override
			protected MetaInfo create(String key, Object... hints) {
				return new MetaInfo();
			}
		};

		MetaInfo metaInfo = null;
		MetaInfo metaAtt = null;
		
		String eIdLocal;
		String eIdGlobal;
		
		DustEntity eTarget;

		@Override
		public void startJSON() throws ParseException, IOException {
			 mapEntities.clear();
		}

		@Override
		public void endJSON() throws ParseException, IOException {
		}

		@Override
		public boolean startObject() throws ParseException, IOException {
			switch ( state ) {
			case MetaExt:
				metaInfo = new MetaInfo();
				break;
			case Models:
				if ( null == eTarget ) {
					eTarget = accessEntity(eIdGlobal);
					mapEntities.put(eIdLocal, eTarget);
				} else {
					
				}
				break;
			default:
				break;
			}
			return true;
		}

		@Override
		public boolean endObject() throws ParseException, IOException {
			String alias, owner;
			switch ( state ) {
			case MetaExt:
				alias = metaInfo.info.get(JsonTag.alias);
				owner = metaInfo.info.get(JsonTag.ownerType);
				
				if ( null == owner ) {
					metaMap.get(alias).info.putAll(metaInfo.info);
				} else {
					metaMap.get(owner).attrMap.put(alias, metaInfo);
				}
				metaInfo = null;
				break;
			default:
				break;
			}
			return true;
		}

		@Override
		public boolean startObjectEntry(String key) throws ParseException, IOException {
			if ( JsonTag.Models == state ) {
				if ( null == metaInfo ) {
					metaInfo = metaMap.peek(key);
				} else {
					metaAtt = metaInfo.attrMap.get(key);
				}
			} else {
				state = (JsonTag) DustUtilsJava.parseEnum(key, JsonTag.class);
//				DustUtilsDev.dump(state);
			}
			return true;
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			if ( JsonTag.Models == state ) {
				if ( null == metaAtt) {
					if ( null == metaInfo) {
						eTarget = null;
						state = JsonTag.Entities;
					} else {
						metaInfo = null;
					}
				} else {
					metaAtt = null;
				}
			} else {
				state = state.parent;
			}			
			return true;
		}

		@Override
		public boolean startArray() throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean endArray() throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean primitive(Object value) throws ParseException, IOException {
			try {
				switch ( state ) {
				case idStore:
					storeId = (String) value;
					break;
				case handler:
					if (!DustUtilsJava.isEqual(CONTENT_HANDLER, value)) {
						DustException.throwException(DustStatusToolsPersistence.HandlerInvalid, CONTENT_HANDLER, value);
					}
					break;
				case version:
					if (!DustUtilsJava.isEqual(CONTENT_VERSION, value)) {
						DustException.throwException(DustStatusToolsPersistence.HandlerVersionMismatch, CONTENT_VERSION, value);
					}
					break;
				case alias:
				case globalId:
				case ownerType:
					metaInfo.info.put(state, DustUtilsJava.toString(value));
					break;
				case idGlobal:
					eIdGlobal = DustUtilsJava.toString(value);
					break;
				case idLocal:
					eIdLocal = DustUtilsJava.toString(value);
					break;
				case Models:
					if ( null != metaAtt ) {
						setField(eTarget, metaAtt, value);
					}
					break;
				default:
					break;
				
				}
				// streamData.get(state).put(key, value);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		}
		
		DustEntity accessEntity(String idGlobal) {
			DustUtilsDev.dump("accessing entity", idGlobal);
			return DustAttributeKnowledgeInfoEntity.svcImpl;
		}

		void setField(DustEntity e, MetaInfo mi, Object value) {
			DustUtilsDev.dump("set field value", mi.info.get(JsonTag.globalId), value);
		}

	}