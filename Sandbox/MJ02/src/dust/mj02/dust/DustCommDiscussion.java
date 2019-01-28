package dust.mj02.dust;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustCommDiscussion implements DustCommComponents {
		
	DustUtilsFactory<Object, Map<Object, Object>> entities = new DustUtilsFactory<Object, Map<Object, Object>>(true) {
		@Override
		protected Map<Object, Object> create(Object key, Object... hints) {
			DustUtilsDev.dump("Creating entity", key);
			return new HashMap<>();
		}
	};

	public DustCommDiscussion() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public void load(SourceReader rdr, Object... sources) throws Exception {
		Set<Map<Object, Object>> srcData = new HashSet<>();
		
		for ( Object src : sources ) {
			Map<Object, Object> sd = rdr.load(src);
			srcData.add(sd);
			
			Object keyStoreId = sd.get(CommKeys.KeyCommIdStore);
			
			for ( Object o : sd.values() ) {
				if ( o instanceof Map ) {
					String si = DustUtilsJava.getByPath(o, keyStoreId);
					Map<Object, Object> e = entities.get(si);
					e.put(keyStoreId, si);
				}
			}
		}
		
		for ( Map<Object, Object> sd : srcData ) {			
			Object keyStoreId = sd.get(CommKeys.KeyCommIdStore);
			
			DustUtilsFactory<Object, Map<Object, Object>> attRefs = new DustUtilsFactory<Object, Map<Object, Object>>(true) {
				@Override
				protected Map<Object, Object> create(Object key, Object... hints) {
					Object localAtt = sd.get(key);
					String si = DustUtilsJava.getByPath(localAtt, keyStoreId);
					return entities.get(si);
				}
			};
			
			for ( Object o : sd.values() ) {
				if ( o instanceof Map ) {
					String si = DustUtilsJava.getByPath(o, keyStoreId);
					Map<Object, Object> entity = entities.get(si);

					DustUtilsDev.dump("Loading entity data", si);

					for (Map.Entry<Object, Object> eAtts : ((Map<Object, Object>) o).entrySet()) {
						Object attDef = attRefs.get(eAtts.getKey());
						
						DustUtilsDev.dump("  set att", DustUtilsJava.getByPath(attDef, keyStoreId), "to", eAtts.getValue());
					}
				}
			}
		}
	}

}
