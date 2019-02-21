package dust.mj02.dust.knowledge;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustComponents;
import dust.mj02.dust.tools.DustToolsGen;
import dust.mj02.montru.gui.swing.MontruGuiSwingFrame;

public class DustProcRuntime extends Dust implements DustComponents, DustDataComponents, Dust.DustRuntime {
	
	private ThreadLocal<DustContext> ctxThread = new ThreadLocal<>();
	private DustContext ctxRoot;
	
	public static void main(String[] args) throws Exception {
		DustProcRuntime rt = new DustProcRuntime();
		init(rt);
		rt.init();
	}
	
	public DustProcRuntime() {
		ctxThread.set(ctxRoot = new DustDataContext(null));
	}
	
	void init() throws Exception {
		DustToolsGen.init();
		DustKnowledgeGen.init();

//		DustCommComponents.DustCommSource rdr = new DustCommJsonLoader();
//		DustCommDiscussion disc = new DustCommDiscussion();
//		
//		disc.load(rdr, "MJ02Boot02.json");
		
		new MontruGuiSwingFrame();
	}

	@Override
	public DustEntity ctxGetEntity(Object globalId) {
		return getCtx().ctxGetEntity(globalId);
	}

	@Override
	public <RetType> RetType ctxAccessEntity(DataCommand cmd, DustEntity e, DustEntity key, Object val, Object collId) {
		return getCtx().ctxAccessEntity(cmd, e, key, val, collId);
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
		DustContext ctx = ctxThread.get();
		return (null == ctx) ? ctxRoot : ctx;
	}

}
