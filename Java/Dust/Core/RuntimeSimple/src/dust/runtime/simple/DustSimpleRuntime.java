package dust.runtime.simple;

import dust.gen.dust.test.unit01.DustTestUnit01Components;
import dust.gen.knowledge.comm.DustKnowledgeCommComponents;
import dust.gen.runtime.binding.DustRuntimeBindingComponents;
import dust.gen.runtime.environment.DustRuntimeEnvironmentComponents;
import dust.gen.tools.generic.DustToolsGenericComponents;
import dust.gen.tools.persistence.DustToolsPersistenceComponents;
import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.DustUtilsDev;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustBootComponents.DustRuntimeBootable, DustToolsPersistenceComponents, DustKnowledgeCommComponents {
	class SimpleBlock extends DustUtilsFactory<DustConstKnowledgeInfoContext, SimpleEntity> {
		public SimpleBlock(SimpleEntity self, SimpleEntity msg) {
			super(true);
			put(DustConstKnowledgeInfoContext.Self, self);
			put(DustConstKnowledgeInfoContext.Message, msg);
		}

		@Override
		protected SimpleEntity create(DustConstKnowledgeInfoContext key, Object... hints) {
			return new SimpleEntity(mgrData, null);
		}

	}

	DustSimpleManagerMeta mgrMeta;
	DustSimpleManagerData mgrData;
	DustSimpleManagerLink mgrLink;
	DustSimpleManagerAaa mgrAaa;
	
	DustBindingManager binMgr;

	SimpleBlock block;

	public DustSimpleRuntime() {
		mgrMeta = new DustSimpleManagerMeta();
		mgrData = new DustSimpleManagerData();
		mgrLink = new DustSimpleManagerLink();
		mgrAaa = new DustSimpleManagerAaa();
		
		block = new SimpleBlock(new SimpleEntity(mgrData, mgrMeta.getSimpleType(DustTypeRuntimeEnvironment.Manager)), null);
	}

	private SimpleEntity resolveEntity(DustEntity entity, DustConstRuntimeAccessAccessMode access) {
		SimpleEntity se = null;
		
		if (entity instanceof SimpleEntity) {
			se = (SimpleEntity) entity;
		} else {
			if (entity instanceof DustConstKnowledgeInfoContext) {
				se = block.get((DustConstKnowledgeInfoContext) entity);
			} else {
				se = mgrMeta.optResolveEntity(entity);
			}
		}
		
		if ( (null != se ) && !mgrAaa.verifyAccess(se, access) ) {
			DustException.throwException(DustStatusRuntimeAccess.AccessDenied, se, access);
		}
		
		return se;
	}

	@Override
	public void init(DustConfig config) throws Exception {
	}

	void test() throws Exception {
		DustEntity msg = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true, DustLinkRuntimeEnvironmentManager.InitMessage, null);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, msg, DustLinkKnowledgeProcMessage.Command, DustCommandToolsGenericInitable.Init);
//		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, msg, DustCommandToolsPersistenceStore.Read, DustLinkKnowledgeProcMessage.Command);

		DustEntity target = Dust.getRefEntity(msg, true, DustLinkKnowledgeProcMessage.Target, null);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, target, DustLinkKnowledgeInfoEntity.Services, DustServiceToolsPersistence.Store);

		assignLogic(DustServiceToolsPersistence.Store, "dust.persistence.jsonsimple.DustJsonReader");
		assignLogic(DustServiceKnowledgeComm.Discussion, "dust.runtime.simple.DustSimpleCommDiscussion");

		DustEntity msgRelay = Dust.getRefEntity(target, true, DustLinkToolsGenericChain.DefaultMessage, null);
		DustEntity agent = Dust.getRefEntity(msgRelay, true, DustLinkKnowledgeProcMessage.Target, null);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, agent, DustLinkKnowledgeInfoEntity.Services, DustServiceKnowledgeComm.Discussion);

		Dust.send(msg);
		
//		DustUtilsDev.dump("Test dump ...", agent);

	}

	private void assignLogic(DustService svc, String javaClass) {
		DustEntity bm = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true, DustLinkRuntimeEnvironmentManager.BinaryManager, null);

		DustEntity la = Dust.getRefEntity(bm, true, DustRuntimeBindingComponents.DustLinkRuntimeBindingManager.LogicAssignments, svc);
		Dust.setAttrValue(la, DustRuntimeBindingComponents.DustAttributeRuntimeBindingLogicAssignment.javaClass, javaClass);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, la, DustRuntimeBindingComponents.DustLinkRuntimeBindingLogicAssignment.Service, svc);
	}

	void test02() throws Exception {
		DustEntity msg = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true, DustLinkRuntimeEnvironmentManager.InitMessage, null);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, msg, DustLinkKnowledgeProcMessage.Command, DustTestUnit01Components.DustCommandTestUnit01TestSimple.Msg01);

		DustEntity target = Dust.getRefEntity(msg, true, DustLinkKnowledgeProcMessage.Target, null);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, target, DustLinkKnowledgeInfoEntity.Services, DustTestUnit01Components.DustServiceTestUnit01.TestSimple);

		DustEntity bm = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true, DustLinkRuntimeEnvironmentManager.BinaryManager, null);
		DustEntity la = Dust.getRefEntity(bm, true, DustRuntimeBindingComponents.DustLinkRuntimeBindingManager.LogicAssignments, null);
		
		Dust.setAttrValue(la, DustRuntimeBindingComponents.DustAttributeRuntimeBindingLogicAssignment.javaClass, "dust.test.unit01.TestSimple");
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, la, DustRuntimeBindingComponents.DustLinkRuntimeBindingLogicAssignment.Service, DustTestUnit01Components.DustServiceTestUnit01.TestSimple);

		Dust.send(msg);
	}

	void test01() throws Exception {
//		mgrMeta.registerUnit(DustUtilsComponents.DustUtilsTypes.class.getName(), null);
//		mgrMeta.registerUnit(DustBaseComponents.DustBaseTypes.class.getName(), null);
//		mgrMeta.registerUnit(DustRuntimeComponents.DustTypeRuntime.class.getName(), null);
		
		DustEntity eMeta = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true, DustRuntimeEnvironmentComponents.DustLinkRuntimeEnvironmentManager.MetaManager, null);
		
		Dust.setAttrValue(eMeta, DustToolsGenericComponents.DustAttributeToolsGenericIdentified.idLocal, "na?");
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, eMeta, DustLinkKnowledgeInfoEntity.Services, DustRuntimeEnvironmentComponents.DustServiceKnowledgeMeta.Manager);
		
		DustEntity msg = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true, DustRuntimeEnvironmentComponents.DustLinkRuntimeEnvironmentManager.InitMessage, null);
		
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, msg, DustLinkKnowledgeProcMessage.Target, eMeta);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, msg, DustLinkKnowledgeProcMessage.Command, DustRuntimeEnvironmentComponents.DustCommandKnowledgeMetaManager.RegisterUnit);
		
		Dust.processRefs(new DustKnowledgeProcVisitor() {
			@Override
			public DustConstKnowledgeProcVisitorResponse dustKnowledgeProcVisitorVisit(DustEntity entity) throws Exception {
				DustUtilsDev.dump("Test visitor called!");
				return null;
			}
		}, DustConstKnowledgeInfoContext.Self, DustRuntimeEnvironmentComponents.DustLinkRuntimeEnvironmentManager.InitMessage);
		
		Dust.send(msg);
	}
	
	@Override
	public void setBinaryManager(DustBindingManager binMgr) {
		this.binMgr = binMgr;
	}

	@Override
	public void dustKnowledgeProcProcessorBegin() throws Exception {
		DustEntity bm = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true, DustRuntimeEnvironmentComponents.DustLinkRuntimeEnvironmentManager.BinaryManager, null);
		binMgr.setEntity(bm);
		
		test();
	}

	@Override
	public void dustKnowledgeProcProcessorEnd() throws Exception {
	}

	@Override
	public <ValType> ValType dustRuntimeEnvironmentManagerGetAttrValue(DustEntity entity, DustAttribute field) {
		SimpleEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Read);
		if (null == se) {
			return null;
		} else {
			SimpleAttDef sa = mgrMeta.getSimpleAttDef(field);
			return se.getFieldValue(sa);
		}
	}

	@Override
	public void dustRuntimeEnvironmentManagerSetAttrValue(DustEntity entity, DustAttribute field, Object value) {
		SimpleEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Write);
		if (null != se) {
			SimpleAttDef sa = mgrMeta.getSimpleAttDef(field);
			se.setFieldValue(sa, value);
		}
	}

	@Override
	public void dustRuntimeEnvironmentManagerSend(DustEntity msg) {
		SimpleEntity se = resolveEntity(msg, DustConstRuntimeAccessAccessMode.Execute);
		
		if (null != se) {
			SimpleBlock b = block;
			try {
				SimpleEntity target = (SimpleEntity) Dust.getRefEntity(msg, false, DustLinkKnowledgeProcMessage.Target, null);
				block = new SimpleBlock(target, (SimpleEntity) msg);
				binMgr.sendMessage(target, se);
			} catch (Exception e) {
				DustException.wrapException(e, DustStatusRuntimeEnvironment.MessageSendError);
			} finally {
				block = b;
			}
		}
	}
	
	@Override
	public DustEntity dustRuntimeEnvironmentManagerGetRefEntity(DustEntity entity, boolean createIfMissing, DustLink linkDef,
			Object key) {
		SimpleEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Read);
		SimpleLinkDef ld = mgrMeta.getSimpleLinkDef(linkDef);

		try {
			return (null == se) ? null : mgrLink.getRefEntity(se, createIfMissing, ld, key);
		} catch (Exception e) {
			DustException.wrapException(e, DustStatusRuntimeEnvironment.LinkCreationError);
			return null;
		}
	}

	@Override
	public void dustRuntimeEnvironmentManagerProcessRefs(DustKnowledgeProcVisitor proc, DustEntity entity, DustLink... path) {
		SimpleEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Read);
		if (null != se) {
			mgrLink.processRefs(proc, se, path, 0);
		}
	}

	@Override
	public DustEntity dustRuntimeEnvironmentManagerModifyRefs(DustConstKnowledgeInfoLinkCommand refCmd, DustEntity left,
			DustEntity right, DustLink linkDef, Object... params) {
		SimpleEntity seLeft = resolveEntity(left, DustConstRuntimeAccessAccessMode.Write);
		SimpleEntity seRight = resolveEntity(right, DustConstRuntimeAccessAccessMode.Write);
		SimpleLinkDef ld = mgrMeta.getSimpleLinkDef(linkDef);
		return mgrLink.modifyRefs(refCmd, seLeft, seRight, ld, params);
	}

}
