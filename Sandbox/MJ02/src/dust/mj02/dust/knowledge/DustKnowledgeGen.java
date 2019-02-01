package dust.mj02.dust.knowledge;

import java.util.HashMap;
import java.util.Map;

import dust.utils.DustUtilsJava;

@SuppressWarnings("unchecked")
public class DustKnowledgeGen implements DustCommComponents, DustMetaComponents, DustDataComponents {

	private static Map<Object, Object> IDRESOLVER = new HashMap<Object, Object>();
	
	static {
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:AttDef:Bool", DustMetaValueAttDefType.AttDefBool);
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:AttDef:Identifier", DustMetaValueAttDefType.AttDefIdentifier);
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:AttDef:Integer", DustMetaValueAttDefType.AttDefInteger);
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:AttDef:Float", DustMetaValueAttDefType.AttDefFloat);
		
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:LinkDef:Single", DustMetaValueLinkDefType.LinkDefSingle);
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:LinkDef:Set", DustMetaValueLinkDefType.LinkDefSet);
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:LinkDef:Array", DustMetaValueLinkDefType.LinkDefArray);
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:LinkDef:Map", DustMetaValueLinkDefType.LinkDefMap);
		
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:AttDef.Type", DustMetaAtts.AttDefType);
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:LinkDef.Type", DustMetaAtts.LinkDefType);
		
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Data:Entity.PrimaryType", DustDataLinks.EntityPrimaryType);
		
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:Type", DustMetaTypes.Type);
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:AttDef", DustMetaTypes.AttDef);
		DustUtilsJava.biDiPut(IDRESOLVER, "Knowledge:Meta:LinkDef", DustMetaTypes.LinkDef);
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
