package dust.mj02.dust.tools;

import java.util.HashMap;
import java.util.Map;

import dust.utils.DustUtilsJava;

@SuppressWarnings("unchecked")
public class DustToolsGen implements DustGenericComponents {

	private static Map<Object, Object> IDRESOLVER = new HashMap<Object, Object>();
	
	static {
		DustUtilsJava.biDiPut(IDRESOLVER, "Tools:Generic:Identified", DustGenericTypes.Identified);
		DustUtilsJava.biDiPut(IDRESOLVER, "Tools:Generic:Connected", DustGenericTypes.Connected);
		
		DustUtilsJava.biDiPut(IDRESOLVER, "Tools:Generic:Identified.idLocal", DustGenericAtts.identifiedIdLocal);
		
		DustUtilsJava.biDiPut(IDRESOLVER, "Tools:Generic:Connected.Owner", DustGenericLinks.Owner);
		DustUtilsJava.biDiPut(IDRESOLVER, "Tools:Generic:Connected.Extends", DustGenericLinks.Extends);
	}
	
	public static <RetType> RetType  resolve(Object id) { 
		return (RetType) IDRESOLVER.get(id);
	}
	
	public static Map<Object, Object> resolveAll(Map<Object, Object> target, Object... keys) { 
		if (null == target) {
			target = new HashMap<>();
		}
		for ( Object key : keys ) {
			target.put(key, IDRESOLVER.get(key));
		}
		return target;
	}
}
