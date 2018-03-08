package dust.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import dust.pub.DustUtils;

public abstract class DustUtilsFactory<KeyType, ValType> {
	private final Map<KeyType, ValType> content;
	
	public DustUtilsFactory(boolean sorted) {
		this.content = sorted ? new TreeMap<>() : new HashMap<>();
	}
	
	protected abstract ValType create(KeyType key, Object... hints);
	
	protected void initNew(ValType item, KeyType key, Object... hints) {
		
	}
	
	public synchronized ValType peek(KeyType key) {
		return content.get(key);
	}
	
	public synchronized ValType get(KeyType key, Object... hints) {
		ValType v = content.get(key);
		
		if ( null == v ) {
			v = create(key, hints);
			content.put(key, v);
			initNew(v, key, hints);
		}
		
		return v;
	}
	
	public synchronized void clear() {
		content.clear();
	}
	
	public Iterable<KeyType> keys() {
		return content.keySet();
	}

	public void put(KeyType key, ValType value) {
		content.put(key, value);
	}
	
	public boolean drop(ValType value) {
		return content.values().remove(value);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = DustUtils.sbApend(null, "", true, "{");
		
		for ( Map.Entry<KeyType, ValType> e : content.entrySet() ) {
			DustUtils.sbApend(sb, " ", true, e.getKey(), ":", e.getValue());
		}
		DustUtils.sbApend(sb, "", true, "}");
		
		return DustUtils.toString(sb);
	}
}
