package dust.runtime.simple;

import dust.gen.dust.test.unit01.DustTestUnit01Components;
import dust.gen.knowledge.comm.DustKnowledgeCommComponents;
import dust.gen.runtime.binding.DustRuntimeBindingComponents;
import dust.gen.runtime.environment.DustRuntimeEnvironmentComponents;
import dust.gen.tools.persistence.DustToolsPersistenceComponents;
import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustBootComponents.DustRuntimeBootable,
		DustToolsPersistenceComponents, DustKnowledgeCommComponents {
	class SimpleBlock extends DustUtilsFactory<DustConstKnowledgeInfoContext, InfoEntity> {
		public SimpleBlock(InfoEntity self, InfoEntity msg) {
			super(true);
			put(DustConstKnowledgeInfoContext.Self, self);
			put(DustConstKnowledgeInfoContext.Message, msg);
		}

		@Override
		protected InfoEntity create(DustConstKnowledgeInfoContext key, Object... hints) {
			return new InfoEntityData(mgrData, null);
		}

	}

	DustSimpleManagerData mgrData;
	DustSimpleManagerLink mgrLink;
	DustSimpleManagerAaa mgrAaa;

	DustBindingManager binMgr;

	SimpleBlock block;

	public DustSimpleRuntime() {
		mgrData = new DustSimpleManagerData();
		mgrLink = new DustSimpleManagerLink();
		mgrAaa = new DustSimpleManagerAaa();

		block = new SimpleBlock(
				new InfoEntityData(mgrData, (SimpleType) optResolveMeta(DustTypeRuntimeEnvironment.Manager)), null);
	}

	private InfoEntity resolveEntity(DustEntity entity, DustConstRuntimeAccessAccessMode access) {
		InfoEntity se = null;

		if (entity instanceof InfoEntity) {
			se = (InfoEntity) entity;
		} else {
			if (entity instanceof DustConstKnowledgeInfoContext) {
				se = block.get((DustConstKnowledgeInfoContext) entity);
			} else {
				se = optResolveMeta(entity);
			}
		}

		if ((null != se) && !mgrAaa.verifyAccess(se, access)) {
			DustException.throwException(DustStatusRuntimeAccess.AccessDenied, se, access);
		}

		return se;
	}

	private <RetType> RetType optResolveMeta(Object entity) {
		return mgrData.optResolveMeta(entity);
	}

	@Override
	public void init(DustConfig config) throws Exception {
	}

	void test() throws Exception {
		DustEntity msg = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true,
				DustLinkRuntimeEnvironmentManager.InitMessage, null);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, msg, DustLinkKnowledgeProcMessage.Command,
				DustCommandToolsGenericInitable.Init);

		DustEntity target = Dust.getRefEntity(msg, true, DustLinkKnowledgeProcMessage.Target, null);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, target, DustLinkKnowledgeInfoEntity.Services,
				DustServiceToolsPersistence.Store);

		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, DustServiceToolsPersistence.Store,
				DustLinkToolsGenericConnected.Extends, DustServiceToolsGeneric.Initable);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, DustServiceToolsPersistence.Store,
				DustLinkToolsGenericConnected.Extends, DustServiceKnowledgeProc.Processor);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, DustServiceKnowledgeComm.Discussion,
				DustLinkToolsGenericConnected.Extends, DustServiceKnowledgeProc.Processor);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, DustServiceKnowledgeComm.Discussion,
				DustLinkToolsGenericConnected.Extends, DustServiceKnowledgeProc.Visitor);

		assignLogic(DustServiceToolsPersistence.Store, "dust.persistence.jsonsimple.DustJsonReader");
		assignLogic(DustServiceKnowledgeComm.Discussion, "dust.runtime.simple.DustSimpleCommDiscussion");

		DustEntity msgRelay = Dust.getRefEntity(target, true, DustLinkToolsGenericChain.DefaultMessage, null);
		DustEntity agent = Dust.getRefEntity(msgRelay, true, DustLinkKnowledgeProcMessage.Target, null);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, agent, DustLinkKnowledgeInfoEntity.Services,
				DustServiceKnowledgeComm.Discussion);

		Dust.send(msg);

	}

	private void assignLogic(DustService svc, String javaClass) {
		DustEntity bm = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true,
				DustLinkRuntimeEnvironmentManager.BinaryManager, null);

		DustEntity la = Dust.getRefEntity(bm, true,
				DustRuntimeBindingComponents.DustLinkRuntimeBindingManager.LogicAssignments, svc);
		Dust.setAttrValue(la, DustRuntimeBindingComponents.DustAttributeRuntimeBindingLogicAssignment.javaClass,
				javaClass);
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Add, la,
				DustRuntimeBindingComponents.DustLinkRuntimeBindingLogicAssignment.Service, svc);
	}

	@Override
	public void setBinaryManager(DustBindingManager binMgr) {
		this.binMgr = binMgr;
	}

	@Override
	public void launch() {
		DustEntity bm = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true,
				DustRuntimeEnvironmentComponents.DustLinkRuntimeEnvironmentManager.BinaryManager, null);
		binMgr.setEntity(bm);

		test();
	}

	@Override
	public void shutdown() {
	}
	
	@Override
	public DustEntity getEntity(DustEntity type, String storeId, String revision) {
		try {
			return mgrData.dustKnowledgeInfoSourceGet((DustType) type, storeId);
		} catch (Exception e) {
			DustException.wrapException(e, DustStatusRuntimeEnvironment.GetEntityError);
		}
		return null;
	}

	@Override
	public <ValType> ValType getAttrValue(DustEntity entity, DustEntity field) {
		InfoEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Read);
		if (null == se) {
			return null;
		} else {
			SimpleAttDef sa = optResolveMeta(field);
			return se.getFieldValue(sa);
		}
	}

	@Override
	public void setAttrValue(DustEntity entity, DustEntity field, Object value) {
		InfoEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Write);
		if (null != se) {
			SimpleAttDef sa = optResolveMeta(field);
			se.setFieldValue(sa, value);
		}
	}

	@Override
	public void send(DustEntity msg) {
		InfoEntity se = resolveEntity(msg, DustConstRuntimeAccessAccessMode.Execute);

		if (null != se) {
			SimpleBlock b = block;
			try {
				InfoEntity target = (InfoEntity) Dust.getRefEntity(msg, false, DustLinkKnowledgeProcMessage.Target,
						null);
				block = new SimpleBlock(target, (InfoEntity) msg);
				binMgr.sendMessage(target, se);
			} catch (Exception e) {
				DustException.wrapException(e, DustStatusRuntimeEnvironment.MessageSendError);
			} finally {
				block = b;
			}
		}
	}

	@Override
	public DustEntity getRefEntity(DustEntity entity, boolean createIfMissing, DustEntity linkDef, Object key) {
		InfoEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Read);
		SimpleLinkDef ld = optResolveMeta(linkDef);

		try {
			return (null == se) ? null : mgrLink.getRefEntity(se, createIfMissing, ld, key);
		} catch (Exception e) {
			DustException.wrapException(e, DustStatusRuntimeEnvironment.LinkCreationError);
			return null;
		}
	}

	@Override
	public void processRefs(DustRefVisitor proc, DustEntity root, DustEntity ref) {
		InfoEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Read);
		if (null != se) {
			mgrLink.processRefs(proc, se, path, 0);
		}
	}

	@Override
	public DustEntity modifyRefs(DustRefCommand refCmd, DustEntity left, DustEntity linkDef, DustEntity right,
			Object key) {
		InfoEntity seLeft = resolveEntity(left, DustConstRuntimeAccessAccessMode.Write);
		InfoEntity seRight = resolveEntity(right, DustConstRuntimeAccessAccessMode.Write);
		SimpleLinkDef ld = optResolveMeta(linkDef);
		return mgrLink.modifyRefs(refCmd, seLeft, seRight, ld, key);
	}

}
