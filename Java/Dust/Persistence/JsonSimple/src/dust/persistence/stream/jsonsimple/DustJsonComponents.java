package dust.persistence.stream.jsonsimple;

import java.io.IOException;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

import dust.pub.DustPubComponents;

public interface DustJsonComponents extends DustPubComponents {
	String CONTENT_HANDLER = "DustJsonReader";
	String CONTENT_VERSION = "1";	

	String EXT_JSON = ".json";	

	enum JsonTag {
		Root(null),
		DustStreamInfo(Root), idStore(DustStreamInfo), handler(DustStreamInfo), version(DustStreamInfo),
		Entities(Root), 
		MetaExt(Entities), globalId(MetaExt), alias(MetaExt), ownerType(MetaExt),
		StoreInfo(Entities), idGlobal(StoreInfo), idLocal(StoreInfo),
		Models(Entities);
		
		JsonTag parent;

		private JsonTag(JsonTag parent) {
			this.parent = parent;
		}
		
	};

	int TARGET_NOT_STORED = -1;
	
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
}
