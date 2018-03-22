package dust.pub.boot;

import java.util.HashMap;
import java.util.Map;

import dust.gen.dust.base.DustBaseServices;

public interface DustBootComponents extends DustBaseServices {
	String CFG_KEYVALUESEP = "=";
	String CFG_LISTFLAG = "*";

	enum DustConfigKeys {
		DustBinaryManager, DustRuntime, DustNodeInit
	}

	abstract class DustConfig {
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
	}

	interface DustConfigurable {
		void init(DustConfig config) throws Exception;
	}

	interface DustShutdownAware {
		void shutdown() throws Exception;
	}

	public interface DustBinaryManager extends DustShutdownAware {
		void sendMessage(DustEntity msg) throws Exception;
//		void initLogicInstance(DustEntity owner, DustEntity command) throws Exception;

//		<LogicClass> Class<LogicClass> getEntityLogicClass(DustEntity entity) throws Exception;
//		DustEntity enterCustomLogic(Object logic) throws Exception;
//		void leaveCustomLogic();
	}
	
	interface DustRuntimeBootable extends DustRuntime, DustConfigurable {
		void setBinaryManager(DustBinaryManager binMgr);
	}

	class DustConfigStd extends DustConfig {
		@Override
		@SuppressWarnings("unchecked")
		public <ValType> ValType getCfg(String key) {
			String ret = System.getProperty(key);
			return (ValType) ((null == ret) ? System.getenv(key) : ret);
		}
	}

	class DustConfigConsole extends DustConfigStd {
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
