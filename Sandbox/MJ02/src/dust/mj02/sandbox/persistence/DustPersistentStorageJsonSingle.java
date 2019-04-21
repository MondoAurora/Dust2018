package dust.mj02.sandbox.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustPersistentStorageJsonSingle implements DustPersistenceComponents, DustPersistenceComponents.PersistentStorage {

    private static final String DEF_PATH = "output/temp";

    private String path = "path";

    private JSONParser parser = new JSONParser();
    private Map<String, Map> map;

    public static DustPersistentStorageJsonSingle DEFAULT = new DustPersistentStorageJsonSingle(DEF_PATH);

    public InputStream is;
    public Writer writer;

    public DustPersistentStorageJsonSingle(String path) {
        this.path = path;
    }

    @Override
    public void activeInit() throws Exception {
    }

    @Override
    public void activeRelease() throws Exception {
        map = null;
    }

    private File getFile(String commitId) {
        File dir = new File(path);
        dir.mkdirs();

        commitId = "TestSingle";

        return new File(dir, commitId + EXT_JSON);
    }

    @Override
    public void save(String commitId, Map<String, Map> result) throws Exception {
        boolean close = true;

        if (null == writer) {
            File file = getFile(commitId);
            writer = new OutputStreamWriter(new FileOutputStream(file), UTF8);
            close = true;
        }
        
        JSONObject.writeJSONString(result, writer);
        
        if (close) {
            writer.flush();
            writer.close();
        }
        writer = null;
    }

    @Override
    public Map<String, Map> load(String unitId, String commitId) throws Exception {
        if (null == map) {
            Reader r;
            if (null == is) {
                File uf = getFile(commitId);
                r = new InputStreamReader(new FileInputStream(uf), UTF8);
            } else {
                r = new InputStreamReader(is, UTF8);
            }
            map = (JSONObject) parser.parse(r);

        }
        return map.get(unitId);
    }
}
