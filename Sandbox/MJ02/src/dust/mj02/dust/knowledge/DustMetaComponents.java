package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustMetaComponents extends DustComponents {
	
	enum DustMetaTypes implements DustEntityKey {
		Unit, Type, AttDef, LinkDef, Service, Command
	};
	
	enum DustMetaLinks implements DustEntityKey {
		AttDefType, LinkDefType, LinkDefReverse,
		TypeAttDefs, TypeLinkDefs
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
