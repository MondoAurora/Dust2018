package dust.mj02.dust;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DustCommGen implements DustCommComponents {

	private static Map<Object, Object> IDRESOLVER = new HashMap<Object, Object>();
	
	static {
		IDRESOLVER.put("Knowledge:Meta:AttDef:Bool", CommAttDefTypes.AttDefBool);
		IDRESOLVER.put("Knowledge:Meta:AttDef:Identifier", CommAttDefTypes.AttDefIdentifier);
		IDRESOLVER.put("Knowledge:Meta:AttDef:Integer", CommAttDefTypes.AttDefInteger);
		IDRESOLVER.put("Knowledge:Meta:AttDef:Float", CommAttDefTypes.AttDefFloat);
		
		IDRESOLVER.put("Knowledge:Meta:LinkDef:Single", CommLinkDefTypes.LinkDefSingle);
		IDRESOLVER.put("Knowledge:Meta:LinkDef:Set", CommLinkDefTypes.LinkDefSet);
		IDRESOLVER.put("Knowledge:Meta:LinkDef:Array", CommLinkDefTypes.LinkDefArray);
		IDRESOLVER.put("Knowledge:Meta:LinkDef:Map", CommLinkDefTypes.LinkDefMap);
		
		IDRESOLVER.put("Knowledge:Meta:AttDef:Type", CommDiscKeys.AttDefType);
		IDRESOLVER.put("Knowledge:Meta:LinkDef:Type", CommDiscKeys.LinkDefType);
	}
	
	public static <RetType> RetType  resolveStoreId(Object id) { 
		return (RetType) IDRESOLVER.get(id);
	}
}
