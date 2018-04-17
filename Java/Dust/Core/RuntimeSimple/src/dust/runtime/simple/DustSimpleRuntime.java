package dust.runtime.simple;

import java.util.Collection;
import java.util.HashSet;

import dust.gen.knowledge.comm.DustKnowledgeCommComponents;
import dust.gen.runtime.binding.DustRuntimeBindingComponents;
import dust.gen.tools.persistence.DustToolsPersistenceComponents;
import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustBootComponents.DustRuntimeBootable,
		DustToolsPersistenceComponents, DustKnowledgeCommComponents, DustRuntimeBindingComponents {
	
	private static final Collection<DustEntity> BLOCK_ENTITIES = new HashSet<>();
	
	class SimpleBlock extends DustUtilsFactory<DustEntity, InfoEntity> {
		public SimpleBlock(InfoEntity self, InfoEntity msg) {
			super(false);
			put(DustConstKnowledgeInfoContext.Self.entity(), self);
			put(DustConstKnowledgeInfoContext.Message.entity(), msg);
		}
		
		@Override
		protected InfoEntity create(DustEntity key, Object... hints) {
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
	}

	private InfoEntity resolveEntity(DustEntity entity, DustConstRuntimeAccessAccessMode access) {
		InfoEntity se = BLOCK_ENTITIES.contains(entity) ? block.get(entity) : (InfoEntity) entity;

//		if (entity instanceof InfoEntity) {
//			se = (InfoEntity) entity;
//		} else {
//			if (entity instanceof DustConstKnowledgeInfoContext) {
//				se = block.get((DustConstKnowledgeInfoContext) entity);
//			} else {
//				se = optResolveMeta(entity);
//			}
//		}

		if ((null != se) && !mgrAaa.verifyAccess(se, access)) {
			DustException.throwException(DustConstRuntimeAccess.AccessDenied, se, access);
		}

		return se;
	}

	@Override
	public void init(DustConfig config) throws Exception {
	}

	void test() throws Exception {
		DustEntity msg = DustLinkRuntimeEnvironmentManager.InitMessage.link().get(DustConstKnowledgeInfoContext.Self.entity(), true, null);
		DustLinkKnowledgeProcMessage.Command.link().modify(msg, DustRefCommand.Add, DustCommandToolsGenericInitable.Init.entity(), null);

		DustEntity target = DustLinkKnowledgeProcMessage.Target.link().get(msg, true, null);
		DustLinkKnowledgeInfoEntity.Services.link().modify(target, DustRefCommand.Add, DustServiceToolsPersistence.Store.entity(), null);

		DustLinkToolsGenericConnected.Extends.link().modify(DustServiceToolsPersistence.Store.entity(), DustRefCommand.Add, DustServiceToolsGeneric.Initable.entity(), null);
		DustLinkToolsGenericConnected.Extends.link().modify(DustServiceToolsPersistence.Store.entity(), DustRefCommand.Add, DustServiceKnowledgeProc.Processor.entity(), null);
		DustLinkToolsGenericConnected.Extends.link().modify(DustServiceKnowledgeComm.Discussion.entity(), DustRefCommand.Add, DustServiceKnowledgeProc.Processor.entity(), null);
		DustLinkToolsGenericConnected.Extends.link().modify(DustServiceKnowledgeComm.Discussion.entity(), DustRefCommand.Add, DustServiceKnowledgeProc.Visitor.entity(), null);

		assignLogic(DustServiceToolsPersistence.Store.entity(), "dust.persistence.jsonsimple.DustJsonReader");
		assignLogic(DustServiceKnowledgeComm.Discussion.entity(), "dust.runtime.simple.DustSimpleCommDiscussion");

		DustEntity msgRelay = DustLinkToolsGenericChain.DefaultMessage.link().get(target, true, null);
		DustEntity agent = DustLinkKnowledgeProcMessage.Target.link().get(msgRelay, true, null);
		DustLinkKnowledgeInfoEntity.Services.link().modify(agent, DustRefCommand.Add, DustServiceKnowledgeComm.Discussion.entity(), null);

		Dust.send(msg);
	}

	private void assignLogic(DustEntity svc, String javaClass) {
		DustEntity bm = DustLinkRuntimeEnvironmentManager.BinaryManager.link().get(DustConstKnowledgeInfoContext.Self.entity(), true,
				null);

		DustEntity la = DustLinkRuntimeBindingManager.LogicAssignments.link().get(bm, true, svc);
		DustAttributeRuntimeBindingLogicAssignment.javaClass.attribute().setValue(la, javaClass);
		DustLinkRuntimeBindingLogicAssignment.Service.link().modify(la, DustRefCommand.Add, svc, null);
	}

	@Override
	public void setBinaryManager(DustBindingManager binMgr) {
		this.binMgr = binMgr;
	}

	@Override
	public void launch() throws Exception {
		for ( DustConstKnowledgeInfoContext e : DustConstKnowledgeInfoContext.values() ) {
			BLOCK_ENTITIES.add(e.entity());
		}
		
		block = new SimpleBlock(
				new InfoEntityData(mgrData, (SimpleType) DustTypeRuntimeEnvironment.Manager.entity()), null);
		
		DustEntity bm = DustLinkRuntimeEnvironmentManager.BinaryManager.link().get(DustConstKnowledgeInfoContext.Self.entity(), true,
				null);
		binMgr.setEntity(bm);

		test();
	}

	@Override
	public void shutdown() throws Exception {
	}
	
	@Override
	public DustEntity getEntity(DustEntity type, String storeId, String revision) {
		try {
			return mgrData.dustKnowledgeInfoSourceGet( type, storeId);
		} catch (Exception e) {
			DustException.wrapException(e, DustConstRuntimeEnvironment.GetEntityError.entity());
		}
		return null;
	}

	@Override
	public <ValType> ValType getAttrValue(DustEntity entity, DustEntity field) {
		InfoEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Read);
		if (null == se) {
			return null;
		} else {
			return se.getFieldValue((SimpleAttDef)field);
		}
	}

	@Override
	public void setAttrValue(DustEntity entity, DustEntity field, Object value) {
		InfoEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Write);
		if (null != se) {
			se.setFieldValue((SimpleAttDef)field, value);
		}
	}

	@Override
	public void send(DustEntity msg) {
		InfoEntity se = resolveEntity(msg, DustConstRuntimeAccessAccessMode.Execute);

		if (null != se) {
			SimpleBlock b = block;
			try {
				InfoEntity target = (InfoEntity) DustLinkKnowledgeProcMessage.Target.link().get(msg, false, null);
				block = new SimpleBlock(target, (InfoEntity) msg);
				binMgr.sendMessage(target, se);
			} catch (Exception e) {
				DustException.wrapException(e, DustConstRuntimeEnvironment.MessageSendError.entity());
			} finally {
				block = b;
			}
		}
	}

	@Override
	public DustEntity getRefEntity(DustEntity entity, boolean createIfMissing, DustEntity linkDef, Object key) {
		InfoEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Read);

		try {
			return (null == se) ? null : mgrLink.getRefEntity(se, createIfMissing, (SimpleLinkDef)linkDef, key);
		} catch (Exception e) {
			DustException.wrapException(e, DustConstRuntimeEnvironment.LinkCreationError.entity());
			return null;
		}
	}

	@Override
	public void processRefs(DustRefVisitor proc, DustEntity entity, DustEntity ref) {
		InfoEntity se = resolveEntity(entity, DustConstRuntimeAccessAccessMode.Read);
		if (null != se) {
			mgrLink.processRefs(proc, se, ref);
		}
	}

	@Override
	public DustEntity modifyRefs(DustRefCommand refCmd, DustEntity left, DustEntity linkDef, DustEntity right,
			Object key) {
		InfoEntity seLeft = resolveEntity(left, DustConstRuntimeAccessAccessMode.Write);
		InfoEntity seRight = resolveEntity(right, DustConstRuntimeAccessAccessMode.Write);
		return mgrLink.modifyRefs(refCmd, seLeft, seRight, (SimpleLinkDef)linkDef, key);
	}

}
