package dust.pub.boot;

import dust.pub.Dust;
import dust.pub.DustComponents;
import dust.utils.DustUtilsConfig;

public interface DustBootComponents extends DustComponents {
	enum DustConfigKeys {
		DustBinding, DustRuntime, DustNodeInit
	}

	interface DustEntityOwner {
		void setEntity(DustEntity entity);
	}

	interface DustEnvironmentService {
		void launch() throws Exception;
		void shutdown() throws Exception;
	}

	public interface DustBindingManager extends DustEnvironmentService, DustEntityOwner {
		void sendMessage(DustEntity target, DustEntity msg) throws Exception;
		// void initLogicInstance(DustEntity owner, DustEntity command) throws
		// Exception;

		// <LogicClass> Class<LogicClass> getEntityLogicClass(DustEntity entity) throws
		// Exception;
		// DustEntity enterCustomLogic(Object logic) throws Exception;
		// void leaveCustomLogic();
	}

	interface DustRuntimeBootable extends Dust.DustInterface, DustUtilsConfig.Configurable, DustEnvironmentService {
		void setBinaryManager(DustBindingManager binMgr);
	}
}
