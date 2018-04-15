package dust.persistence.jsonsimple;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import dust.utils.DustUtilsDev;

class DustJsonHandlerDump extends DustJsonComponents.ContentHandlerDefault {
	StringBuilder prefix = new StringBuilder();

	@Override
	public boolean startObject() throws ParseException, IOException {
		DustUtilsDev.dump(prefix, "{");
		return true;
	}

	@Override
	public boolean endObject() throws ParseException, IOException {
		DustUtilsDev.dump(prefix, "}");
		return true;
	}

	@Override
	public boolean startObjectEntry(String key) throws ParseException, IOException {
		DustUtilsDev.dump(prefix, key, ":");
		prefix.append("  ");
		return true;
	}

	@Override
	public boolean endObjectEntry() throws ParseException, IOException {
		prefix.delete(0, 2);
		return true;
	}

	@Override
	public boolean startArray() throws ParseException, IOException {
		DustUtilsDev.dump(prefix, "[");
		return true;
	}

	@Override
	public boolean endArray() throws ParseException, IOException {
		DustUtilsDev.dump(prefix, "]");
		return true;
	}

	@Override
	public boolean primitive(Object value) throws ParseException, IOException {
		try {
			DustUtilsDev.dump(prefix, "\"", value, "\"");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
}