package dust.runtime.simple;

import dust.gen.dust.utils.DustUtilsComponents;
import dust.pub.DustException;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustBootComponents.DustRuntimeBootable {
	class SimpleBlock extends DustUtilsFactory<DustBaseContext, SimpleEntity> {
		public SimpleBlock() {
			super(true);
		}

		@Override
		protected SimpleEntity create(DustBaseContext key, Object... hints) {
			return new SimpleEntity(mgrData, null);
		}

	}

	DustSimpleManagerMeta mgrMeta;
	DustSimpleManagerData mgrData;
	DustSimpleManagerLink mgrLink;
	DustSimpleManagerAaa mgrAaa;
	
	DustBinaryManager binMgr;

	SimpleBlock block = new SimpleBlock();

	public DustSimpleRuntime() {
		mgrMeta = new DustSimpleManagerMeta();
		mgrData = new DustSimpleManagerData();
		mgrLink = new DustSimpleManagerLink();
		mgrAaa = new DustSimpleManagerAaa();
	}

	private SimpleEntity resolveEntity(DustBaseEntity entity, DustAccessMode access) {
		SimpleEntity se = null;
		
		if (entity instanceof SimpleEntity) {
			se = (SimpleEntity) entity;
		} else {
			if (entity instanceof DustBaseContext) {
				se = block.get((DustBaseContext) entity);
			} else {
				se = mgrMeta.optResolveEntity(entity);
			}
		}
		
		if ( (null != se ) && !mgrAaa.verifyAccess(se, access) ) {
			DustException.throwException(DustAaaMessages.AccessDenied, se, access);
		}
		
		return se;
	}

	@Override
	public void init(DustConfig config) throws Exception {
		test();
	}

	private void test() {
		mgrMeta.registerUnit(DustUtilsComponents.DustUtilsTypes.class, null);
		setAttrValue(DustBaseContext.Self, DustUtilsComponents.DustUtilsIdentifiedAtt.id, "HelloWorld");	
	}

	@Override
	public void setBinaryManager(DustBinaryManager binMgr) {
		this.binMgr = binMgr;
	}

	@Override
	public void dustBaseBlockProcessorBegin() throws Exception {
	}

	@Override
	public void dustBaseBlockProcessorEnd(DustBaseVisitorResponse lastResp, Exception optException) throws Exception {
	}

	@Override
	public <ValType> ValType getAttrValue(DustBaseEntity entity, DustBaseAttribute field) {
		SimpleEntity se = resolveEntity(entity, DustAccessMode.Read);
		if (null == se) {
			return null;
		} else {
			SimpleAttribute sa = mgrMeta.getAtt(field);
			return se.getFieldValue(sa);
		}
	}

	@Override
	public void setAttrValue(DustBaseEntity entity, DustBaseAttribute field, Object value) {
		SimpleEntity se = resolveEntity(entity, DustAccessMode.Write);
		if (null != se) {
			SimpleAttribute sa = mgrMeta.getAtt(field);
			se.setFieldValue(sa, value);
		}
	}

	@Override
	public void send(DustBaseEntity msg) {
		SimpleEntity se = resolveEntity(msg, DustAccessMode.Execute);
		
		if (null != se) {
			// 
		}
	}

	@Override
	public void processRefs(DustBaseVisitor proc, DustBaseEntity entity, DustBaseLink... path) {
		SimpleEntity se = resolveEntity(entity, DustAccessMode.Read);
		if (null != se) {
			mgrLink.processRefs(proc, se, path, 0);
		}
	}

	@Override
	public DustBaseEntity modifyRefs(DustBaseLinkCommand refCmd, DustBaseEntity left, DustBaseEntity right,
			DustBaseLink linkDef, Object... params) {
		SimpleEntity seLeft = resolveEntity(left, DustAccessMode.Write);
		SimpleEntity seRight = resolveEntity(right, DustAccessMode.Write);
		return mgrLink.modifyRefs(refCmd, seLeft, seRight, linkDef, params);
	}

}
