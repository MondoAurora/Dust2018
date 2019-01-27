package dust.mj02.dust;

import java.util.Map;

public interface DustCommComponents extends DustComponents {
	enum CommKeys {
		CommSrcHandler, KeyCommIdLocal, KeyCommIdStore;
	}
	
	interface SourceReader {
		public Map<Object, Object> load(Object src) throws Exception;
	}
}
