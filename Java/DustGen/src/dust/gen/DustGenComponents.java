package dust.gen;

import java.util.regex.Pattern;

import dust.pub.DustComponents;

public interface DustGenComponents extends DustComponents {
	char SEP_PATH = '.';
	
	char SEP_ATT = ':';
	
	String INTERFACE_NAME_PREFIX = "Dust";
	String INTERFACE_NAME_POSTFIX = "Components";
	
	Pattern PTRN_OWNER_FINDER = Pattern.compile(INTERFACE_NAME_PREFIX + "(.*)" + INTERFACE_NAME_POSTFIX);
	Pattern PTRN_OWNER_SPLITTER = Pattern.compile("[A-Z][a-z0-9]*");
	
	interface DustEntityWrapper {
		DustEntity entity();
	}
	
	interface DustEntityAttribute extends DustEntityWrapper {
		<ValType> ValType getValue(DustEntity entity);
		void setValue(DustEntity entity, Object value);
	}
	
	interface DustEntityLink extends DustEntityWrapper {
		void process(DustEntity entity, DustRefVisitor proc);
		DustEntity get(DustEntity entity, boolean createIfMissing, Object key);
		DustEntity modify(DustEntity entity, DustRefCommand cmd, DustEntity target, Object key);
	}
	
}
