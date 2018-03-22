package dust.runtime.simple;

import dust.gen.dust.base.DustBaseComponents;
import dust.gen.dust.runtime.DustRuntimeComponents;
import dust.gen.dust.runtime.DustRuntimeComponents.DustMessageRuntime;
import dust.gen.dust.utils.DustUtilsComponents;
import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.DustUtilsDev;
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

	private SimpleEntity resolveEntity(DustEntity entity, DustAccessMode access) {
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
	}

	private void test() throws Exception {
		mgrMeta.registerUnit(DustUtilsComponents.DustUtilsTypes.class.getName(), null);
		mgrMeta.registerUnit(DustBaseComponents.DustBaseTypes.class.getName(), null);
		mgrMeta.registerUnit(DustRuntimeComponents.DustTypeRuntime.class.getName(), null);
		
		DustEntity eMeta = Dust.getRefEntity(DustBaseContext.Self, true, DustRuntimeComponents.DustLinkRuntimeRuntime.MetaManager, null);
		Dust.modifyRefs(DustBaseLinkCommand.Add, eMeta, DustRuntimeComponents.DustRuntimeServices.MetaManager, DustLinkBaseEntity.Services);
		
		DustEntity msg = Dust.getRefEntity(DustBaseContext.Self, true, DustRuntimeComponents.DustLinkRuntimeRuntime.InitMessage, null);
		
		Dust.modifyRefs(DustBaseLinkCommand.Add, msg, eMeta, DustLinkBaseMessage.Target);
		Dust.modifyRefs(DustBaseLinkCommand.Add, msg, DustRuntimeComponents.DustRuntimeMessageMetaManager.RegisterUnit, DustLinkBaseMessage.Command);
		
		Dust.processRefs(new DustBaseVisitor() {
			@Override
			public DustBaseVisitorResponse dustDustBaseVisitorVisit(DustEntity entity) throws Exception {
				DustUtilsDev.dump("Test visitor called!");
				return null;
			}
		}, DustBaseContext.Self, DustRuntimeComponents.DustLinkRuntimeRuntime.InitMessage);
		
		Dust.send(msg);
	}

	@Override
	public void setBinaryManager(DustBinaryManager binMgr) {
		this.binMgr = binMgr;
	}

	@Override
	public void dustBaseBlockProcessorBegin() throws Exception {
		test();
	}

	@Override
	public void dustBaseBlockProcessorEnd(DustBaseVisitorResponse lastResp, Exception optException) throws Exception {
	}

	@Override
	public <ValType> ValType getAttrValue(DustEntity entity, DustAttribute field) {
		SimpleEntity se = resolveEntity(entity, DustAccessMode.Read);
		if (null == se) {
			return null;
		} else {
			SimpleAttDef sa = mgrMeta.getSimpleAttDef(field);
			return se.getFieldValue(sa);
		}
	}

	@Override
	public void setAttrValue(DustEntity entity, DustAttribute field, Object value) {
		SimpleEntity se = resolveEntity(entity, DustAccessMode.Write);
		if (null != se) {
			SimpleAttDef sa = mgrMeta.getSimpleAttDef(field);
			se.setFieldValue(sa, value);
		}
	}

	@Override
	public void send(DustEntity msg) {
		SimpleEntity se = resolveEntity(msg, DustAccessMode.Execute);
		
		if (null != se) {
			try {
				binMgr.sendMessage(se);
			} catch (Exception e) {
				DustException.wrapException(e, DustMessageRuntime.MessageSendError);
			}
		}
	}
	
	@Override
	public DustEntity getRefEntity(DustEntity entity, boolean createIfMissing, DustLink linkDef, Object key) {
		SimpleEntity se = resolveEntity(entity, DustAccessMode.Read);
		SimpleLinkDef ld = mgrMeta.getSimpleLinkDef(linkDef);

		try {
			return (null == se) ? null : mgrLink.getRefEntity(se, createIfMissing, ld, key);
		} catch (Exception e) {
			DustException.wrapException(e, DustMessageRuntime.LinkCreationError);
			return null;
		}
	}

	@Override
	public void processRefs(DustBaseVisitor proc, DustEntity entity, DustLink... path) {
		SimpleEntity se = resolveEntity(entity, DustAccessMode.Read);
		if (null != se) {
			mgrLink.processRefs(proc, se, path, 0);
		}
	}

	@Override
	public DustEntity modifyRefs(DustBaseLinkCommand refCmd, DustEntity left, DustEntity right,
			DustLink linkDef, Object... params) {
		SimpleEntity seLeft = resolveEntity(left, DustAccessMode.Write);
		SimpleEntity seRight = resolveEntity(right, DustAccessMode.Write);
		SimpleLinkDef ld = mgrMeta.getSimpleLinkDef(linkDef);
		return mgrLink.modifyRefs(refCmd, seLeft, seRight, ld, params);
	}

}
