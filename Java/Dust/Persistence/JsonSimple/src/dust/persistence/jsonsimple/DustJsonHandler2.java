package dust.persistence.jsonsimple;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import dust.gen.tools.persistence.DustToolsPersistenceComponents.DustStatusToolsPersistence;
import dust.pub.DustException;
import dust.pub.DustUtilsJava;

class DustJsonHandler2 extends DustJsonComponents.ContentHandlerRelay implements DustJsonComponents {

	private static final String CONTENT_HANDLER = "DustJsonTalkReader";
	private static final String CONTENT_VERSION = "1";

	DustJsonHandlerComm talkHandler = new DustJsonHandlerComm();
//	ContentHandler talkHandler = new DustJsonHandlerDump();

	JsonTag state;

	enum JsonTag {
		Head, handler, version, Body
	}

	@Override
	public void startJSON() throws ParseException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endJSON() throws ParseException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean startArray() throws ParseException, IOException {
		if (null == relay) {
			setRelay(talkHandler);
		}
		return super.startArray();
	}
	
	@Override
	public boolean endArray() throws ParseException, IOException {
		super.endArray();
		if ( null == talkHandler.state ) {
			setRelay(null);
		}
		return true;
	}

	@Override
	public boolean startObjectEntry(String key) throws ParseException, IOException {
		if (null == relay) {
			state = DustUtilsJava.parseEnum(key, JsonTag.class);
			return true;
		} else {
			return super.startObjectEntry(key);
		}
	}

	@Override
	public boolean primitive(Object value) throws ParseException, IOException {
		if (null == relay) {
			switch (state) {
			case handler:
				if (!DustUtilsJava.isEqual(CONTENT_HANDLER, value)) {
					DustException.throwException(DustStatusToolsPersistence.HandlerInvalid, CONTENT_HANDLER, value);
				}
				break;
			case version:
				if (!DustUtilsJava.isEqual(CONTENT_VERSION, value)) {
					DustException.throwException(DustStatusToolsPersistence.HandlerVersionMismatch, CONTENT_VERSION,
							value);
				}
				break;
			default:
				break;
			}
			return true;
		} else {
			return super.primitive(value);
		}
	}
}