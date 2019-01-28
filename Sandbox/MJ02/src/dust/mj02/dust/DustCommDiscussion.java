package dust.mj02.dust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustCommDiscussion implements DustCommComponents {

	private static final Object KEY_INFO = new Object();

	class SourceVocabulary {
		Map<Object, Object> allData;
		Map<Object, Object> idMap = new HashMap<>();

		private Object keyStoreId;
		// private Object keyLocalId;

		private Object keyAttType;
		private Object keyLinkType;

		public SourceVocabulary(Map<Object, Object> allData) {
			this.allData = allData;

			keyStoreId = allData.remove("");
			// keyStoreId = allData.remove(CommKeys.KeyCommIdStore);
			// keyLocalId = allData.remove(CommKeys.KeyCommIdLocal);
		}
	}
	
	class TempEntity {
		Map<Object, Object> content = new HashMap<>();
		
		public Object put(Object key, Object value) {
			return content.put(key, value);
		}

		public Object get(Object key) {
			return content.get(key);
		}
	}

	DustUtilsFactory<Object, TempEntity> entities = new DustUtilsFactory<Object, TempEntity>(true) {
		@Override
		protected TempEntity create(Object key, Object... hints) {
			DustUtilsDev.dump("Creating entity", key);
			return new TempEntity();
		}
	};

	public DustCommDiscussion() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public void load(SourceReader rdr, Object... sources) throws Exception {
		Set<SourceVocabulary> srcData = new HashSet<>();

		for (Object src : sources) {
			SourceVocabulary sVoc = new SourceVocabulary(rdr.load(src));
			srcData.add(sVoc);

			for (Map.Entry<Object, Object> eData : sVoc.allData.entrySet()) {
				Map<Object, Object> in = (Map<Object, Object>) eData.getValue();
				String si = DustUtilsJava.getByPath(in, sVoc.keyStoreId);
				TempEntity e = entities.get(si);
				e.put(sVoc.keyStoreId, si);

				Object knownId = DustCommGen.resolve(si);
				if (null != knownId) {
					Object li = eData.getKey();
					sVoc.idMap.put(li, si);
					if (CommDiscKeys.AttDefType == knownId) {
						sVoc.keyAttType = li;
					} else if (CommDiscKeys.LinkDefType == knownId) {
						sVoc.keyLinkType = li;
					}
				}
			}

			for (Object in : sVoc.allData.values()) {
				Object infoVal = null;

				Object keyMetaInfo = DustUtilsJava.getByPath(in, sVoc.keyAttType);
				if (null != keyMetaInfo) {
					Object metaInfo = sVoc.allData.get(keyMetaInfo);
					Object metaId = DustUtilsJava.getByPath(metaInfo, sVoc.keyStoreId);
					infoVal = DustCommGen.resolve(metaId);
				} else if (null != (keyMetaInfo = DustUtilsJava.getByPath(in, sVoc.keyLinkType))) {
					Object metaInfo = sVoc.allData.get(keyMetaInfo);
					Object metaId = DustUtilsJava.getByPath(metaInfo, sVoc.keyStoreId);
					infoVal = DustCommGen.resolve(metaId);
				}

				if (null != infoVal) {
					Object si = DustUtilsJava.getByPath(in, sVoc.keyStoreId);
					TempEntity entity = entities.get(si);

					entity.put(KEY_INFO, infoVal);
				}
			}
		}

		for (SourceVocabulary sd : srcData) {
			DustUtilsFactory<Object, TempEntity> attRefs = new DustUtilsFactory<Object, TempEntity>(
					true) {
				@Override
				protected TempEntity create(Object key, Object... hints) {
					Object localAtt = sd.allData.get(key);
					String si = DustUtilsJava.getByPath(localAtt, sd.keyStoreId);
					return entities.get(si);
				}
			};

			for (Object o : sd.allData.values()) {
				String si = DustUtilsJava.getByPath(o, sd.keyStoreId);
				TempEntity entity = entities.get(si);

				DustUtilsDev.dump("Loading entity data", si);
				
				if ( "Knowledge:Meta:Type".equals(si) ) {
					DustUtilsDev.dump("hopp");
				}

				for (Map.Entry<Object, Object> eAtts : ((Map<Object, Object>) o).entrySet()) {
					TempEntity attDef = attRefs.get(eAtts.getKey());

					Object infoStoreId = DustUtilsJava.getByPath(attDef.content, sd.keyStoreId);
					Object info = attDef.get(KEY_INFO);

					Object value = eAtts.getValue();

					if (info instanceof CommAttDefTypes) {
						if (null != value) {
							switch ((CommAttDefTypes) info) {
							case AttDefBool:
								if (!(value instanceof Boolean)) {
									throw new DustException("Invalid type");
								}
								break;
							case AttDefFloat:
								break;
							case AttDefIdentifier:
								break;
							case AttDefInteger:
								break;
							}
						}
					} else if (info instanceof CommLinkDefTypes) {
						switch ((CommLinkDefTypes) info) {
						case LinkDefSingle:
							value = resolveEntity(sd, value);
							break;
						case LinkDefArray:
						case LinkDefSet:
							Collection<Object> c = (Collection<Object>) value;
							if ((null != c) && !c.isEmpty()) {
								Collection<Object> target = (CommLinkDefTypes.LinkDefArray == info) ? new ArrayList<>()
										: new HashSet<>();
								for (Object lk : c) {
									target.add(resolveEntity(sd, lk));
								}
								value = target;
							}
							break;
						case LinkDefMap:
							Map<Object, Object> m = (Map<Object, Object>) value;
							if ((null != m) && !m.isEmpty()) {
								Map<Object, Object> target = new HashMap<>();
								for (Map.Entry<Object, Object> ee : ((Map<Object, Object>) o).entrySet()) {
									target.put(ee.getKey(), resolveEntity(sd, ee.getValue()));
								}
								value = target;
							}
							break;
						}
					}

					DustUtilsDev.dump("  set att", infoStoreId, "to", value);

					if (null != value) {
						entity.put(infoStoreId, value);
					}
				}
			}
		}
		
		DustUtilsDev.dump("Finished loading data");
	}

	private Object resolveEntity(SourceVocabulary sd, Object value) {
		Object src = sd.allData.get(value);
		Object idSrc = DustUtilsJava.getByPath(src, sd.keyStoreId);
		return entities.get(idSrc);
	}

}
