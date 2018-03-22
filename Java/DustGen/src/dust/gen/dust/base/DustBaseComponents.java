package dust.gen.dust.base;

import dust.gen.dust.DustComponents;
import dust.pub.metaenum.DustMetaEnum;

public interface DustBaseComponents extends DustComponents, DustMetaEnum {

	enum DustEntityState implements DustEntity {
		Temporal, InSync, RefChanged, Changed, Constructed, Destructed
	}
	
	enum DustBaseLinkCommand implements DustEntity {
		Add, Replace, Remove, ChangeKey;
	}

	enum DustBaseVisitorResponse implements DustEntity {
		OK, Skip, Exit, Repeat, Restart;
	}

	enum DustBaseContext implements DustEntity {
		Self, Message, Block;
	}
	

	enum DustLinkBaseMessage implements DustLink {
		Command, Target
	}
	
	enum DustLinkBaseEntity implements DustLink {
		Services
	}
	
	enum DustAttributeBaseEntity implements DustAttribute {
		svcImpl
	}
	
	enum DustBaseTypes implements DustMetaTypeDescriptor {
		Entity(DustAttributeBaseEntity.class, DustLinkBaseEntity.class),
		Message(null, DustLinkBaseMessage.class),
		ConstValue(null, null),
		;
		
		private final Class<? extends Enum<?>> atts;
		private final Class<? extends Enum<?>> links;
		
		private DustBaseTypes(Class<? extends Enum<?>> atts, Class<? extends Enum<?>> links) {
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
	
	enum DustBaseMessageInitable implements DustEntity {
		Init
	}

	enum DustBaseServices implements DustMetaServiceDescriptor {
		Initable(DustBaseMessageInitable.class);
		
		private final Class<? extends Enum<?>> msgs;
		
		private DustBaseServices( Class<? extends Enum<?>> msgs) {
			this.msgs = msgs;
		}

		@Override
		public Class<? extends Enum<?>> getMessageEnum() {
			return msgs;
		}
	}
}
