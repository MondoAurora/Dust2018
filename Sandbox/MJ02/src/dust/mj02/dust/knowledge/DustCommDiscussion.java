package dust.mj02.dust.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustCommDiscussion implements DustCommComponents, DustDataComponents, DustMetaComponents {

	private static final Object KEY_INFO = new Object();

	class SourceVocabulary {
		Map<Object, Object> allData;
		Map<Object, Object> idMap = new HashMap<>();

		private Object keyStoreId;
		// private Object keyLocalId;

		private Object keyAttType;
		private Object keyLinkType;
		private Object idAttType;
		private Object idLinkType;

		public SourceVocabulary(Map<Object, Object> allData) {
			this.allData = allData;

			keyStoreId = allData.remove("");
			// keyStoreId = allData.remove(CommKeys.KeyCommIdStore);
			// keyLocalId = allData.remove(CommKeys.KeyCommIdLocal);
		}
	}

	public DustCommDiscussion() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public void load(DustCommSource rdr, Object... sources) throws Exception {
		Set<SourceVocabulary> srcData = new HashSet<>();

		for (Object src : sources) {
			SourceVocabulary sVoc = new SourceVocabulary(rdr.dustCommSourceRead(src));
			srcData.add(sVoc);

			for (Map.Entry<Object, Object> eData : sVoc.allData.entrySet()) {
				Map<Object, Object> in = (Map<Object, Object>) eData.getValue();
				String si = DustUtilsJava.getByPath(in, sVoc.keyStoreId);
				DustEntity e = Dust.getEntity(si);
				Dust.accessEntity(DataCommand.setValue, e, sVoc.keyStoreId, si, null);

				Object knownId = DustKnowledgeGen.resolve(si);
				if (null != knownId) {
					Object li = eData.getKey();
					sVoc.idMap.put(li, si);
					if (DustMetaAtts.AttDefType == knownId) {
						sVoc.keyAttType = li;
						sVoc.idAttType = si;
					} else if (DustMetaAtts.LinkDefType == knownId) {
						sVoc.keyLinkType = li;
						sVoc.idLinkType = si;
					}
				}
			}

			for (Object in : sVoc.allData.values()) {
				Object infoVal = null;
				Object infoId = null;

				Object keyMetaInfo = DustUtilsJava.getByPath(in, sVoc.keyAttType);
				if (null != keyMetaInfo) {
					Object metaInfo = sVoc.allData.get(keyMetaInfo);
					Object metaId = DustUtilsJava.getByPath(metaInfo, sVoc.keyStoreId);
					infoVal = DustKnowledgeGen.resolve(metaId);
					infoId = sVoc.idAttType;
				} else if (null != (keyMetaInfo = DustUtilsJava.getByPath(in, sVoc.keyLinkType))) {
					Object metaInfo = sVoc.allData.get(keyMetaInfo);
					Object metaId = DustUtilsJava.getByPath(metaInfo, sVoc.keyStoreId);
					infoVal = DustKnowledgeGen.resolve(metaId);
					infoId = sVoc.idLinkType;
				}

				if (null != infoVal) {
					Object si = DustUtilsJava.getByPath(in, sVoc.keyStoreId);
					DustEntity entity = Dust.getEntity(si);

					Dust.accessEntity(DataCommand.setValue, entity, KEY_INFO, infoVal, null);
					Dust.accessEntity(DataCommand.setValue, entity, infoId, infoVal, null);
				}
			}
		}

		for (SourceVocabulary sd : srcData) {
			DustUtilsFactory<Object, DustEntity> attRefs = new DustUtilsFactory<Object, DustEntity>(true) {
				@Override
				protected DustEntity create(Object key, Object... hints) {
					Object localAtt = sd.allData.get(key);
					String si = DustUtilsJava.getByPath(localAtt, sd.keyStoreId);
					return Dust.getEntity(si);
				}
			};

			for (Object o : sd.allData.values()) {
				String si = DustUtilsJava.getByPath(o, sd.keyStoreId);
				DustEntity entity = Dust.getEntity(si);

				DustUtilsDev.dump("Loading entity data", si);

				if ("Knowledge:Meta:Type".equals(si)) {
					DustUtilsDev.dump("hopp");
				}

				for (Map.Entry<Object, Object> eAtts : ((Map<Object, Object>) o).entrySet()) {
					DustEntity attDef = attRefs.get(eAtts.getKey());

					Object infoStoreId = Dust.accessEntity(DataCommand.getValue, attDef, sd.keyStoreId, null, null);
					Object info = Dust.accessEntity(DataCommand.getValue, attDef, KEY_INFO, null, null);

					Object value = eAtts.getValue();

					if (info instanceof DustMetaValueAttDefType) {
						if (null != value) {
							switch ((DustMetaValueAttDefType) info) {
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

							Dust.accessEntity(DataCommand.setValue, entity, infoStoreId, value, null);
						}
					} else if (info instanceof DustMetaValueLinkDefType) {
						switch ((DustMetaValueLinkDefType) info) {
						case LinkDefSingle:
							value = resolveEntity(sd, value);
							Dust.accessEntity(DataCommand.setRef, entity, infoStoreId, value, null);
							break;
						case LinkDefArray:
						case LinkDefSet:
							for (Object lk : (Collection<Object>) value) {
								value = resolveEntity( sd, lk);
								Dust.accessEntity(DataCommand.setRef, entity, infoStoreId, value, null);
							}
							break;
						case LinkDefMap:
							for (Map.Entry<Object, Object> ee : ((Map<Object, Object>) value).entrySet()) {
								Dust.accessEntity(DataCommand.setRef, entity, infoStoreId,
										resolveEntity( sd, ee.getValue()), ee.getKey());
							}
							break;
						}
					}

					DustUtilsDev.dump("  set att", infoStoreId, "to", value);
				}
			}
		}

		DustUtilsDev.dump("Finished loading data");
	}

	private Object resolveEntity(SourceVocabulary sd, Object value) {
		Object src = sd.allData.get(value);
		Object idSrc = DustUtilsJava.getByPath(src, sd.keyStoreId);
		return Dust.getEntity(idSrc);
	}

}
