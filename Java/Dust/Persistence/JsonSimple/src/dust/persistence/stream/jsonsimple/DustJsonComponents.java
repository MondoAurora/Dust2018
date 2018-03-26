package dust.persistence.stream.jsonsimple;

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
}
