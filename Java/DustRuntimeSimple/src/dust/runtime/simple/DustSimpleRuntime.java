package dust.runtime.simple;

import dust.gen.dust.utils.DustUtilsComponents;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleRuntime implements DustSimpleRuntimeComponents, DustBootComponents.DustRuntimeBootable {
	class SimpleBlock extends DustUtilsFactory<DustBaseContext, SimpleEntity> {
		public SimpleBlock() {
			super(true);
		}

		@Override
		protected SimpleEntity create(DustBaseContext key, Object... hints) {
			return new SimpleEntity(ctxRoot, null);
		}

	}

	DustSimpleMetaManager metaMgr;
	DustSimpleContext ctxRoot;
	DustBinaryManager binMgr;

	SimpleBlock block = new SimpleBlock();

	public DustSimpleRuntime() {
		metaMgr = new DustSimpleMetaManager();
	}

	private SimpleEntity resolveEntity(DustBaseEntity entity) {
		if (entity instanceof SimpleEntity) {
			return (SimpleEntity) entity;
		} else {
			if (entity instanceof DustBaseContext) {
				return block.get((DustBaseContext) entity);
			} else {
				return metaMgr.optResolveEntity(entity);
			}
		}
	}

	@Override
	public void init(DustConfig config) throws Exception {
		test();
	}

	private void test() {
		metaMgr.registerUnit(DustUtilsComponents.DustUtilsTypes.class, null);
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
		SimpleEntity se = resolveEntity(entity);
		if (null == se) {
			return null;
		} else {
			SimpleAttribute sa = metaMgr.getAtt(field);
			return se.getFieldValue(sa);
		}
	}

	@Override
	public void setAttrValue(DustBaseEntity entity, DustBaseAttribute field, Object value) {
		SimpleEntity se = resolveEntity(entity);
		if (null != se) {
			SimpleAttribute sa = metaMgr.getAtt(field);
			se.setFieldValue(sa, value);
		}
	}

	@Override
	public void send(DustBaseEntity msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processRefs(DustBaseVisitor proc, DustBaseEntity root, DustBaseLink... path) {
		// TODO Auto-generated method stub

	}

	@Override
	public DustBaseEntity modifyRefs(DustBaseLinkCommand refCmd, DustBaseEntity left, DustBaseEntity right,
			DustBaseLink linkDef, Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

}
