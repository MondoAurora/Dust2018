package dust.pub;

import dust.gen.dust.base.DustBaseComponents;

public class DustUtilsJava implements DustBaseComponents {

	public static String toString(Object ob) {
		return (null == ob) ? "" : ob.toString();
	}

	public static boolean isEmpty(String str) {
		return (null == str) ? true : str.isEmpty();
	}
	
	public static StringBuilder sbApend(StringBuilder sb, String sep, boolean strict, Object... objects) {
		for (Object ob : objects) {
			String str = toString(ob);

			if (null == sb) {
				sb = new StringBuilder(str);
			} else {
				if (strict || (0 < sb.length())) {
					sb.append(sep);
				}
				sb.append(str);
			}
		}
		return sb;
	}

	@SuppressWarnings("unchecked")
	public static <Content> Content safeGet(int idx, Object... arr ) {
		return ((null != arr) && (0 < idx) && (idx < arr.length)) ? (Content)arr[idx] : null;
	}
}
