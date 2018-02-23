package dust.pub;

public class Dust implements DustRuntimeComponents {
	
	private static DustIdManager idMgr;
	private static DustRuntime rt;
	
	public static void main(String[] args) throws Exception {
		String cName;
		DustConfig cfg = new DustConfigConsole(args);

		cName = cfg.getCfg(DustConfigKeys.DustIdManager);
		idMgr = (DustIdManager) Class.forName(cName).newInstance();
		idMgr.init(cfg);
		
		cName = cfg.getCfg(DustConfigKeys.DustRuntime);
		rt = (DustRuntime) Class.forName(cName).newInstance();
		rt.init(cfg);
	}

	public static DustField getField(String idType, String idField) {
		return idMgr.getField(idType, idField);
	}
}
