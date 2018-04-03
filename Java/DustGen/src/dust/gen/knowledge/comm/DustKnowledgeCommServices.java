package dust.gen.knowledge.comm;

import dust.gen.knowledge.proc.DustKnowledgeProcServices;

public interface DustKnowledgeCommServices extends DustKnowledgeCommComponents, DustKnowledgeProcServices {

	interface DustKnowledgeCommDiscussion extends DustKnowledgeProcProcessor, DustKnowledgeProcVisitor {
	}

}
