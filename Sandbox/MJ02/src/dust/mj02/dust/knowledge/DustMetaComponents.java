package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;
import dust.utils.DustUtilsJava;

public interface DustMetaComponents extends DustComponents {
	
	enum DustMetaTypes implements DustEntityKey {
		Unit(null), Type("Types"), AttDef("Atts"), LinkDef("Links"), Service("Services"), Command("Messages");
		
		private final String enumPostfix;

		private DustMetaTypes(String enumPostfix) {
			this.enumPostfix = enumPostfix;
		}
		
		public static DustMetaTypes getMetaTypeHack(String eName) {
			for ( DustMetaTypes mt : values() ) {
				if ( !DustUtilsJava.isEmpty(mt.enumPostfix) && eName.endsWith(mt.enumPostfix) ) {
					return mt;
				}
			}
			return null;
		}
	};
	
	enum DustMetaLinks implements DustEntityKey {
		AttDefType, AttDefParent, 
		LinkDefType, LinkDefReverse, LinkDefParent,
		TypeAttDefs, TypeLinkDefs, TypeLinkedServices
	};
	
	enum DustMetaValueAttDefType implements DustEntityKey {
		AttDefBool, AttDefIdentifier, AttDefFloat, AttDefInteger
	};
	
	enum DustMetaValueLinkDefType implements DustEntityKey {
		LinkDefSet("(", ")"), LinkDefMap("{", "}"), LinkDefArray("[", "]"), LinkDefSingle("", "");
		
		final String sepStart;
		final String sepEnd;
		
		
		private DustMetaValueLinkDefType(String sepStart, String sepEnd) {
			this.sepStart = sepStart;
			this.sepEnd = sepEnd;
		}

		public String getSepStart() {
			return sepStart;
		}
		
		public String getSepEnd() {
			return sepEnd;
		}
		
	};
	
}
