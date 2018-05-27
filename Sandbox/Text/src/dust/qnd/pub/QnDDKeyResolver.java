package dust.qnd.pub;

import java.util.Map;
import java.util.TreeMap;

public class QnDDKeyResolver implements QnDDComponents {

	private static final Map<String, Class<Enum<?>>> keyEnums = new TreeMap<String, Class<Enum<?>>>();
	
	@SafeVarargs
	public static void register( Class<Enum<?>>...keys ) {
		for ( Class<Enum<?>> k : keys ) {
			String sn = k.getSimpleName();
			Class<Enum<?>> o = keyEnums.put(sn, k);
			
			if ((null != o) && (o != k) ) {
				QnDDException.throwException("Repeated key name", sn, o, k);
			}
		}
	}
	
	public static Class<Enum<?>> getEnumClass(String name) {
		return keyEnums.get(name);
	}
	
}
