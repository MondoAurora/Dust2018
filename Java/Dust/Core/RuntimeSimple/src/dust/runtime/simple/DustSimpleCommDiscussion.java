package dust.runtime.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import dust.gen.DustUtilsGen;
import dust.gen.knowledge.comm.DustKnowledgeCommComponents;
import dust.pub.Dust;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustSimpleCommDiscussion implements DustKnowledgeCommComponents,
		DustKnowledgeCommComponents.DustKnowledgeCommDiscussion, DustSimpleRuntimeComponents {

	DustEntity tempFound = new DustEntity() {
	};

	enum KeyType {
		Unknown, Type, AttDef, LinkDef
	}

	class KeyInfo {
		String id;

		KeyType type = KeyType.Unknown;
		StatementData sd;

		public KeyInfo(String id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return id;
		}
	}

	class StatementData extends DustUtilsFactory<String, HashMap<KeyInfo, Object>> {
		public StatementData() {
			super(true);
		}

		String idSource;
		String idLocal;
		String idType;

		DustEntity e;
		DustTypeKnowledgeMeta mt;

		void init(String storeId, String localId, String typeId) {
			this.idSource = storeId;
			this.idLocal = localId;
			this.idType = typeId;
		}

		DustEntity getEntity() throws Exception {
			if (null == e) {
				DustEntity eType = idType.equals(idLocal) ? null : (DustType) factKeyInfo.get(idType).sd.getEntity();
				e = localData.dustKnowledgeInfoSourceGet(eType, idSource);
			}

			return e;
		}

		DustTypeKnowledgeMeta getMetaType() {
			if (null == mt) {
				String ts = factKeyInfo.get(idType).sd.idSource;
				if (getStoreId(DustTypeKnowledgeMeta.AttDef).equals(ts)) {
					mt = DustTypeKnowledgeMeta.AttDef;
				} else if (getStoreId(DustTypeKnowledgeMeta.LinkDef).equals(ts)) {
					mt = DustTypeKnowledgeMeta.LinkDef;
				}
			}

			return mt;
		}

		boolean isMatched() {
			return null != e;
		}

		@Override
		protected HashMap<KeyInfo, Object> create(String key, Object... hints) {
			return new HashMap<>();
		}

		void setValue(String key, Object value) {
			HashMap<KeyInfo, Object> valMap = get(modelKey);
			KeyInfo ki = factKeyInfo.get(key);
			valMap.put(ki, value);
		}

		@Override
		public String toString() {
			return "Entity " + idType + "|" + idSource + " as local id " + idLocal;
		}
	}

	DustUtilsFactory<String, KeyInfo> factKeyInfo = new DustUtilsFactory<String, KeyInfo>(true) {
		@Override
		protected KeyInfo create(String key, Object... hints) {
			return new KeyInfo(key);
		}
	};

	ArrayList<StatementData> arrStatements = new ArrayList<>();
	StatementData currStatement;
	String modelKey;
	Object dataColl;
	DustConstKnowledgeMetaCardinality dataCard = DustConstKnowledgeMetaCardinality.Single;

	String keyTerm = null;
	KeyInfo keyLocal = null;
	KeyInfo keyStore = null;

	String keyEntity = null;
	KeyInfo keyPrimaryType = null;

	DustSimpleManagerData localData = new DustSimpleManagerData();
	DustSimpleManagerLink localRefs = new DustSimpleManagerLink();

	@Override
	public void dustKnowledgeProcProcessorBegin() throws Exception {
		DustConstKnowledgeCommStatementType st = getStatementType();
		String key = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message, DustAttributeKnowledgeInfoIterator.key);

		switch (st) {
		case Entity:
			currStatement = new StatementData();
			break;
		case Model:
			modelKey = key;
			break;
		case Data:
			dataCard = getMsgConst(DustLinkKnowledgeInfoIterator.Cardinality, DustConstKnowledgeMetaCardinality.class);
			switch (dataCard) {
			case Array:
				dataColl = new ArrayList<Object>();
				break;
			case Map:
				dataColl = new HashMap<String, Object>();
				break;
			case Set:
				dataColl = new HashSet<Object>();
				break;
			case Single:
				break;
			}
			currStatement.setValue(key, dataColl);
			break;
		default:
			break;
		}
	}

	@Override
	public void dustKnowledgeProcProcessorEnd() throws Exception {
		DustConstKnowledgeCommStatementType st = getStatementType();

		switch (st) {
		case Data:
			dataColl = null;
			dataCard = DustConstKnowledgeMetaCardinality.Single;
			break;
		case Model:
			modelKey = null;
			break;
		case Entity:
			acceptStatement();
			break;
		case Discussion:
			identifyCoreTerms();
			processStatements();
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public DustConstKnowledgeProcVisitorResponse dustKnowledgeProcVisitorVisit(DustEntity entity) throws Exception {
		String key = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message,
				DustAttributeToolsGenericIdentified.idLocal);
		Object value = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message,
				DustAttributeKnowledgeInfoVariant.value);

		switch (dataCard) {
		case Map:
			((Map<String, Object>) dataColl).put(key, value);
			break;
		case Array:
		case Set:
			((Collection<Object>) dataColl).add(value);
			break;
		case Single:
			currStatement.setValue(key, value);
			break;
		}

		return null;
	}

	private DustConstKnowledgeCommStatementType getStatementType() {
		DustEntity type = Dust.getRefEntity(DustConstKnowledgeInfoContext.Message, false,
				DustLinkKnowledgeCommStatement.Type, null);
		String state = Dust.getAttrValue(type, DustAttributeToolsGenericIdentified.idLocal);
		DustConstKnowledgeCommStatementType st = DustUtilsJava.parseEnum(state,
				DustConstKnowledgeCommStatementType.class);

		return st;
	}

	private <RetType extends Enum<RetType>> RetType getMsgConst(DustEntity link, Class<RetType> rc) {
		DustEntity type = Dust.getRefEntity(DustConstKnowledgeInfoContext.Message, false, link, null);
		String str = Dust.getAttrValue(type, DustAttributeToolsGenericIdentified.idLocal);
		RetType st = DustUtilsJava.parseEnum(str, rc);

		return st;
	}

	private void acceptStatement() {
		arrStatements.add(currStatement);
	}

	private String getStoreId(IdentifiableMeta meta) {
		return DustUtilsGen.metaToStoreId(meta);
	}

	private void identifyCoreTerms() {
		// First, what is your word for "Term"?
		String idTermType = getStoreId(DustTypeKnowledgeComm.Term);
		for (StatementData sd : arrStatements) {
			boolean selfId = false;
			boolean term = false;
			for (String tk : sd.keys()) {
				for (Map.Entry<KeyInfo, Object> fields : sd.peek(tk).entrySet()) {
					Object value = fields.getValue();
					if (tk.equals(value)) {
						selfId = true;
					} else if (idTermType.equals(value)) {
						term = true;
					}
				}

				if (selfId && term) {
					// sd.e = tempFound;
					keyTerm = tk;
					break;
				}
			}
		}

		// What is the localID for local and store id in Term?
		String idStoreId = getStoreId(DustAttributeKnowledgeCommTerm.idStore);
		String idLocalId = getStoreId(DustAttributeKnowledgeCommTerm.idLocal);
		for (StatementData sd : arrStatements) {
			boolean si = false;
			boolean li = false;
			Object val = null;
			for (Map.Entry<KeyInfo, Object> fields : sd.peek(keyTerm).entrySet()) {
				Object value = fields.getValue();
				if (idStoreId.equals(value)) {
					si = true;
				} else if (idLocalId.equals(value)) {
					li = true;
				} else {
					val = value;
				}
			}

			if (li) {
				keyLocal = factKeyInfo.get((String) val);
			}
			if (si) {
				keyStore = factKeyInfo.get((String) val);
			}
		}

		// Register all statements with their loclId and find what is the localID for
		// Entity and PrimaryType?
		String idEntityType = getStoreId(DustTypeKnowledgeInfo.Entity);
		String idPrimaryType = getStoreId(DustLinkKnowledgeInfoEntity.PrimaryType);
		for (StatementData sd : arrStatements) {
			Map<KeyInfo, Object> termData = sd.peek(keyTerm);
			Object si = termData.get(keyStore);
			String li = (String) termData.get(keyLocal);
			if (idEntityType.equals(si)) {
				keyEntity = li;
			} else if (idPrimaryType.equals(si)) {
				keyPrimaryType = factKeyInfo.get(li);
			}
			factKeyInfo.get(li).sd = sd;
		}

		for (StatementData sd : arrStatements) {
			Map<KeyInfo, Object> termData = sd.peek(keyTerm);
			Map<KeyInfo, Object> entityData = sd.peek(keyEntity);
			sd.init((String) termData.get(keyStore), (String) termData.get(keyLocal),
					(String) entityData.get(keyPrimaryType));
		}

	}

	private void processStatements() throws Exception {
		for (StatementData sd : arrStatements) {
			InfoEntity eTarget = (InfoEntity) sd.getEntity();
			DustUtilsDev.dump("Reading", factKeyInfo.get(sd.idType).sd.idSource, ":", eTarget);
			for (String tk : sd.keys()) {
				if (!keyTerm.equals(tk) && !keyEntity.equals(tk)) {
					for (Map.Entry<KeyInfo, Object> fields : sd.peek(tk).entrySet()) {
						StatementData sdKey = factKeyInfo.get(fields.getKey().id).sd;
						Object value = fields.getValue();
						InfoEntity refTarget;
						int idx;

						switch (sdKey.getMetaType()) {
						case AttDef:
							SimpleAttDef att = (SimpleAttDef) sdKey.getEntity();
							DustUtilsDev.dump("set field", att, "to", value);
							eTarget.setFieldValue(att, value);
							break;
						case LinkDef:
							SimpleLinkDef link = (SimpleLinkDef) sdKey.getEntity();
							if (value instanceof String) {
								refTarget = (InfoEntity) factKeyInfo.get((String) value).sd.getEntity();
								DustUtilsDev.dump("set ref", link, "to", refTarget);
								localRefs.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, eTarget, refTarget, link);
							} else if (value instanceof List<?>) {
								idx = 0;
								for (Object v : (List<?>) value) {
									refTarget = (InfoEntity) factKeyInfo.get((String) v).sd.getEntity();
									DustUtilsDev.dump("add array ref", link, "to", refTarget);
									localRefs.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, eTarget, refTarget,
											link, ++idx);
								}
							} else if (value instanceof Map<?, ?>) {
								for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
									refTarget = (InfoEntity) factKeyInfo.get((String) e.getValue()).sd.getEntity();
									Object key = e.getKey();
									DustUtilsDev.dump("set map ref", link, key, "=", refTarget);
									localRefs.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, eTarget, refTarget,
											link, key);
								}
							}
							break;
						default:
							break;
						}
					}
				}
			}
		}
	}
}
