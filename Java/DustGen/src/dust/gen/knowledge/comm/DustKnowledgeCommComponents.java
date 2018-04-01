package dust.gen.knowledge.comm;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;

public interface DustKnowledgeCommComponents extends DustToolsGenericComponents, DustKnowledgeMetaComponents {

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
		Agent,;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Service;
		}
	}

}
