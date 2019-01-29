package dust.mj02.dust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public interface DustCommComponents extends DustComponents {
	enum CommKeys {
		CommSrcHandler, KeyCommIdLocal, KeyCommIdStore;
	};
	
	enum CommDiscKeys {
		LinkDefType, AttDefType, AttPrimaryType, TypeAtt, TypeLinkDef
	};
	
	enum CommAttDefTypes {
		AttDefBool, AttDefIdentifier, AttDefFloat, AttDefInteger
	};
	
	enum CommLinkDefTypes {
		LinkDefSet(HashSet.class), LinkDefMap(HashMap.class), LinkDefArray(ArrayList.class), LinkDefSingle(null);
		
		private final Class<?> containerClass;

		private CommLinkDefTypes(Class<?> containerClass) {
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
	
	interface SourceReader {
		public Map<Object, Object> load(Object src) throws Exception;
	}
}
