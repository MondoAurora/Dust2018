package dust.pub;

import dust.gen.dust.base.DustBaseServices;

public abstract class DustDefaultProcessor implements DustBaseServices.DustBaseBlockProcessor, DustBaseServices.DustBaseVisitor, DustBaseServices {
	protected void doProcess(DustEntity entity) throws Exception {
	};

	@Override
	public DustBaseVisitorResponse dustDustBaseVisitorVisit(DustEntity entity) throws Exception {
		doProcess(entity);
		return DustBaseVisitorResponse.OK;
	}

	@Override
	public void dustBaseBlockProcessorBegin() throws Exception {
	}

	@Override
	public void dustBaseBlockProcessorEnd(DustBaseVisitorResponse lastResp, Exception optException) throws Exception {
	}

}