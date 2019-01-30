package dust.mj02.dust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public interface DustMetaComponents extends DustComponents {
	
	enum DustMetaAtts {
		LinkDefType, AttDefType, AttPrimaryType, TypeAtt, TypeLinkDef
	};
	
	enum DustMetaValueAttDefType {
		AttDefBool, AttDefIdentifier, AttDefFloat, AttDefInteger
	};
	
	enum DustMetaValueLinkDefType {
		LinkDefSet(HashSet.class), LinkDefMap(HashMap.class), LinkDefArray(ArrayList.class), LinkDefSingle(null);
		
		private final Class<?> containerClass;

		private DustMetaValueLinkDefType(Class<?> containerClass) {
			this.containerClass = containerClass;
		}
		
		@SuppressWarnings("unchecked")
		public <RetType> RetType createContainer() {
			try {
				return (RetType) ((null == containerClass) ? null : containerClass.newInstance());
			} catch (Exception e) {
				throw new DustException("", e);
			}
		}
	};
	
}
