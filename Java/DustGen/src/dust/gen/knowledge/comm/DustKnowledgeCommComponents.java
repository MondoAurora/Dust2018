package dust.gen.knowledge.comm;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeCommComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustAttributeKnowledgeCommTerm implements DustAttribute {
		idGlobal, idLocal;

		@Override
		public DustType getType() {
			return DustTypeKnowledgeComm.Term;
		}
	}
	
	enum DustLinkKnowledgeCommTalk implements DustLink {
		Source;

		@Override
		public DustType getType() {
			return DustTypeKnowledgeComm.Talk;
		}
	}


	enum DustTypeKnowledgeComm implements DustType {
		Term, Talk;
	}
	
	enum DustServiceKnowledgeComm implements DustService {
		Agent(DustServiceKnowledgeProc.Visitor, DustServiceKnowledgeProc.Processor),;
		final DustService[] extServices;
		
		private DustServiceKnowledgeComm(DustService... extServices) {
			this.extServices = extServices;
		}
		@Override
		public DustService[] getExtends() {
			return extServices;
		}
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Service;
		}
	}

}
