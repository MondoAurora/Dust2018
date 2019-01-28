package dust.mj02.dust;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DustCommGen implements DustCommComponents {

	private static Map<Object, Object> IDRESOLVER = new HashMap<Object, Object>();
	
	static {
		biDiPut("Knowledge:Meta:AttDef:Bool", CommAttDefTypes.AttDefBool);
		biDiPut("Knowledge:Meta:AttDef:Identifier", CommAttDefTypes.AttDefIdentifier);
		biDiPut("Knowledge:Meta:AttDef:Integer", CommAttDefTypes.AttDefInteger);
		biDiPut("Knowledge:Meta:AttDef:Float", CommAttDefTypes.AttDefFloat);
		
		biDiPut("Knowledge:Meta:LinkDef:Single", CommLinkDefTypes.LinkDefSingle);
		biDiPut("Knowledge:Meta:LinkDef:Set", CommLinkDefTypes.LinkDefSet);
		biDiPut("Knowledge:Meta:LinkDef:Array", CommLinkDefTypes.LinkDefArray);
		biDiPut("Knowledge:Meta:LinkDef:Map", CommLinkDefTypes.LinkDefMap);
		
		biDiPut("Knowledge:Meta:AttDef:Type", CommDiscKeys.AttDefType);
		biDiPut("Knowledge:Meta:LinkDef:Type", CommDiscKeys.LinkDefType);
		
		biDiPut("Knowledge:Info:Entity.PrimaryType", CommDiscKeys.AttPrimaryType);
		biDiPut("Knowledge:Meta:AttDef", CommDiscKeys.TypeAtt);
		biDiPut("Knowledge:Meta:LinkDef", CommDiscKeys.TypeLinkDef);
	}
	
	private static void biDiPut(Object o1, Object o2) { 
		IDRESOLVER.put(o1, o2);
		IDRESOLVER.put(o2, o1);
	}
	
	public static <RetType> RetType  resolve(Object id) { 
		return (RetType) IDRESOLVER.get(id);
	}
}
