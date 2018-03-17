package dust.gen.dust.utils;

import dust.gen.dust.base.DustBaseComponents;
import dust.pub.metaenum.DustMetaEnum;

public interface DustUtilsComponents extends DustMetaEnum, DustBaseComponents {
	
	enum DustUtilsIdentifiedAtt implements DustAttribute {
		id
	}
	
	enum DustUtilsTypes implements DustMetaTypeDescriptor {
		Identified(DustUtilsIdentifiedAtt.class,null);
		
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
