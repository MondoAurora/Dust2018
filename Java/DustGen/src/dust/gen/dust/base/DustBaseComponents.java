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
	

	enum DustBaseMessageLink implements DustLink {
		Command, Target
	}
	
	enum DustUtilsTypes implements DustMetaTypeDescriptor {
		Message(null, DustBaseMessageLink.class);
		
		private final Class<? extends Enum<?>> atts;
		private final Class<? extends Enum<?>> links;
		
		private DustUtilsTypes(Class<? extends Enum<?>> atts, Class<? extends Enum<?>> links) {
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
}
