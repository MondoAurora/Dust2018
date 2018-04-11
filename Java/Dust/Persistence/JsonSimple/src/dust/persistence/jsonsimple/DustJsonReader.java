package dust.persistence.jsonsimple;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;

import dust.gen.tools.persistence.DustToolsPersistenceComponents;
import dust.pub.DustUtilsDev;
import dust.pub.DustUtilsJava;

public class DustJsonReader implements DustJsonComponents, DustToolsPersistenceComponents.DustToolsPersistenceStore {
	
	@Override
	public void dustToolsGenericInitableInit() throws Exception {
		dustToolsPersistenceStoreRead();
	}
	
	@Override
	public void dustToolsPersistenceStoreRead() throws Exception {
		dustKnowledgeProcProcessorBegin();
		dustKnowledgeProcProcessorEnd();
	}

	@Override
	public void dustKnowledgeProcProcessorBegin() throws Exception {
		Reader r = null; // DustUtils.getFieldValueDef(DF_STREAM_READER);
		JSONParser jp = new JSONParser();

		if (null == r) {
			String fileNames = "cfg2*";

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
	public void dustKnowledgeProcProcessorEnd() throws Exception {
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

	ContentHandler h = new DustJsonHandler2();
	
}
