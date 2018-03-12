package dust.gen.base;
import dust.pub.DustUnitComponents;

public interface DustUtilsComponents extends DustUnitComponents {
	
	enum DustUtilsIdentifiedAtt implements DustAttrDef {
		id
	}
	
	enum DustUtilsTypes implements DustTypeDescriptor {
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
