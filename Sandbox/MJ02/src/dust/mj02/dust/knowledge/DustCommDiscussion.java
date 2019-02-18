package dust.mj02.dust.knowledge;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.tools.DustGenericComponents.DustGenericLinks;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsJava;

public class DustCommDiscussion implements DustCommComponents, DustDataComponents, DustMetaComponents {
	
	enum TempKey {
		entity, contentType
	}

	class SourceVocabulary {
		Map<Object, Object> allData;

		private Object keyStoreId;

		private Object keyAttType;
		private DustEntity eAttType;

		private Object keyLinkType;
		private DustEntity eLinkType;

		public SourceVocabulary(Map<Object, Object> allData) {
			this.allData = allData;
			keyStoreId = allData.remove("");
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
			Object keyOwner = null;
			DustEntity eOwner = null;

			for (Map.Entry<Object, Object> eData : sVoc.allData.entrySet()) {
				Object li = eData.getKey();
				Map<Object, Object> in = (Map<Object, Object>) eData.getValue();
				
				String si = DustUtilsJava.getByPath(in, sVoc.keyStoreId);
				DustEntity e = Dust.getEntity(si);
				in.put(TempKey.entity, e);

				Object knownId = EntityResolver.getKey(e);
				if (null != knownId) {
					if (DustMetaLinks.AttDefType == knownId) {
						sVoc.keyAttType = li;
						sVoc.eAttType = e;
					} else if (DustMetaLinks.LinkDefType == knownId) {
						sVoc.keyLinkType = li;
						sVoc.eLinkType = e;
					} else if (DustGenericLinks.Owner == knownId) {
						keyOwner = eData.getKey();
						eOwner = e;
					}
				}
			}

			for (Object in : sVoc.allData.values()) {
				DustEntity eInfo = null;
				DustEntity eInfoVal = null;

				Object keyMetaInfo = DustUtilsJava.getByPath(in, sVoc.keyAttType);
				if (null != keyMetaInfo) {
					eInfo = sVoc.eAttType;
				} else if (null != (keyMetaInfo = DustUtilsJava.getByPath(in, sVoc.keyLinkType))) {
					eInfo = sVoc.eLinkType;
				}

				if (null != keyMetaInfo) {
					Object metaInfo = sVoc.allData.get(keyMetaInfo);
					Object metaId = DustUtilsJava.getByPath(metaInfo, sVoc.keyStoreId);
					eInfoVal = Dust.getEntity(metaId);

					Object si = DustUtilsJava.getByPath(in, sVoc.keyStoreId);
					DustEntity entity = Dust.getEntity(si);

					Dust.accessEntity(DataCommand.setRef, entity, eInfo, eInfoVal, null);
					
					Object typeId = DustUtilsJava.getByPath(in, keyOwner);
					Object inType = sVoc.allData.get(typeId);
					Object sidType = DustUtilsJava.getByPath(inType, sVoc.keyStoreId);
					DustEntity eType = Dust.getEntity(sidType);
					
					Dust.accessEntity(DataCommand.setRef, entity, eOwner, eType, null);					
					((Map<Object, Object>) in).put(TempKey.contentType, EntityResolver.getKey(eInfoVal));
				}
			}
		}

		for (SourceVocabulary sd : srcData) {
			for (Object o : sd.allData.values()) {
				String si = DustUtilsJava.getByPath(o, sd.keyStoreId);
				DustEntity entity = Dust.getEntity(si);

				DustUtilsDev.dump("Loading entity data", si);

				for (Map.Entry<Object, Object> eAtts : ((Map<Object, Object>) o).entrySet()) {
					Object ck = eAtts.getKey();
					if ( ck instanceof TempKey) {
						continue;
					}
					Map<Object, Object> contentInfo = (Map<Object, Object>) sd.allData.get(ck);
					DustEntity eContent = (DustEntity) contentInfo.get(TempKey.entity);

					Object info = contentInfo.get(TempKey.contentType);

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

							Dust.accessEntity(DataCommand.setValue, entity, eContent, value, null);
						}
					} else if (info instanceof DustMetaValueLinkDefType) {
						switch ((DustMetaValueLinkDefType) info) {
						case LinkDefSingle:
							value = resolveEntity(sd, value);
							Dust.accessEntity(DataCommand.setRef, entity, eContent, value, null);
							break;
						case LinkDefArray:
						case LinkDefSet:
							for (Object lk : (Collection<Object>) value) {
								value = resolveEntity( sd, lk);
								Dust.accessEntity(DataCommand.setRef, entity, eContent, value, null);
							}
							break;
						case LinkDefMap:
							for (Map.Entry<Object, Object> ee : ((Map<Object, Object>) value).entrySet()) {
								Dust.accessEntity(DataCommand.setRef, entity, eContent,
										resolveEntity( sd, ee.getValue()), ee.getKey());
							}
							break;
						}
					}

					DustUtilsDev.dump("  set", (info instanceof DustMetaValueAttDefType) ? "att" : "ref", ck, "to", value);
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
