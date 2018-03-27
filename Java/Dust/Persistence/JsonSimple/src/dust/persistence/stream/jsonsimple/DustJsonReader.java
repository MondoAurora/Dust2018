package dust.persistence.stream.jsonsimple;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dust.gen.dust.core.exec.DustCoreExecServices;
import dust.gen.dust.tools.generic.DustToolsGenericServices;
import dust.pub.DustException;
import dust.pub.DustUtilsDev;
import dust.pub.DustUtilsJava;
import dust.utils.DustUtilsFactory;

public class DustJsonReader implements DustJsonComponents, DustCoreExecServices.DustCoreExecBlockProcessor, DustToolsGenericServices.DustToolsGenericInitable {

	Map<Object, DustEntity> mapEntities = new HashMap<>();
	
	@Override
	public void dustToolsGenericInitableInit() throws Exception {
		dustCoreExecBlockProcessorBegin();
		dustCoreExecBlockProcessorEnd(null, null);
	}

	@Override
	public void dustCoreExecBlockProcessorBegin() throws Exception {
		Reader r = null; // DustUtils.getFieldValueDef(DF_STREAM_READER);
		JSONParser jp = new JSONParser();

		if (null == r) {
			String fileNames = "cfg*";

			if (!DustUtilsJava.isEmpty(fileNames)) {
				for (String fn : fileNames.split(DEFAULT_SEPARATOR)) {
					boolean recursive = fn.endsWith(MULTI_FLAG);
					if (recursive) {
						fn = fn.substring(0, fn.length() - 1);
					}

					processFile(new File(fn), recursive, jp);
				}
			}
//		} else {
//			read(r, jp);
		}
	}
	
	@Override
	public void dustCoreExecBlockProcessorEnd(DustConstCoreExecVisitorResponse lastResp, Exception optException)
			throws Exception {
	}

	public void processFile(File f, boolean recursive, JSONParser jp) throws Exception {
		if (f.exists()) {
			if (f.isDirectory()) {
				for (File ff : f.listFiles()) {
					if (ff.isDirectory()) {
						if (recursive) {
							processFile(ff, recursive, jp);
						}
					} else if (ff.getName().toLowerCase().endsWith(EXT_JSON)) {
						readFile(ff, jp);
					}
				}
			} else {
				readFile(f, jp);
			}
		}
	}

	public void readFile(File f, JSONParser jp) throws Exception {
		DustUtilsDev.dump("Here comes the init file loading...", f.getAbsolutePath());
		read(new FileReader(f), jp);
	}

	protected void read(Reader r, JSONParser jp) throws Exception {
		jp.parse(r, h);
	}

	class MetaInfo {
		EnumMap<JsonTag, String> info = new EnumMap<>(JsonTag.class);
		Map<String, MetaInfo> attrMap = new TreeMap<>();
	}

	class ReadHandler implements ContentHandler {
		
		String storeId;

		JsonTag state = JsonTag.Root;
		DustUtilsFactory<String, MetaInfo> metaMap = new DustUtilsFactory<String, MetaInfo>(true) {
			@Override
			protected MetaInfo create(String key, Object... hints) {
				return new MetaInfo();
			}
		};

		MetaInfo metaInfo = null;
		MetaInfo metaAtt = null;
		
		String eIdLocal;
		String eIdGlobal;
		
		DustEntity eTarget;

		@Override
		public void startJSON() throws ParseException, IOException {
			 mapEntities.clear();
		}

		@Override
		public void endJSON() throws ParseException, IOException {
		}

		@Override
		public boolean startObject() throws ParseException, IOException {
			switch ( state ) {
			case MetaExt:
				metaInfo = new MetaInfo();
				break;
			case Models:
				if ( null == eTarget ) {
					eTarget = accessEntity(eIdGlobal);
					mapEntities.put(eIdLocal, eTarget);
				} else {
					
				}
				break;
			default:
				break;
			}
			return true;
		}

		@Override
		public boolean endObject() throws ParseException, IOException {
			String alias, owner;
			switch ( state ) {
			case MetaExt:
				alias = metaInfo.info.get(JsonTag.alias);
				owner = metaInfo.info.get(JsonTag.ownerType);
				
				if ( null == owner ) {
					metaMap.get(alias).info.putAll(metaInfo.info);
				} else {
					metaMap.get(owner).attrMap.put(alias, metaInfo);
				}
				metaInfo = null;
				break;
			default:
				break;
			}
			return true;
		}

		@Override
		public boolean startObjectEntry(String key) throws ParseException, IOException {
			if ( JsonTag.Models == state ) {
				if ( null == metaInfo ) {
					metaInfo = metaMap.peek(key);
				} else {
					metaAtt = metaInfo.attrMap.get(key);
				}
			} else {
				state = (JsonTag) DustUtilsJava.parseEnum(key, JsonTag.class);
//				DustUtilsDev.dump(state);
			}
			return true;
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			if ( JsonTag.Models == state ) {
				if ( null == metaAtt) {
					if ( null == metaInfo) {
						eTarget = null;
						state = JsonTag.Entities;
					} else {
						metaInfo = null;
					}
				} else {
					metaAtt = null;
				}
			} else {
				state = state.parent;
			}			
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
			try {
				switch ( state ) {
				case idStore:
					storeId = (String) value;
					break;
				case handler:
					if (!DustUtilsJava.isEqual(CONTENT_HANDLER, value)) {
						DustException.throwException();
					}
					break;
				case version:
					if (!DustUtilsJava.isEqual(CONTENT_VERSION, value)) {
						DustException.throwException();
					}
					break;
				case alias:
				case globalId:
				case ownerType:
					metaInfo.info.put(state, DustUtilsJava.toString(value));
					break;
				case idGlobal:
					eIdGlobal = DustUtilsJava.toString(value);
					break;
				case idLocal:
					eIdLocal = DustUtilsJava.toString(value);
					break;
				case Models:
					if ( null != metaAtt ) {
						setField(eTarget, metaAtt, value);
					}
					break;
				default:
					break;
				
				}
				// streamData.get(state).put(key, value);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		}
	}
	
	ReadHandler h = new ReadHandler();
	
	DustEntity accessEntity(String idGlobal) {
		DustUtilsDev.dump("accessing entity", idGlobal);
		return DustAttributeCoreDataEntity.svcImpl;
	}

	void setField(DustEntity e, MetaInfo mi, Object value) {
		DustUtilsDev.dump("set field value", mi.info.get(JsonTag.globalId), value);
	}

}
