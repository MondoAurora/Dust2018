package dust.gen.knowledge.comm;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeCommComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustConstKnowledgeCommStatementType implements DustConst {
		Discussion, Entity, Model, Data;
	}

	enum DustAttributeKnowledgeCommTerm implements DustAttribute {
		idStore, idLocal;
	}
	
	enum DustLinkKnowledgeCommStatement implements DustLink {
		Type;
	}
	
	enum DustLinkKnowledgeCommAgent implements DustLink {
		Source;
	}


	enum DustTypeKnowledgeComm implements DustType {
		Term, Discussion, Statement;
	}
	
	enum DustServiceKnowledgeComm implements DustService {
		Discussion(DustServiceKnowledgeProc.Visitor, DustServiceKnowledgeProc.Processor),;
		final DustService[] extServices;
		
		private DustServiceKnowledgeComm(DustService... extServices) {
			this.extServices = extServices;
		}
	}

}
