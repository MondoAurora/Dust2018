package dust.gen.dust.runtime;

import dust.gen.dust.DustComponents;
import dust.pub.metaenum.DustMetaEnum;

public interface DustRuntimeComponents extends DustComponents, DustMetaEnum {
	
	enum DustMessageRuntime implements DustEntity {
		LinkCreationError, MessageSendError
	}

	enum DustLinkRuntimeRuntime implements DustLink {
		InitMessage, BinaryManager, MetaManager
	}
	
	enum DustTypeRuntime implements DustMetaTypeDescriptor, DustEntity {
		Runtime(null, DustLinkRuntimeRuntime.class),
		MetaManager(null, null),
		;
		
		private final Class<? extends Enum<?>> atts;
		private final Class<? extends Enum<?>> links;
		
		private DustTypeRuntime(Class<? extends Enum<?>> atts, Class<? extends Enum<?>> links) {
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

	enum DustRuntimeServices implements DustMetaServiceDescriptor, DustEntity {
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
