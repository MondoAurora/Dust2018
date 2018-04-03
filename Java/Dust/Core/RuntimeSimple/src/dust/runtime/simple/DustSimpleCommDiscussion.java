package dust.runtime.simple;

import java.util.ArrayList;
import java.util.HashMap;

import dust.gen.DustUtilsGen;
import dust.gen.knowledge.comm.DustKnowledgeCommServices;
import dust.pub.Dust;
import dust.pub.DustUtilsDev;
import dust.pub.DustUtilsJava;
import dust.utils.DustUtilsFactory;

public class DustSimpleCommDiscussion
		implements DustKnowledgeCommServices, DustKnowledgeCommServices.DustKnowledgeCommDiscussion {

	enum KeyType {
		Unknown, Type, AttDef, LinkDef
	}

	class KeyInfo {
		String id;

		KeyType type = KeyType.Unknown;
		DustEntity eDef;

		public KeyInfo(String id) {
			this.id = id;
		}
		
		@Override
		public String toString() {
			return id;
		}
	}

	class StatementData extends HashMap<KeyInfo, Object> {
		private static final long serialVersionUID = 1L;

		String localId;
		String globalId;
	}

	DustUtilsFactory<String, KeyInfo> factKeyInfo = new DustUtilsFactory<String, KeyInfo>(true) {
		@Override
		protected KeyInfo create(String key, Object... hints) {
			return new KeyInfo(key);
		}
	};

	ArrayList<StatementData> arrStatements = new ArrayList<>();
	StatementData currStatement;
	
	String idGlobalId;
	String idLocalId;

	DustConstKnowledgeCommStatementType getStatementType() {
		DustEntity type = Dust.getRefEntity(DustConstKnowledgeInfoContext.Message, false,
				DustLinkKnowledgeCommStatement.Type, null);
		String state = Dust.getAttrValue(type, DustAttributeToolsGenericIdentified.idLocal);
		DustConstKnowledgeCommStatementType st = DustUtilsJava.parseEnum(state,
				DustConstKnowledgeCommStatementType.class);

		return st;
	}

	private void acceptStatement() {
		idGlobalId = DustUtilsGen.metaToId(DustAttributeKnowledgeCommTerm.idGlobal);
		idLocalId = DustUtilsGen.metaToId(DustAttributeKnowledgeCommTerm.idLocal);
		arrStatements.add(currStatement);
	}

	@Override
	public void dustKnowledgeProcProcessorBegin() throws Exception {
		DustConstKnowledgeCommStatementType st = getStatementType();

		switch (st) {
		case Entity:
			currStatement = new StatementData();
			break;
		default:
			break;
		}

		Integer idx = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message,
				DustAttributeKnowledgeInfoIterator.index);
		String key = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message, DustAttributeKnowledgeInfoIterator.key);

		DustUtilsDev.dump("Receiving Start block...", st,
				(DustConstKnowledgeCommStatementType.Entity == st) ? idx : key);
	}

	@Override
	public void dustKnowledgeProcProcessorEnd() throws Exception {
		DustConstKnowledgeCommStatementType st = getStatementType();
		
		switch (st) {
		case Entity:
			acceptStatement();
			break;
		default:
			break;
		}

		DustUtilsDev.dump("Receiving End block...", st);
	}

	@Override
	public DustConstKnowledgeProcVisitorResponse dustKnowledgeProcVisitorVisit(DustEntity entity) throws Exception {
		String key = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message,
				DustAttributeToolsGenericIdentified.idLocal);
		Object value = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message,
				DustAttributeKnowledgeInfoVariant.value);

		KeyInfo ki = factKeyInfo.get(key);
		currStatement.put(ki, value);

		DustUtilsDev.dump("Receiving value...", key, value);

		return null;
	}

}
