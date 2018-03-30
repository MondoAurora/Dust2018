package dust.persistence.jsonsimple;

import java.io.IOException;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

import dust.pub.DustPubComponents;

public interface DustJsonComponents extends DustPubComponents {
	String EXT_JSON = ".json";	
	
	class ContentHandlerDefault implements ContentHandler {
		@Override
		public void startJSON() throws ParseException, IOException {
		}

		@Override
		public void endJSON() throws ParseException, IOException {
		}

		@Override
		public boolean startObject() throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean endObject() throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean startObjectEntry(String key) throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean startArray() throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean endArray() throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean primitive(Object value) throws ParseException, IOException {
			return true;
		}
	}
	
	abstract class ContentHandlerRelay implements ContentHandler {
		ContentHandler relay;
		
		public void setRelay(ContentHandler relay) {
			this.relay = relay;
		}

		@Override
		public boolean startObject() throws ParseException, IOException {
			return (null == relay) ? true : relay.startObject();
		}

		@Override
		public boolean endObject() throws ParseException, IOException {
			return (null == relay) ? true : relay.endObject();
		}

		@Override
		public boolean startObjectEntry(String key) throws ParseException, IOException {
			return (null == relay) ? true : relay.startObjectEntry(key);
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			return (null == relay) ? true : relay.endObjectEntry();
		}

		@Override
		public boolean startArray() throws ParseException, IOException {
			return (null == relay) ? true : relay.startArray();
		}

		@Override
		public boolean endArray() throws ParseException, IOException {
			return (null == relay) ? true : relay.endArray();
		}

		@Override
		public boolean primitive(Object value) throws ParseException, IOException {
			return (null == relay) ? true : relay.primitive(value);
		}
	}
}
