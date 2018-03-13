package dust.pub;

public class Dust implements DustBootComponents {
	
	private static DustRuntime RUNTIME;
//	private static ArrayList<DustShutdownAware> SHUTDOWN_COMPONENTS = new ArrayList<>();
	
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
		try {
			RUNTIME.dustBaseBlockProcessorEnd(DustBaseVisitorResponse.OK, null);
		} catch (Exception e) {
			DustUtils.wrapException(e, null);
		}
		
//		for(DustShutdownAware dsa : SHUTDOWN_COMPONENTS ) {
//			try {
//				DustUtilsDev.dump("Shutting down", dsa.getClass().getName());
//				dsa.shutdown();
//			} catch (Exception e) {
//				DustUtils.wrapException(e, null);
//			}
//		}
	}

	protected static void initComps(DustConfig cfg) throws Exception {
//		META_MANAGER = optLoadInit(cfg, DustConfigKeys.DustMetaManager);
		RUNTIME = optLoadInit(cfg, DustConfigKeys.DustRuntime);

		DustBinaryManager binMgr = optLoadInit(cfg, DustConfigKeys.DustBinaryManager);
		RUNTIME.setBinaryManager(binMgr);
		
		RUNTIME.dustBaseBlockProcessorBegin();
		
		optLoadInit(cfg, DustConfigKeys.DustNodeInit);
	}

//	protected static void optAddShutdown(Object comp) {
//		if ( comp instanceof DustShutdownAware ) {
//			SHUTDOWN_COMPONENTS.add(0, (DustShutdownAware)comp);
//		}
//	}

	@SuppressWarnings("unchecked")
	protected static <RetType extends DustConfigurable> RetType optLoadInit(DustConfig cfg, DustConfigKeys key) throws Exception {
		RetType ret = null;
		
		String cName = cfg.getCfg(key);
		if ( !DustUtils.isEmpty(cName) ) {
			ret = (RetType) Class.forName(cName).newInstance();
			DustUtilsDev.dump("Initializing", cName);
			ret.init(cfg);
			
//			optAddShutdown(ret);
		}
		
		return ret;
	}

	public static <ValType> ValType getAttrValue(DustBaseEntity entity, DustBaseAttributeDef field) {
		return RUNTIME.getAttrValue(entity, field);
	}

	public static void setAttrValue(DustBaseEntity entity, DustBaseAttributeDef field, Object value) {
		RUNTIME.setAttrValue(entity, field, value);
	}
	
	public static void processRefs(DustBaseVisitor proc, DustBaseEntity root, DustBaseLinkDef... path) {
		RUNTIME.processRefs(proc, root, path);
	}

	public static DustBaseEntity modifyRefs(DustBaseLinkCommand refCmd, DustBaseEntity left, DustBaseEntity right, DustBaseLinkDef linkDef, Object... params) {
		return RUNTIME.modifyRefs(refCmd, left, right, linkDef, params);
	}
	
	public static void send(DustBaseEntity msg) {
		RUNTIME.send(msg);
	}
}
