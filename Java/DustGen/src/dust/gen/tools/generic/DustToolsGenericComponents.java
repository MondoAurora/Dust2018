package dust.gen.tools.generic;

import dust.gen.DustGenComponents;
import dust.gen.DustUtilsGen.EntityWrapper;

public interface DustToolsGenericComponents extends DustGenComponents {

	enum DustAttributeToolsGenericIdentified implements DustEntityAttribute {
		idLocal, idCombined;

		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

		@Override
		public <ValType> ValType getValue(DustEntity entity) {
			return ew.getValue(entity);
		}

		@Override
		public void setValue(DustEntity entity, Object value) {
			ew.setValue(entity, value);
		}
	}

	enum DustLinkToolsGenericConnected implements DustEntityLink {
		Owner, Requires, Extends;

		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

		@Override
		public void process(DustEntity entity, DustRefVisitor proc) {
			ew.process(entity, proc);
		}

		@Override
		public DustEntity get(DustEntity entity, boolean createIfMissing, Object key) {
			return ew.get(entity, createIfMissing, key);
		}

		@Override
		public DustEntity modify(DustEntity entity, DustRefCommand cmd, DustEntity target, Object key) {
			return ew.modify(entity, cmd, target, key);
		}

	}

	enum DustLinkToolsGenericChain implements DustEntityLink {
		NextEntity, DefaultMessage;

		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}

		@Override
		public void process(DustEntity entity, DustRefVisitor proc) {
			ew.process(entity, proc);
		}

		@Override
		public DustEntity get(DustEntity entity, boolean createIfMissing, Object key) {
			return ew.get(entity, createIfMissing, key);
		}

		@Override
		public DustEntity modify(DustEntity entity, DustRefCommand cmd, DustEntity target, Object key) {
			return ew.modify(entity, cmd, target, key);
		}
	}

	enum DustTypeToolsGeneric {
		Identified, Connected, Chain;
	}

	enum DustCommandToolsGenericInitable implements DustEntityWrapper {
		Init;

		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	enum DustServiceToolsGeneric implements DustEntityWrapper {
		Initable;

		private final EntityWrapper ew = new EntityWrapper(this);

		@Override
		public DustEntity entity() {
			return ew.entity();
		}
	}

	interface DustToolsGenericInitable {
		void dustToolsGenericInitableInit() throws Exception;
	}

}
