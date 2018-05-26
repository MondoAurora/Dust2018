package dust.gen.tools.generic;

import dust.gen.DustGenComponents;
import dust.gen.DustUtilsGen.AttributeWrapper;
import dust.gen.DustUtilsGen.EntityWrapper;
import dust.gen.DustUtilsGen.LinkWrapper;

public interface DustToolsGenericComponents extends DustGenComponents {

	enum DustAttributeToolsGenericIdentified implements DustAttributeWrapper {
		idLocal, idCombined;

		private final AttributeWrapper aw = new AttributeWrapper(this);

		@Override
		public DustEntity entity() {
			return aw.entity();
		}

		@Override
		public DustAttribute attribute() {
			return aw;
		}
	}

	enum DustLinkToolsGenericConnected implements DustLinkWrapper {
		Owner, Requires, Extends;

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

	enum DustLinkToolsGenericChain implements DustLinkWrapper {
		NextEntity, DefaultMessage;

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
