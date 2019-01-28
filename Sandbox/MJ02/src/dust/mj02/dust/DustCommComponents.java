package dust.mj02.dust;

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
		LinkDefSet, LinkDefMap, LinkDefArray, LinkDefSingle
	};
	
	interface SourceReader {
		public Map<Object, Object> load(Object src) throws Exception;
	}
}
