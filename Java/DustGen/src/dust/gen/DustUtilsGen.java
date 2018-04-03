package dust.gen;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;

public class DustUtilsGen implements DustComponents {
	
	public static <RetType extends IdentifiableMeta> RetType metaFromId(String id) {
		return null;
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
		for ( String s : pnames ) {
			s = "" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
			prefix.append(s);
		}
		
		String ret = "";

		int idx = cname.lastIndexOf(prefix.toString());
		if ( -1 != idx ) {
			String name = cname.substring(idx + prefix.length());
			ret = prefix.insert(0, ":").insert(0, getTypePrefix(DustKnowledgeMetaComponents.DustTypeKnowledgeMeta.AttDef)).append(name).append(".").append(meta).toString();
		}
		
		return ret;
	}
	

}
