package dust.mj02.dust;

import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.JSONParser;

import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsJava;

@SuppressWarnings("unchecked")
public class DustCommJsonLoader implements DustCommComponents, DustCommComponents.SourceReader {

	enum JsonKeys {
		Head, Body;
	}

	@Override
	public Map<Object, Object> load(Object src) throws Exception {
		Map<Object, Object> local = new HashMap<>();
		JSONParser p = new JSONParser();

		Object o = p.parse(new FileReader((String) src));

		if (o instanceof Map) {
			Object head = DustUtilsJava.getByPath(o, JsonKeys.Head);
			Object localKey = DustUtilsJava.getByPath(head, CommKeys.KeyCommIdLocal);

			List<Object> content = DustUtilsJava.getByPath(o, JsonKeys.Body);
			for (Object item : content) {
				Object lKey = ((Map<String, Object>) item).get(localKey);
				local.put(lKey, item);
			}
			
			Set<Object> missingKeys = new HashSet<Object>();
			for (Object item : content) {
				for (Map.Entry<Object, Object> eAtts : ((Map<Object, Object>) item).entrySet()) {
					Object lkey = eAtts.getKey();
					if ((null == local.get(lkey)) && missingKeys.add(lkey)) {
						DustUtilsDev.dump("Missing local att definition", lkey);
					}
				}
			}
			
//			local.put(CommKeys.KeyCommIdLocal, localKey);
//			local.put(CommKeys.KeyCommIdStore, DustUtilsJava.getByPath(head, CommKeys.KeyCommIdStore));
			local.put("", DustUtilsJava.getByPath(head, CommKeys.KeyCommIdStore));

			DustUtilsDev.dump(src, "loaded.");
		}
		
		return local;
	}
}
