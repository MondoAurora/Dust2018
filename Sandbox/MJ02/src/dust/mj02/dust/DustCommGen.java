package dust.mj02.dust;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DustCommGen implements DustCommComponents, DustMetaComponents {

	private static Map<Object, Object> IDRESOLVER = new HashMap<Object, Object>();
	
	static {
		biDiPut("Knowledge:Meta:AttDef:Bool", DustMetaValueAttDefType.AttDefBool);
		biDiPut("Knowledge:Meta:AttDef:Identifier", DustMetaValueAttDefType.AttDefIdentifier);
		biDiPut("Knowledge:Meta:AttDef:Integer", DustMetaValueAttDefType.AttDefInteger);
		biDiPut("Knowledge:Meta:AttDef:Float", DustMetaValueAttDefType.AttDefFloat);
		
		biDiPut("Knowledge:Meta:LinkDef:Single", DustMetaValueLinkDefType.LinkDefSingle);
		biDiPut("Knowledge:Meta:LinkDef:Set", DustMetaValueLinkDefType.LinkDefSet);
		biDiPut("Knowledge:Meta:LinkDef:Array", DustMetaValueLinkDefType.LinkDefArray);
		biDiPut("Knowledge:Meta:LinkDef:Map", DustMetaValueLinkDefType.LinkDefMap);
		
		biDiPut("Knowledge:Meta:AttDef:Type", DustMetaAtts.AttDefType);
		biDiPut("Knowledge:Meta:LinkDef:Type", DustMetaAtts.LinkDefType);
		
		biDiPut("Knowledge:Info:Entity.PrimaryType", DustMetaAtts.AttPrimaryType);
		biDiPut("Knowledge:Meta:AttDef", DustMetaAtts.TypeAtt);
		biDiPut("Knowledge:Meta:LinkDef", DustMetaAtts.TypeLinkDef);
	}
	
	private static void biDiPut(Object o1, Object o2) { 
		IDRESOLVER.put(o1, o2);
		IDRESOLVER.put(o2, o1);
	}
	
	public static <RetType> RetType  resolve(Object id) { 
		return (RetType) IDRESOLVER.get(id);
	}
}
