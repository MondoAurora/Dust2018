package dust.runtime.simple;

import dust.gen.knowledge.comm.DustKnowledgeCommServices;
import dust.pub.Dust;
import dust.pub.DustUtilsDev;
import dust.pub.DustUtilsJava;

public class DustSimpleAgentComm implements DustKnowledgeCommServices, DustKnowledgeCommServices.DustKnowledgeCommAgent {

	@Override
	public void dustKnowledgeProcProcessorBegin() throws Exception {
//		DustEntity card = Dust.getRefEntity(DustConstKnowledgeInfoContext.Message, false, DustLinkKnowledgeInfoIterator.Cardinality, null);
		DustEntity type = Dust.getRefEntity(DustConstKnowledgeInfoContext.Message, false, DustLinkKnowledgeCommStatement.Type, null);
		
		Integer idx = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message, DustAttributeKnowledgeInfoIterator.index);
		String key = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message, DustAttributeKnowledgeInfoIterator.key);

		String state = Dust.getAttrValue(type, DustAttributeToolsGenericIdentified.idLocal);
		DustUtilsDev.dump("Receiving Start block...", state, DustUtilsJava.toEnumId(DustConstKnowledgeCommStatementType.Entity).endsWith(state) ? idx : key);
	}

	@Override
	public void dustKnowledgeProcProcessorEnd() throws Exception {
		DustEntity type = Dust.getRefEntity(DustConstKnowledgeInfoContext.Message, false, DustLinkKnowledgeCommStatement.Type, null);
		String state = Dust.getAttrValue(type, DustAttributeToolsGenericIdentified.idLocal);
		DustUtilsDev.dump("Receiving End block...", state);
	}

	@Override
	public DustConstKnowledgeProcVisitorResponse dustKnowledgeProcVisitorVisit(DustEntity entity) throws Exception {
		String key = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message, DustAttributeToolsGenericIdentified.idLocal);
		Object value = Dust.getAttrValue(DustConstKnowledgeInfoContext.Message, DustAttributeKnowledgeInfoVariant.value);

		DustUtilsDev.dump("Receiving value...", key, value);
		
		return null;
	}

}
