package dust.gen.knowledge.proc;

import dust.gen.DustComponents;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents;

public interface DustKnowledgeProcComponents extends DustComponents, DustKnowledgeInfoComponents {

	enum DustConstKnowledgeProcVisitorResponse implements DustConst {
		OK, Skip, Exit, Repeat, Restart;
	}

	enum DustLinkKnowledgeProcMessage implements DustEntity {
		Command, Target;
	}

	enum DustTypeKnowledgeProc implements DustEntity {
		Message, Status;
	}

	
	
	enum DustCommandKnowledgeProcVisitor implements DustCommand {
		Visit;
	}

	enum DustCommandKnowledgeProcProcessor implements DustCommand {
		Begin, End;
	}

	enum DustServiceKnowledgeProc implements DustService {
		Visitor, Processor;
		final DustService[] extServices;
		
		private DustServiceKnowledgeProc(DustService... extServices) {
			this.extServices = extServices;
		}
	}
}
