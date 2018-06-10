package dust.qnd.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import dust.qnd.pub.QnDDEnvironment;

public class QnDDUtils implements QnDDUtilsComponents {
	
	private static ResourceBundle res;
	private static QnDDEnvironment env;
	
	public static void init(QnDDEnvironment env) {
		if ( null == res ) {
			res = ResourceBundle.getBundle(RES_MESSAGES_NAME);
			QnDDUtils.env = env;
		}
	}
	
	public static QnDDEnvironment getEnv() {
		return env;
	}
	
	public static String getEnumKey(Enum<?> e) {
		String key = e.getClass().getSimpleName() + "." + e.name();
		return key;
	}
	
	public static String formatEnum(Enum<?> e, Object... params) {
		String key = getEnumKey(e);
		if ( !res.containsKey(key) ) {
			return key;
		} 

		String fmt = res.getString(key);
		
		if ( 0 < params.length ) {
			fmt = MessageFormat.format(fmt, params);
		}
		
		return fmt;
	}
}
