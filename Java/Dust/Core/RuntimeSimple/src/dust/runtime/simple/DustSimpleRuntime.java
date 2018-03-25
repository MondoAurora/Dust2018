package dust.runtime.simple;

import dust.gen.dust.core.binding.DustCoreBindingComponents;
import dust.gen.dust.core.runtime.DustCoreRuntimeComponents;
import dust.gen.dust.test.unit01.DustTestUnit01Components;
import dust.gen.dust.tools.generic.DustToolsGenericComponents;
import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.DustUtilsDev;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustBootComponents.DustRuntimeBootable {
	class SimpleBlock extends DustUtilsFactory<DustConstCoreDataContext, SimpleEntity> {
		public SimpleBlock() {
			super(true);
		}

		@Override
		protected SimpleEntity create(DustConstCoreDataContext key, Object... hints) {
			return new SimpleEntity(mgrData, null);
		}

	}

	DustSimpleManagerMeta mgrMeta;
	DustSimpleManagerData mgrData;
	DustSimpleManagerLink mgrLink;
	DustSimpleManagerAaa mgrAaa;
	
	DustBindingManager binMgr;

	SimpleBlock block = new SimpleBlock();

	public DustSimpleRuntime() {
		mgrMeta = new DustSimpleManagerMeta();
		mgrData = new DustSimpleManagerData();
		mgrLink = new DustSimpleManagerLink();
		mgrAaa = new DustSimpleManagerAaa();
	}

	private SimpleEntity resolveEntity(DustEntity entity, DustConstAaaAccessMode access) {
		SimpleEntity se = null;
		
		if (entity instanceof SimpleEntity) {
			se = (SimpleEntity) entity;
		} else {
			if (entity instanceof DustConstCoreDataContext) {
				se = block.get((DustConstCoreDataContext) entity);
			} else {
				se = mgrMeta.optResolveEntity(entity);
			}
		}
		
		if ( (null != se ) && !mgrAaa.verifyAccess(se, access) ) {
			DustException.throwException(DustStatusCoreAaa.AccessDenied, se, access);
		}
		
		return se;
	}

	@Override
	public void init(DustConfig config) throws Exception {
	}

	private void test() throws Exception {
		DustEntity msg = Dust.getRefEntity(DustConstCoreDataContext.Self, true, DustCoreRuntimeComponents.DustLinkCoreRuntimeManager.InitMessage, null);
		Dust.modifyRefs(DustConstCoreDataLinkCommand.Add, msg, DustTestUnit01Components.DustCommandTestUnit01TestSimple.Msg01, DustLinkCoreExecMessage.Command);

		DustEntity target = Dust.getRefEntity(msg, true, DustLinkCoreExecMessage.Target, null);
		Dust.modifyRefs(DustConstCoreDataLinkCommand.Add, target, DustTestUnit01Components.DustServiceTestUnit01.TestSimple, DustLinkCoreDataEntity.Services);

		DustEntity bm = Dust.getRefEntity(DustConstCoreDataContext.Self, true, DustCoreRuntimeComponents.DustLinkCoreRuntimeManager.BinaryManager, null);
		DustEntity la = Dust.getRefEntity(bm, true, DustCoreBindingComponents.DustLinkCoreBindingManager.LogicAssignments, null);
		
		Dust.setAttrValue(la, DustCoreBindingComponents.DustAttributeCoreBindingLogicAssignment.javaClass, "dust.test.unit01.TestSimple");
		Dust.modifyRefs(DustConstCoreDataLinkCommand.Add, la, DustTestUnit01Components.DustServiceTestUnit01.TestSimple, DustCoreBindingComponents.DustLinkCoreBindingLogicAssignment.Service);

		Dust.send(msg);
	}

	void test01() throws Exception {
//		mgrMeta.registerUnit(DustUtilsComponents.DustUtilsTypes.class.getName(), null);
//		mgrMeta.registerUnit(DustBaseComponents.DustBaseTypes.class.getName(), null);
//		mgrMeta.registerUnit(DustRuntimeComponents.DustTypeRuntime.class.getName(), null);
		
		DustEntity eMeta = Dust.getRefEntity(DustConstCoreDataContext.Self, true, DustCoreRuntimeComponents.DustLinkCoreRuntimeManager.MetaManager, null);
		
		Dust.setAttrValue(eMeta, DustToolsGenericComponents.DustAttributeToolsGenericIdentified.idLocal, "na?");
		Dust.modifyRefs(DustConstCoreDataLinkCommand.Add, eMeta, DustCoreRuntimeComponents.DustServiceCoreMeta.Manager, DustLinkCoreDataEntity.Services);
		
		DustEntity msg = Dust.getRefEntity(DustConstCoreDataContext.Self, true, DustCoreRuntimeComponents.DustLinkCoreRuntimeManager.InitMessage, null);
		
		Dust.modifyRefs(DustConstCoreDataLinkCommand.Add, msg, eMeta, DustLinkCoreExecMessage.Target);
		Dust.modifyRefs(DustConstCoreDataLinkCommand.Add, msg, DustCoreRuntimeComponents.DustCommandCoreMetaManager.RegisterUnit, DustLinkCoreExecMessage.Command);
		
		Dust.processRefs(new DustCoreExecVisitor() {
			@Override
			public DustConstCoreExecVisitorResponse dustDustCoreExecVisitorVisit(DustEntity entity) throws Exception {
				DustUtilsDev.dump("Test visitor called!");
				return null;
			}
		}, DustConstCoreDataContext.Self, DustCoreRuntimeComponents.DustLinkCoreRuntimeManager.InitMessage);
		
		Dust.send(msg);
	}
	
	@Override
	public void setBinaryManager(DustBindingManager binMgr) {
		this.binMgr = binMgr;
	}

	@Override
	public void dustCoreExecBlockProcessorBegin() throws Exception {
		DustEntity bm = Dust.getRefEntity(DustConstCoreDataContext.Self, true, DustCoreRuntimeComponents.DustLinkCoreRuntimeManager.BinaryManager, null);
		binMgr.setEntity(bm);
		
		test();
	}

	@Override
	public void dustCoreExecBlockProcessorEnd(DustConstCoreExecVisitorResponse lastResp, Exception optException)
			throws Exception {
	}

	@Override
	public <ValType> ValType dustCoreRuntimeManagerGetAttrValue(DustEntity entity, DustAttribute field) {
		SimpleEntity se = resolveEntity(entity, DustConstAaaAccessMode.Read);
		if (null == se) {
			return null;
		} else {
			SimpleAttDef sa = mgrMeta.getSimpleAttDef(field);
			return se.getFieldValue(sa);
		}
	}

	@Override
	public void dustCoreRuntimeManagerSetAttrValue(DustEntity entity, DustAttribute field, Object value) {
		SimpleEntity se = resolveEntity(entity, DustConstAaaAccessMode.Write);
		if (null != se) {
			SimpleAttDef sa = mgrMeta.getSimpleAttDef(field);
			se.setFieldValue(sa, value);
		}
	}

	@Override
	public void dustCoreRuntimeManagerSend(DustEntity msg) {
		SimpleEntity se = resolveEntity(msg, DustConstAaaAccessMode.Execute);
		
		if (null != se) {
			try {
				binMgr.sendMessage(se);
			} catch (Exception e) {
				DustException.wrapException(e, DustStatusCoreRuntime.MessageSendError);
			}
		}
	}
	
	@Override
	public DustEntity dustCoreRuntimeManagerGetRefEntity(DustEntity entity, boolean createIfMissing, DustLink linkDef,
			Object key) {
		SimpleEntity se = resolveEntity(entity, DustConstAaaAccessMode.Read);
		SimpleLinkDef ld = mgrMeta.getSimpleLinkDef(linkDef);

		try {
			return (null == se) ? null : mgrLink.getRefEntity(se, createIfMissing, ld, key);
		} catch (Exception e) {
			DustException.wrapException(e, DustStatusCoreRuntime.LinkCreationError);
			return null;
		}
	}

	@Override
	public void dustCoreRuntimeManagerProcessRefs(DustCoreExecVisitor proc, DustEntity entity, DustLink... path) {
		SimpleEntity se = resolveEntity(entity, DustConstAaaAccessMode.Read);
		if (null != se) {
			mgrLink.processRefs(proc, se, path, 0);
		}
	}

	@Override
	public DustEntity dustCoreRuntimeManagerModifyRefs(DustConstCoreDataLinkCommand refCmd, DustEntity left,
			DustEntity right, DustLink linkDef, Object... params) {
		SimpleEntity seLeft = resolveEntity(left, DustConstAaaAccessMode.Write);
		SimpleEntity seRight = resolveEntity(right, DustConstAaaAccessMode.Write);
		SimpleLinkDef ld = mgrMeta.getSimpleLinkDef(linkDef);
		return mgrLink.modifyRefs(refCmd, seLeft, seRight, ld, params);
	}

}
