package dust.gen;

import java.util.HashMap;
import java.util.Map;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;

public class DustUtilsGen implements DustComponents {
	
	private static final Map<Class<? extends IdentifiableMeta>, String> TYPE_PREFIX = new HashMap<>();
	
	static {
		TYPE_PREFIX.put(DustType.class, getTypePrefix(DustKnowledgeMetaComponents.DustTypeKnowledgeMeta.Type) + "|");
		TYPE_PREFIX.put(DustAttribute.class, getTypePrefix(DustKnowledgeMetaComponents.DustTypeKnowledgeMeta.AttDef) + "|");
		TYPE_PREFIX.put(DustLink.class, getTypePrefix(DustKnowledgeMetaComponents.DustTypeKnowledgeMeta.LinkDef) + "|");
		TYPE_PREFIX.put(DustService.class, getTypePrefix(DustKnowledgeMetaComponents.DustTypeKnowledgeMeta.Service) + "|");
		TYPE_PREFIX.put(DustCommand.class, getTypePrefix(DustKnowledgeMetaComponents.DustTypeKnowledgeMeta.Command) + "|");
		TYPE_PREFIX.put(DustConst.class, getTypePrefix(DustKnowledgeMetaComponents.DustTypeKnowledgeMeta.Const) + "|");
	}
		
	private static String[] getTypePath(String cname) {
		String genName = DustUtilsGen.class.getName();
		cname = cname.substring(genName.lastIndexOf('.') + 1);
		
		String[] pnames = cname.substring(0, cname.lastIndexOf('.')).split("\\.");
		
		return pnames;
	}
	
	public static String getTypePrefix(DustType type) {
		String cname = type.getClass().getName();
		String[] pnames = getTypePath(cname);

		StringBuilder id = new StringBuilder();
		for ( String s : pnames ) {
			s = "" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
			id.append(s).append(":");
		}
		
		return id.append(type).toString();
	}
	
	public static String metaToId(IdentifiableMeta meta) {
		String cname = meta.getClass().getName();
		String[] pnames = getTypePath(cname);
		StringBuilder prefix = new StringBuilder();
		StringBuilder id = null;
		for ( String s : pnames ) {
			s = "" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
			prefix.append(s);
			id = (null == id)?new StringBuilder(s) : id.append(":").append(s);
		}
		
		String ret = "";

		int idx = cname.lastIndexOf(prefix.toString());
		if ( -1 != idx ) {
			String name = cname.substring(idx + prefix.length());
			if ((meta instanceof DustService) || (meta instanceof DustType)) {
				id.append(name).append(":").append(meta);				
			} else {
				id.append(":").append(name).append(".").append(meta);
			}
			for ( Map.Entry<Class<? extends IdentifiableMeta>, String> e : TYPE_PREFIX.entrySet() ) {
				if ( e.getKey().isInstance(meta) ) {
					id.insert(0, e.getValue());
					break;
				}
			}
			ret = id.toString();
		}
		
		return ret;
	}
	// 3284 / 3084 94%
	public static DustType getTypeFromId(String id) {
		return null;
	}
	
	// 3284 / 3084 94%
	public static <RetType extends IdentifiableMeta> RetType idToMeta(String id) {
		RetType ret = null;
		
		
		return ret;
	}

}
