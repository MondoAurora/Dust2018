package dust.mj02.dust;

import java.io.FileReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.parser.JSONParser;

import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;

@SuppressWarnings("unchecked")
public class DustCommReader {

	enum KnownKeys {
		Head, CommSrcHandler, KeyCommIdLocal, KeyCommIdStore, Body;

		<RetType> RetType get(Object from) {
			return (RetType) ((Map<String, Object>) from).get(name());
		}

		public boolean isKey() {
			return name().startsWith("Key");
		}
	}

	EnumMap<KnownKeys, String> termKeys = new EnumMap<>(KnownKeys.class);
	DustUtilsFactory<Object, Map<Object, Object>> entities = new DustUtilsFactory<Object, Map<Object, Object>>(true) {
		@Override
		protected Map<Object, Object> create(Object key, Object... hints) {
			DustUtilsDev.dump("Creating entity", key);
			return new HashMap<>();
		}
	};

	public DustCommReader() {
		// TODO Auto-generated constructor stub
	}

	public void load(String fileName, Map<Object, Object> local) throws Exception {
		JSONParser p = new JSONParser();

		Object o = p.parse(new FileReader(fileName));

		if (o instanceof Map) {
			Object head = KnownKeys.Head.get(o);
			for (KnownKeys k : KnownKeys.values()) {
				if (k.isKey()) {
					termKeys.put(k, k.get(head));
				}
			}

			List<Object> content = KnownKeys.Body.get(o);

			for (Object item : content) {
				local.put(getTermValue(item, KnownKeys.KeyCommIdLocal), item);
			}

			for (Object item : content) {
				Object idStore = getTermValue(item, KnownKeys.KeyCommIdStore);

				Map<Object, Object> entity = entities.get(idStore);

				for (Map.Entry<Object, Object> eAtts : ((Map<Object, Object>) item).entrySet()) {
					Object att = local.get(eAtts.getKey());
					if (null == att) {
						DustUtilsDev.dump("Missing local att definition", att);
					} else {
						Object attId = getTermValue(att, KnownKeys.KeyCommIdStore);
//						Object eAtt = entities.get(attId);
						Object val = eAtts.getValue();
						entity.put(attId, val);
					}
				}
			}
			
			local.put("", KnownKeys.KeyCommIdStore.get(head));

			DustUtilsDev.dump("done");
		}
	}

	private Object getTermValue(Object from, KnownKeys... termKey) {
		Object ret = from;
		for (KnownKeys k : termKey) {
			ret = ((Map<String, Object>) ret).get(termKeys.get(k));
		}
		return ret;
	}
}
