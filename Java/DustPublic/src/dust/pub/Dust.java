package dust.pub;

public class Dust implements DustRuntimeComponents {
	
	private static DustIdManager idMgr;
	private static DustRuntime rt;
	
	public static void main(String[] args) throws Exception {
		DustConfig cfg = new DustConfigConsole(args);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				optShutdown(rt);
				optShutdown(idMgr);
			}
		});

		idMgr = optLoadInit(cfg, DustConfigKeys.DustIdManager);
		rt = optLoadInit(cfg, DustConfigKeys.DustRuntime);
		optLoadInit(cfg, DustConfigKeys.DustNodeInit);
	}

	private static void optShutdown(Object comp) {
		if ( comp instanceof DustShutdownAware ) {
			try {
				((DustShutdownAware)comp).shutdown();
			} catch (Exception e) {
				DustUtils.wrapException(e, null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <RetType extends DustConfigurable> RetType optLoadInit(DustConfig cfg, DustConfigKeys key) throws Exception {
		RetType ret = null;
		
		String cName = cfg.getCfg(key);
		if ( !DustUtils.isEmpty(cName) ) {
			ret = (RetType) Class.forName(cName).newInstance();
			ret.init(cfg);
		}
		
		return ret;
	}

	public static DustField getField(String idType, String idField) {
		return idMgr.getField(idType, idField);
	}
	
	public static DustEntity getEntity(DustContext root, DustField... path) {
		return rt.getEntity(root, path);
	}

	public static <ValType> ValType getFieldValue(DustEntity entity, DustField field) {
		return rt.getFieldValue(entity, field);
	}

	public static void setFieldValue(DustEntity entity, DustField field, Object value) {
		rt.setFieldValue(entity, field, value);
	}

	public static void send(DustEntity msg) {
		rt.send(msg);
	}

}
