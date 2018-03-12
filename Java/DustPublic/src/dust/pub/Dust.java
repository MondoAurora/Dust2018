package dust.pub;

import java.util.ArrayList;

public class Dust implements DustRuntimeComponents {
	
	private static DustMetaManager META_MANAGER;
	private static DustRuntime RUNTIME;
	private static ArrayList<DustShutdownAware> SHUTDOWN_COMPONENTS = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		DustConfig cfg = new DustConfigConsole(args);
		
		initComps(cfg);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown();
			}
		});
	}

	protected static void shutdown() {
		for(DustShutdownAware dsa : SHUTDOWN_COMPONENTS ) {
			try {
				DustUtilsDev.dump("Shutting down", dsa.getClass().getName());
				dsa.shutdown();
			} catch (Exception e) {
				DustUtils.wrapException(e, null);
			}
		}
	}

	protected static void initComps(DustConfig cfg) throws Exception {
		META_MANAGER = optLoadInit(cfg, DustConfigKeys.DustMetaManager);
		RUNTIME = optLoadInit(cfg, DustConfigKeys.DustRuntime);

		DustBinaryManager binMgr = optLoadInit(cfg, DustConfigKeys.DustBinaryManager);
		RUNTIME.setBinaryManager(binMgr);
		
		optLoadInit(cfg, DustConfigKeys.DustNodeInit);
	}

	protected static void optAddShutdown(Object comp) {
		if ( comp instanceof DustShutdownAware ) {
			SHUTDOWN_COMPONENTS.add(0, (DustShutdownAware)comp);
		}
	}

	@SuppressWarnings("unchecked")
	protected static <RetType extends DustConfigurable> RetType optLoadInit(DustConfig cfg, DustConfigKeys key) throws Exception {
		RetType ret = null;
		
		String cName = cfg.getCfg(key);
		if ( !DustUtils.isEmpty(cName) ) {
			ret = (RetType) Class.forName(cName).newInstance();
			DustUtilsDev.dump("Initializing", cName);
			ret.init(cfg);
			
			optAddShutdown(ret);
		}
		
		return ret;
	}

//	public static void registerUnit(Class<? extends Enum<?>> types, Class<? extends Enum<?>> services) {
//		META_MANAGER.registerUnit(types, services);		
//	}

//	public static DustAttrDef getAttrDef(DustEntity eType, String id) {
//		return META_MANAGER.getAttrDef(eType, id);
//	}
	
	public static <ValType> ValType getAttrValue(DustEntity entity, DustAttrDef field) {
		return RUNTIME.getAttrValue(entity, field);
	}

	public static void setAttrValue(DustEntity entity, DustAttrDef field, Object value) {
		RUNTIME.setAttrValue(entity, field, value);
	}

	
//	public static DustLinkDef getLinkDef(DustEntity eType, String id) {
//		return META_MANAGER.getLinkDef(eType, id);
//	}
	
	public static void processRefs(DustItemProcessor proc, DustEntity root, DustLinkDef... path) {
		RUNTIME.processRefs(proc, root, path);
	}

	public static DustEntity modifyRefs(DustRefCommand refCmd, DustEntity left, DustEntity right, DustLinkDef linkDef, Object... params) {
		return RUNTIME.modifyRefs(refCmd, left, right, linkDef, params);
	}


	
	public static void send(DustEntity msg) {
		RUNTIME.send(msg);
	}

}
