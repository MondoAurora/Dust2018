package dust.pub;

public class DustUtilsJava implements DustPubComponents {

	public static String toString(Object ob) {
		return (null == ob) ? "" : ob.toString();
	}

	public static String toLocalId(Enum<?> e) {
		return new StringBuilder(e.getClass().getSimpleName()).append(ID_SEP).append(e.name()).toString();
	}

	public static String toEnumId(Enum<?> e) {
		return new StringBuilder(e.getClass().getName()).append(ID_SEP).append(e.name()).toString();
	}

	@SuppressWarnings("unchecked")
	public static Enum<?> fromEnumId(String s) throws ClassNotFoundException {
		String[] ee = s.split(ID_SEP);
		return parseEnum(ee[1], (Class<? extends Enum<?>>) Class.forName(ee[0]));
//		for ( Enum<?> ec : ((Class<? extends Enum<?>>) Class.forName(ee[0])).getEnumConstants() ) {
//			if ( ec.name().equals(ee[1])) {
//				return ec;
//			}
//		}
//			
//		return null;
	}

	public static Enum<?> parseEnum(String name, Class<? extends Enum<?>> ec) {
		for ( Enum<?> e : ec.getEnumConstants() ) {
			if ( e.name().equals(name)) {
				return e;
			}
		}
			
		return null;
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

	public static boolean isEqual(Object o1, Object o2) {
		return (null == o1) ? (null == o2) : (null == o2) ? false : (o1 == o2) ? true : o1.equals(o2);
	}
}
