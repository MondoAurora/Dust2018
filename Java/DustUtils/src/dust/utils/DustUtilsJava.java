package dust.utils;

import java.util.Map;

public class DustUtilsJava implements DustUtilsComponents {

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
	public static <RetType extends Enum<RetType>> RetType fromEnumId(String s) throws ClassNotFoundException {
		String[] ee = s.split(ID_SEP);
		return parseEnum(ee[1], (Class<RetType>) Class.forName(ee[0]));
	}

	public static <RetType extends Enum<RetType>> RetType parseEnum(String name, Class<RetType> ec) {
		for (RetType e : ec.getEnumConstants()) {
			if (e.name().equals(name)) {
				return e;
			}
		}

		return null;
	}

	public static boolean isEmpty(String str) {
		return (null == str) ? true : str.isEmpty();
	}

	public static StringBuilder sbApend(StringBuilder sb, Object sep, boolean strict, Object... objects) {
		for (Object ob : objects) {
			String str = toString(ob);

			if (strict || (0 < str.length())) {
				if (null == sb) {
					sb = new StringBuilder(str);
				} else {
					if (0 < sb.length()) {
						sb.append(sep);
					}
					sb.append(str);
				}
			}
		}
		return sb;
	}

	@SuppressWarnings("unchecked")
	public static <Content> Content safeGet(int idx, Object... arr) {
		return ((null != arr) && (0 < idx) && (idx < arr.length)) ? (Content) arr[idx] : null;
	}

	public static boolean isEqual(Object o1, Object o2) {
		return (null == o1) ? (null == o2) : (null == o2) ? false : (o1 == o2) ? true : o1.equals(o2);
	}

	public static boolean isEqualLenient(Object ob, Object opt) {
		return (null == opt) || ((null != ob) && opt.equals(ob));
	}

	public static <RetType extends Enum<RetType>> RetType shiftEnum(RetType e, boolean up, boolean rotate) {
		int ord = e.ordinal();
		@SuppressWarnings("unchecked")
		RetType[] values = (RetType[]) e.getClass().getEnumConstants();
		ord = up ? ++ord : --ord;

		if ((0 <= ord) && (ord < values.length)) {
			return values[ord];
		} else {
			if (rotate) {
				return ((0 > ord) ? values[values.length - 1] : values[0]);
			} else {
				return null;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static StringBuilder toStringBuilder(StringBuilder target, Iterable<?> content, boolean map, String name) {
		if (!isEmpty(name)) {
			target = DustUtilsJava.sbApend(target, "", false, " \"", name, "\": ");
		}
		target = DustUtilsJava.sbApend(target, "", false, map ? "{ " : "[ ");

		boolean empty = true;
		for (Object r : content) {
			if (empty) {
				empty = false;
			} else {
				target.append(", ");
			}
			if (r instanceof Map.Entry) {
				Map.Entry e = (Map.Entry) r;
				Object val = e.getValue();
				if (!(val instanceof DumpFormatter)) {
					val = sbApend(null, "", false, "\"", val, "\"");
				}
				sbApend(target, "", false, " \"", e.getKey(), "\": ", val);
			} else {
				target.append(r);
			}
		}
		target.append(map ? " }" : " ]");
		return target;
	}
}
