package dust.pub;

import dust.gen.dust.base.DustBaseComponents;

public abstract class DustDefaultProcessor implements DustBaseComponents.DustBaseBlockProcessor, DustBaseComponents.DustBaseVisitor, DustBaseComponents {
	protected void doProcess(DustBaseEntity entity) throws Exception {
	};

	@Override
	public DustBaseVisitorResponse dustDustBaseVisitorVisit(DustBaseEntity entity) throws Exception {
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