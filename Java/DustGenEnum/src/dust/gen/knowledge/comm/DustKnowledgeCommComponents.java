package dust.gen.knowledge.comm;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeCommComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustConstKnowledgeCommStatementType implements DustConst {
		Discussion, Entity, Model, Data;
	}

	enum DustAttributeKnowledgeCommTerm implements DustEntity {
		idStore, idLocal;
	}
	
	enum DustLinkKnowledgeCommStatement implements DustEntity {
		Type;
	}
	
	enum DustLinkKnowledgeCommAgent implements DustEntity {
		Source;
	}


	enum DustTypeKnowledgeComm implements DustEntity {
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
