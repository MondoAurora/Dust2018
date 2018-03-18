package dust.gen.dust.runtime;

import dust.gen.dust.DustComponents;
import dust.pub.metaenum.DustMetaEnum;

public interface DustRuntimeComponents extends DustComponents, DustMetaEnum {
	
	enum DustRuntimeMessages implements DustEntity {
		LinkCreationError
	}


	enum DustRuntimeLinkMessage implements DustLink {
		Command, Target
	}
	
	enum DustRuntimeTypes implements DustMetaTypeDescriptor {
		MetaManager(null, DustRuntimeLinkMessage.class),
		Runtime(null, null),
		;
		
		private final Class<? extends Enum<?>> atts;
		private final Class<? extends Enum<?>> links;
		
		private DustRuntimeTypes(Class<? extends Enum<?>> atts, Class<? extends Enum<?>> links) {
			this.atts = atts;
			this.links = links;
		}

		@Override
		public Class<? extends Enum<?>> getAttribEnum() {
			return atts;
		}

		@Override
		public Class<? extends Enum<?>> getLinkEnum() {
			return links;
		}		
	}
	
	enum DustRuntimeMessageMetaManager implements DustEntity {
		RegisterUnit
	}

	enum DustRuntimeServices implements DustMetaServiceDescriptor {
		MetaManager(DustRuntimeMessageMetaManager.class);
		
		private final Class<? extends Enum<?>> msgs;
		
		private DustRuntimeServices( Class<? extends Enum<?>> msgs) {
			this.msgs = msgs;
		}

		@Override
		public Class<? extends Enum<?>> getMessageEnum() {
			return msgs;
		}
	}
}
