package dust.gen.dust.core.comm;

import dust.gen.dust.core.meta.DustCoreMetaComponents;
import dust.gen.dust.tools.generic.DustToolsGenericComponents;

public interface DustCoreCommComponents extends DustToolsGenericComponents, DustCoreMetaComponents {

	enum DustAttributeCoreCommTerm implements DustAttribute {
		idGlobal, idLocal;

		@Override
		public DustType getType() {
			return DustTypeCoreComm.Term;
		}
	}
	
	enum DustLinkCoreCommTalk implements DustLink {
		Source;

		@Override
		public DustType getType() {
			return DustTypeCoreComm.Talk;
		}
	}


	enum DustTypeCoreComm implements DustType {
		Term, Talk;
	}
	
	enum DustServiceCoreComm implements DustService {
		Talk,;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Service;
		}
	}

}
