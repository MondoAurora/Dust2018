package dust.utils;

import java.util.HashMap;
import java.util.Map;

public abstract class DustUtilsConfig {
	String CFG_KEYVALUESEP = "=";
	String CFG_LISTFLAG = "*";
	
	public interface Configurable {
		void init(DustUtilsConfig config) throws Exception;
	}

	public abstract <ValType> ValType getCfg(String key);

	public <ValType> ValType getCfg(Enum<?> key) {
		return getCfg(key.name());
	}

	protected void loadCfgString(String str, Map<String, Object> target) {
		int idx = str.indexOf(CFG_KEYVALUESEP);
		if (-1 == idx) {
			target.put(str, Boolean.TRUE.toString());
		} else {
			String key = str.substring(0, idx);
			String val = str.substring(idx + 1);
			if (key.endsWith(CFG_LISTFLAG)) {
				key = key.substring(0, idx - 1);
				String ls = val.substring(0, 1);
				target.put(key, val.substring(1).split(ls));
			} else {
				target.put(key, val);
			}
		}
	}

	public static class Std extends DustUtilsConfig {
		@Override
		@SuppressWarnings("unchecked")
		public <ValType> ValType getCfg(String key) {
			String ret = System.getProperty(key);
			return (ValType) ((null == ret) ? System.getenv(key) : ret);
		}
	}

	public static class DustConfigConsole extends Std {
		Map<String, Object> cmdLineCfg = new HashMap<>();

		public DustConfigConsole(String[] cmdLine) {
			for (String par : cmdLine) {
				loadCfgString(par, cmdLineCfg);
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public <ValType> ValType getCfg(String key) {
			ValType ret = (ValType) cmdLineCfg.get(key);

			return (null == ret) ? super.getCfg(key) : ret;
		}
	}
}
