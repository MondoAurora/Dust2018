package dust.mj02.dust;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DustDataContext implements DustDataComponents {

	@SuppressWarnings("unchecked")
	class SimpleEntity {
		Map<Object, Object> content = new HashMap<>();
		
		public <RetType> RetType put(Object key, Object value) {
			return (RetType) content.put(key, value);
		}

		public <RetType> RetType  get(Object key) {
			return (RetType) content.get(key);
		}
	}
	
	class SimpleRef {
		SimpleEntity linkDef;
		SimpleEntity source;
		SimpleEntity target;
		SimpleRef reverse;
		
		public SimpleRef(SimpleEntity linkDef, SimpleEntity source, SimpleEntity target) {
			super();
			this.linkDef = linkDef;
			this.source = source;
			this.target = target;
		}
		
		public <RetType> RetType keyOf(Object key) {
			return null;
		}
	}

	DustDataContext ctxParent;

	Map<DustId, SimpleEntity> entities = new HashMap<>();
	Set<SimpleRef> refs = new HashSet<>();

	public DustDataContext(DustDataContext ctxParent) {
		this.ctxParent = ctxParent;
	}
}
