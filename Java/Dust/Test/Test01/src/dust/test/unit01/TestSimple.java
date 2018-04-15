package dust.test.unit01;

import dust.gen.dust.test.unit01.DustTestUnit01Services;
import dust.utils.DustUtilsDev;

public class TestSimple implements DustTestUnit01Services.DustTestUnit01TestSimple {

	@Override
	public void dustTestUnit01TestSimpleMsg01() throws Exception {
		DustUtilsDev.dump("You got the call!");
	}

}
