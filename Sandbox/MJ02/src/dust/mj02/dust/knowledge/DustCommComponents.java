package dust.mj02.dust.knowledge;

import java.util.Map;

import dust.mj02.dust.DustComponents;

public interface DustCommComponents extends DustComponents {
	enum CommKeys {
		CommSrcHandler, KeyCommIdLocal, KeyCommIdStore;
	};
	
	interface DustCommSource {
		public Map<Object, Object> dustCommSourceRead(Object src) throws Exception;
	}
}
