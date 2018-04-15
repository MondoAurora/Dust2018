package dust.gen;

import java.util.HashMap;
import java.util.Map;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;

public class DustUtilsGen implements DustComponents, DustKnowledgeMetaComponents {

	private static final Map<Class<? extends IdentifiableMeta>, String> TYPE_PREFIX = new HashMap<>();
	
	static {
		TYPE_PREFIX.put(DustType.class, getTypePrefix(DustTypeKnowledgeMeta.Type));
		TYPE_PREFIX.put(DustAttribute.class, getTypePrefix(DustTypeKnowledgeMeta.AttDef));
		TYPE_PREFIX.put(DustLink.class, getTypePrefix(DustTypeKnowledgeMeta.LinkDef));
		TYPE_PREFIX.put(DustService.class, getTypePrefix(DustTypeKnowledgeMeta.Service));
		TYPE_PREFIX.put(DustCommand.class, getTypePrefix(DustTypeKnowledgeMeta.Command));
		TYPE_PREFIX.put(DustConst.class, getTypePrefix(DustTypeKnowledgeMeta.Const));
	}
		
	private static String[] getTypePath(String cname) {
		String genName = DustUtilsGen.class.getName();
		cname = cname.substring(genName.lastIndexOf('.') + 1);
		
		String[] pnames = cname.substring(0, cname.lastIndexOf('.')).split("\\.");
		
		return pnames;
	}

	public static String getMetaType(IdentifiableMeta meta) {
		for (Map.Entry<Class<? extends IdentifiableMeta>, String> e : TYPE_PREFIX.entrySet()) {
			if (e.getKey().isInstance(meta)) {
				return e.getValue();
			}
		}

		throw new RuntimeException("hmm...");
	}

	private static String getTypePrefix(IdentifiableMeta type) {
		String cname = type.getClass().getName();
		String[] pnames = getTypePath(cname);

		StringBuilder id = new StringBuilder();
		for (String s : pnames) {
			s = "" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
			id.append(s).append(":");
		}

		return id.append(type).toString();
	}

	public static String metaToStoreId(IdentifiableMeta meta) {
		String cname = meta.getClass().getName();
		String[] pnames = getTypePath(cname);
		StringBuilder prefix = new StringBuilder();
		StringBuilder id = null;
		for (String s : pnames) {
			s = "" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
			prefix.append(s);
			id = (null == id) ? new StringBuilder(s) : id.append(":").append(s);
		}

		String ret = "";

		int idx = cname.lastIndexOf(prefix.toString());
		if (-1 != idx) {
			String name = cname.substring(idx + prefix.length());
			if ((meta instanceof DustService) || (meta instanceof DustType)) {
				id.append(name).append(":").append(meta);
			} else {
				id.append(":").append(name).append(".").append(meta);
			}
			ret = id.toString();
		}

		return ret;
	}

	public static DustEntity getTypeFromId(String id) {
		return null;
	}

	public static <RetType extends IdentifiableMeta> RetType idToMeta(String id) {
		RetType ret = null;

		return ret;
	}

}
