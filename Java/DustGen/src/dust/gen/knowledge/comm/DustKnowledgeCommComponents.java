package dust.gen.knowledge.comm;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeCommComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustConstKnowledgeCommStatementType {
		Discussion, Entity, Model, Data;
	}

	enum DustAttributeKnowledgeCommTerm {
		idStore, idLocal;
	}
	
	enum DustLinkKnowledgeCommStatement {
		Type;
	}
	
	enum DustLinkKnowledgeCommAgent {
		Source;
	}


	enum DustTypeKnowledgeComm {
		Term, Discussion, Statement;
	}
	
	enum DustServiceKnowledgeComm {
		Discussion;
	}

	interface DustKnowledgeCommDiscussion extends DustKnowledgeProcProcessor, DustKnowledgeProcVisitor {
	}

}
