package dust.runtime.simple;

public class DustSimpleUtils implements DustSimpleRuntimeComponents {

	public static String makeFieldId(String type, String name) {
		return type + IDSEP + name;
	}

}
