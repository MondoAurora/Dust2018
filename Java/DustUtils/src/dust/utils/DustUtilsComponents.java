package dust.utils;

import java.nio.charset.StandardCharsets;

public interface DustUtilsComponents {
	String ID_SEP = ":";
	String DEFAULT_SEPARATOR = ",";
	String MULTI_FLAG = "*";
	
	String CHARSET_UTF8 = StandardCharsets.UTF_8.name();//"UTF-8";
	
    String CONTENT_JSON = "application/json";
    int NO_PORT_SET = -1;


	interface DumpFormatter {
		
	}
	

	public enum CollectionAction {
		contains, add, remove, clear
	}
	
}
