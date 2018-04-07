package dust.runtime.simple;

import java.util.ArrayList;
import java.util.HashMap;
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
		@Override
		public DustType getType() {
			return null;
		}
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

		String gid, lid;
		DustEntity e;

		void init(String globalId, String localId) throws Exception {
			this.gid = globalId;
			this.lid = localId;

			KeyInfo ki = factKeyInfo.peek(localId);
			if (null != ki) {
				ki.sd = this;
			}
			
//			e = localData.dustKnowledgeInfoSourceGet(globalId);
		}

		@Override
		protected HashMap<KeyInfo, Object> create(String key, Object... hints) {
			return new HashMap<>();
		}

		@Override
		public String toString() {
			return "Entity " + gid + " as local id " + lid;
		}
	}

	DustUtilsFactory<String, KeyInfo> factKeyInfo = new DustUtilsFactory<String, KeyInfo>(true) {
		@Override
		protected KeyInfo create(String key, Object... hints) {
			return new KeyInfo(key);
		}
	};

	ArrayList<StatementData> arrStatements = new ArrayList<>();
	String modelKey;
	StatementData currStatement;

	String keyTerm = null;
	KeyInfo keyLocal = null;
	KeyInfo keyGlobal = null;

	DustSimpleManagerData localData = new DustSimpleManagerData();
	
	public DustSimpleCommDiscussion() {
		localData.setMeta(DustSimpleRuntime.mgrMeta);
	}
	
	private DustConstKnowledgeCommStatementType getStatementType() {
		DustEntity type = Dust.getRefEntity(DustConstKnowledgeInfoContext.Message, false,
				DustLinkKnowledgeCommStatement.Type, null);
		String state = Dust.getAttrValue(type, DustAttributeToolsGenericIdentified.idLocal);
		DustConstKnowledgeCommStatementType st = DustUtilsJava.parseEnum(state,
				DustConstKnowledgeCommStatementType.class);

		return st;
	}

	private void acceptStatement() {
		arrStatements.add(currStatement);
	}

	private void identifyCoreTerms() {
		String idGlobalId = DustUtilsGen.metaToId(DustAttributeKnowledgeCommTerm.idGlobal);
		String idLocalId = DustUtilsGen.metaToId(DustAttributeKnowledgeCommTerm.idLocal);
		String idTermType = DustUtilsGen.metaToId(DustTypeKnowledgeComm.Term);

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
			boolean gi = false;
			boolean li = false;
			Object val = null;
			for (Map.Entry<KeyInfo, Object> fields : sd.peek(keyTerm).entrySet()) {
				Object value = fields.getValue();
				if (idGlobalId.equals(value)) {
					gi = true;
				} else if (idLocalId.equals(value)) {
					li = true;
				} else {
					val = value;
				}
			}

			if (li) {
				keyLocal = factKeyInfo.get((String) val);
			}
			if (gi) {
				keyGlobal = factKeyInfo.get((String) val);
			}
		}
	}

	private void processStatements() throws Exception {
		for (StatementData sd : arrStatements) {
			Map<KeyInfo, Object> termData = sd.peek(keyTerm);
			sd.init((String) termData.get(keyGlobal), (String) termData.get(keyLocal));
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
		default:
			break;
		}

//		Integer idx = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message,
//				DustAttributeKnowledgeInfoIterator.index);
//
//		DustUtilsDev.dump("Receiving Start block...", st,
//				(DustConstKnowledgeCommStatementType.Entity == st) ? idx : key);
	}

	@Override
	public void dustKnowledgeProcProcessorEnd() throws Exception {
		DustConstKnowledgeCommStatementType st = getStatementType();

		switch (st) {
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

//		DustUtilsDev.dump("Receiving End block...", st);
	}

	@Override
	public DustConstKnowledgeProcVisitorResponse dustKnowledgeProcVisitorVisit(DustEntity entity) throws Exception {
		String key = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message,
				DustAttributeToolsGenericIdentified.idLocal);
		Object value = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message,
				DustAttributeKnowledgeInfoVariant.value);

		KeyInfo ki = factKeyInfo.get(key);
		currStatement.get(modelKey).put(ki, value);

//		DustUtilsDev.dump("Receiving value...", key, value);

		return null;
	}

}
