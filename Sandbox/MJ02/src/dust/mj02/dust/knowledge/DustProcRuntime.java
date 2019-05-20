package dust.mj02.dust.knowledge;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustComponents;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents.DustProcTypes;
import dust.mj02.dust.text.DustTextGen;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.mj02.dust.tools.DustToolsGen;
import dust.mj02.montru.gui.swing.DustGuiSwingMontruMain;

public class DustProcRuntime extends Dust implements DustComponents, DustDataComponents, DustGenericComponents, Dust.DustRuntime {
	
	private ThreadLocal<DustContext> threadSessions = new ThreadLocal<>();
	private DustProcSession rootSession;
	
	public static void main(String[] args) throws Exception {
		DustProcRuntime rt = new DustProcRuntime();
		init(rt);
		rt.init();
	}
	
	public DustProcRuntime() {
		threadSessions.set(rootSession = new DustProcSession(null));
	}
	
	void init() throws Exception {
		DustKnowledgeGen.init();
		DustToolsGen.init();
		DustTextGen.init();

//	    DustJavaGen.init();
//		DustTempHacks.detectMetaConnections();

        DustUtils.accessEntity(DataCommand.setRef, rootSession.ctxSelf, DustDataLinks.EntityPrimaryType, DustProcTypes.Session);
        DustUtils.accessEntity(DataCommand.setRef, rootSession.ctxSelf, DustDataLinks.EntityModels, DustProcTypes.Session);
        DustUtils.accessEntity(DataCommand.setRef, rootSession.ctxSelf, DustDataLinks.EntityModels, DustGenericTypes.Identified);
        DustUtils.accessEntity(DataCommand.setValue, rootSession.ctxSelf, DustGenericAtts.IdentifiedIdLocal, "RootSession");
		
		new DustGuiSwingMontruMain().activeInit();
	}

	@Override
	public DustEntity ctxGetEntity(Object globalId) {
		return getCtx().ctxGetEntity(globalId);
	}

	@Override
	public <RetType> RetType ctxAccessEntity(DataCommand cmd, DustEntity e, DustEntity key, Object val, Object hint) {
		return getCtx().ctxAccessEntity(cmd, e, key, val, hint);
	}

	@Override
	public void ctxProcessRefs(RefProcessor proc, DustEntity source, DustEntity linkDef, DustEntity target) {
		getCtx().ctxProcessRefs(proc, source, linkDef, target);
	}

	@Override
	public void ctxProcessEntities(EntityProcessor proc) {
		getCtx().ctxProcessEntities(proc);
	}
	

	private DustContext getCtx() {
		DustContext ctx = threadSessions.get();
		return (null == ctx) ? rootSession : ctx;
	}

}
