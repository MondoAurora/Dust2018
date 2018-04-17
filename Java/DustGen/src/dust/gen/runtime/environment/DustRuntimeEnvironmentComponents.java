package dust.gen.runtime.environment;

import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.DustUtilsGen.LinkWrapper;
import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeEnvironmentComponents
		extends DustKnowledgeInfoComponents, DustKnowledgeMetaComponents, DustKnowledgeProcComponents {

	enum DustConstRuntimeEnvironment implements DustEntityWrapper {
		LinkCreationError, MessageSendError, GetEntityError;

		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	enum DustLinkRuntimeEnvironmentManager implements DustLinkWrapper {
		InitMessage, BinaryManager, MetaManager;

		private final LinkWrapper lw = new LinkWrapper(this);

		@Override
		public DustEntity entity() {
			return lw.entity();
		}

		@Override
		public DustLink link() {
			return lw;
		}
	}

	enum DustTypeRuntimeEnvironment implements DustEntityWrapper {
		Manager;

		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

	}

	interface DustRuntimeEnvironmentManager extends DustKnowledgeProcProcessor {
		DustEntity dustRuntimeEnvironmentManagerGetEntity(DustEntity type, String storeId, String revision);

		<ValType> ValType dustRuntimeEnvironmentManagerGetAttrValue(DustEntity entity, DustEntity field);
		void dustRuntimeEnvironmentManagerSetAttrValue(DustEntity entity, DustEntity field, Object value);
	
		DustEntity dustRuntimeEnvironmentManagerGetRefEntity(DustEntity entity, boolean createIfMissing, DustEntity linkDef, Object key);
		void dustRuntimeEnvironmentManagerProcessRefs(DustKnowledgeProcVisitor proc, DustEntity root, DustEntity... path);
		DustEntity dustRuntimeEnvironmentManagerModifyRefs(DustConstKnowledgeInfoLinkCommand refCmd, DustEntity left, DustEntity right, DustEntity linkDef,
				Object... params);
	
		void dustRuntimeEnvironmentManagerSend(DustEntity msg);
	}
}
