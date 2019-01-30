package dust.mj02.dust;

public class DustProcRuntime extends Dust implements DustComponents, DustDataComponents, Dust.DustRuntime {
	
	ThreadLocal<DustContext> ctxRoot = new ThreadLocal<>();
	
	public static void main(String[] args) throws Exception {
		DustProcRuntime rt = new DustProcRuntime();
		init(rt);
		rt.init();
	}
	
	public DustProcRuntime() {
		ctxRoot.set(new DustDataContext(null));
	}
	
	void init() throws Exception {
		DustCommComponents.DustCommSource rdr = new DustCommJsonLoader();
		DustCommDiscussion disc = new DustCommDiscussion();
		
		disc.load(rdr, "MJ02Boot02.json");
	}

	@Override
	public DustEntity ctxGetEntity(Object globalId) {
		return ctxRoot.get().ctxGetEntity(globalId);
	}

	@Override
	public <RetType> RetType ctxAccessEntity(DataCommand cmd, DustEntity e, Object key, Object val, Object collId) {
		return ctxRoot.get().ctxAccessEntity(cmd, e, key, val, collId);
	}

	@Override
	public void processRefs(RefProcessor proc, DustEntity source, Object linkDef, DustEntity target) {
		ctxRoot.get().processRefs(proc, source, linkDef, target);
	}

	@Override
	public void processEntities(EntityProcessor proc) {
		ctxRoot.get().processEntities(proc);
	}
}
