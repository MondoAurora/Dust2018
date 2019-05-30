package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;
import dust.utils.DustUtilsJava;

public interface DustMetaComponents extends DustComponents {
	
	enum DustMetaTypes implements DustEntityKey {
		Type("Types"), AttDef("Atts"), LinkDef("Links"), Service("Services"), Command("Messages"), Constant("Values"), Meta("");
		
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
		
		public String replacePostfix(String name, DustMetaTypes target) {
			int pf = name.lastIndexOf(enumPostfix);
			return name.substring(0, pf) + target.enumPostfix;
		}
	};
	
	enum DustMetaTags implements DustEntityKey {
		NotCloned, AttRaw, AttIdentifier, AttBoolean, AttNumeric, AttValueType
	};
	
	enum DustMetaLinks implements DustEntityKey {
		AttDefType, AttDefParent, 
		LinkDefType, LinkDefReverse, LinkDefParent,
		TypeAttDefs, TypeLinkDefs, TypeLinkedServices,
		CommandRetValType,
		MetaAccessControl
	};
	
	enum DustMetaAttDefTypeValues implements DustEntityKey {
		AttDefBool, AttDefIdentifier, AttDefFloat, AttDefInteger, AttDefRaw
	};
	
	enum DustMetaLinkDefTypeValues implements DustEntityKey {
		LinkDefSet("(", ")"), LinkDefMap("{", "}"), LinkDefArray("[", "]"), LinkDefSingle("", "");
		
		final String sepStart;
		final String sepEnd;
		
		
		private DustMetaLinkDefTypeValues(String sepStart, String sepEnd) {
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
