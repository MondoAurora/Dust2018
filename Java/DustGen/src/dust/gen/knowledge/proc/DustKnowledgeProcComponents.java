package dust.gen.knowledge.proc;

import dust.gen.DustComponents;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents;

public interface DustKnowledgeProcComponents extends DustComponents, DustKnowledgeInfoComponents {

	enum DustConstKnowledgeProcVisitorResponse implements DustEntity {
		OK, Skip, Exit, Repeat, Restart;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Const;
		}
	}

	enum DustLinkKnowledgeProcMessage implements DustLink {
		Command, Target;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeProc.Message;
		}
	}

	enum DustTypeKnowledgeProc implements DustType {
		Message, Status;
	}

	
	
	enum DustCommandKnowledgeProcVisitor implements DustCommand {
		Visit;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustServiceKnowledgeProc.Visitor;
		}
	}

	enum DustCommandKnowledgeProcProcessor implements DustCommand {
		Begin, End;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustServiceKnowledgeProc.Processor;
		}
	}

	enum DustServiceKnowledgeProc implements DustService {
		Visitor, Processor;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Service;
		}
	}
}
