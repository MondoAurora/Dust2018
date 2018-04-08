package dust.runtime.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import dust.gen.DustUtilsGen;
import dust.gen.knowledge.comm.DustKnowledgeCommServices;
import dust.pub.Dust;
import dust.pub.DustUtilsDev;
import dust.pub.DustUtilsJava;
import dust.utils.DustUtilsFactory;

public class DustSimpleCommDiscussion
		implements DustKnowledgeCommServices, DustKnowledgeCommServices.DustKnowledgeCommDiscussion {

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

		void init(String globalId, String localId, String typeId) throws Exception {
			this.idSource = globalId;
			this.idLocal = localId;
			this.idType = typeId;

			KeyInfo ki = factKeyInfo.peek(localId);
			if (null != ki) {
				ki.sd = this;
			}

			// e = localData.dustKnowledgeInfoSourceGet(globalId);
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
		
		switch ( dataCard ) {
		case Map:
			((Map<String, Object>)dataColl).put(key, value);
			break;
		case Array:
		case Set:
			((Collection<Object>)dataColl).add(value);
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
	
	private <RetType extends Enum<RetType>> RetType getMsgConst(DustLink link, Class<RetType> rc) {
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
		String idTermType = getStoreId(DustTypeKnowledgeComm.Term);
		String idStoreId = getStoreId(DustAttributeKnowledgeCommTerm.idStore);
		String idLocalId = getStoreId(DustAttributeKnowledgeCommTerm.idLocal);

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
					sd.e = tempFound;
					keyTerm = tk;
					break;
				}
			}
		}

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

		String idEntityType = getStoreId(DustTypeKnowledgeInfo.Entity);
		String idPrimaryType = getStoreId(DustLinkKnowledgeInfoEntity.PrimaryType);

		for (StatementData sd : arrStatements) {
			Map<KeyInfo, Object> termData = sd.peek(keyTerm);
			if (idEntityType.equals(termData.get(keyStore))) {
				keyEntity = (String) termData.get(keyLocal);
			} else if (idPrimaryType.equals(termData.get(keyStore))) {
				keyPrimaryType = factKeyInfo.get((String) termData.get(keyLocal));
			}
		}
	}

	private void processStatements() throws Exception {
		for (StatementData sd : arrStatements) {
			Map<KeyInfo, Object> termData = sd.peek(keyTerm);
			Map<KeyInfo, Object> entityData = sd.peek(keyEntity);
			sd.init((String) termData.get(keyStore), (String) termData.get(keyLocal),
					(String) entityData.get(keyPrimaryType));
			DustUtilsDev.dump("Reading", sd);
			for (String tk : sd.keys()) {
				if (!keyTerm.equals(tk)) {
					for (Map.Entry<KeyInfo, Object> fields : sd.peek(tk).entrySet()) {
						DustUtilsDev.dump("set field or ref", fields.getKey().id, "to", fields.getValue());
					}
				}
			}
		}
	}
}
