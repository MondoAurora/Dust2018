package dust.gen.knowledge.comm;

import dust.gen.knowledge.proc.DustKnowledgeProcServices;

public interface DustKnowledgeCommServices extends DustKnowledgeCommComponents, DustKnowledgeProcServices {

	interface DustKnowledgeCommAgent extends DustKnowledgeProcProcessor, DustKnowledgeProcVisitor {
	}

}
