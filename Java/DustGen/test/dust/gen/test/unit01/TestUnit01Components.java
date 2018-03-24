package dust.gen.test.unit01;

import dust.gen.dust.base.DustBaseComponents;

public interface TestUnit01Components extends DustBaseComponents {

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

	enum TestUnit01MessageTestSimple implements DustMessage {
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
