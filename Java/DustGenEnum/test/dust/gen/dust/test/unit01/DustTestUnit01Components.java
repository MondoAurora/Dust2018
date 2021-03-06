package dust.gen.dust.test.unit01;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;

public interface DustTestUnit01Components extends DustKnowledgeInfoComponents {

	enum DustTypeTestUnit01 implements DustEntity {
		TestSimple;
	}

	enum DustServiceTestUnit01 implements DustService {
		TestSimple,
		;
		final DustService[] extServices;
		
		private DustServiceTestUnit01(DustService... extServices) {
			this.extServices = extServices;
		}
	}


	enum DustLinkTestSimple implements DustEntity {
		LinkSingle;
	}

	enum DustAttributeTestSimple implements DustEntity {
		attr01;
	}

	enum DustCommandTestUnit01TestSimple implements DustCommand {
		Msg01;
	}
}
