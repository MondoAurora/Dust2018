package dust.gen.dust.test.unit01;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;

public interface DustTestUnit01Components extends DustKnowledgeInfoComponents {

	enum DustTypeTestUnit01 implements DustType {
		TestSimple;
	}

	enum DustServiceTestUnit01 implements DustService {
		TestSimple,
		;
		@Override
		public DustType getType() {
			return DustTypeTestUnit01.TestSimple;
		}
	}


	enum DustLinkTestSimple implements DustLink {
		LinkSingle;
		@Override
		public DustType getType() {
			return DustTypeTestUnit01.TestSimple;
		}
	}

	enum DustAttributeTestSimple implements DustAttribute {
		attr01;
		@Override
		public DustType getType() {
			return DustTypeTestUnit01.TestSimple;
		}
	}

	enum DustCommandTestUnit01TestSimple implements DustCommand {
		Msg01;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustServiceTestUnit01.TestSimple;
		}
	}
}
