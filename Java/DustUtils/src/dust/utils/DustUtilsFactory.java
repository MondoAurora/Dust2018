package dust.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import dust.pub.DustPubComponents;
import dust.pub.DustUtilsJava;

public abstract class DustUtilsFactory<KeyType, ValType> implements DustPubComponents.DumpFormatter {
	String name;
	protected final Map<KeyType, ValType> content;

	public DustUtilsFactory(boolean sorted) {
		this(sorted, null);
	}

	public DustUtilsFactory(boolean sorted, String name) {
		this.content = sorted ? new TreeMap<>() : new HashMap<>();
		this.name = name;
	}

	protected abstract ValType create(KeyType key, Object... hints);

	protected void initNew(ValType item, KeyType key, Object... hints) {

	}

	public synchronized ValType peek(KeyType key) {
		return content.get(key);
	}

	public synchronized ValType get(KeyType key, Object... hints) {
		ValType v = content.get(key);

		if (null == v) {
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

	public StringBuilder toStringBuilder(StringBuilder target) {
		return DustUtilsJava.toStringBuilder(target, content.entrySet(), true, name);
	}

	@Override
	public String toString() {
		// StringBuilder sb = DustUtils.sbApend(null, "", true, "{");
		//
		// for ( Map.Entry<KeyType, ValType> e : content.entrySet() ) {
		// DustUtils.sbApend(sb, " ", true, e.getKey(), ":", e.getValue());
		// }
		// DustUtils.sbApend(sb, "", true, "}");

		return toStringBuilder(null).toString();
	}
}
