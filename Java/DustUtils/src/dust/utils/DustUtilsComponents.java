package dust.utils;

import java.nio.charset.StandardCharsets;

public interface DustUtilsComponents {
	String ID_SEP = ":";
	String DEFAULT_SEPARATOR = ",";
	String MULTI_FLAG = "*";
	
	String CHS_UTF8 = StandardCharsets.UTF_8.name();//"UTF-8";

	interface DumpFormatter {
		
	}
	
}
