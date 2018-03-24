package dust.test.unit01;

import dust.gen.test.unit01.TestUnit01Services;
import dust.pub.DustUtilsDev;

public class TestSimple implements TestUnit01Services.TestUnit01TestSimple {

	@Override
	public void testUnit01TestSimpleMsg01() throws Exception {
		DustUtilsDev.dump("You got the call!");
	}

}
