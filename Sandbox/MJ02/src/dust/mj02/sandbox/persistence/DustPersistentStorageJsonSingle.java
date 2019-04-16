package dust.mj02.sandbox.persistence;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustPersistentStorageJsonSingle implements
        DustPersistenceComponents, DustPersistenceComponents.PersistentStorage {

    private static final String DEF_PATH = "output/temp";

    private String path = "path";

    private JSONParser parser = new JSONParser();
    private Map<String, Map> map;

    public static DustPersistentStorageJsonSingle DEFAULT = new DustPersistentStorageJsonSingle(DEF_PATH);

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
    public void save(String commitId, Map<String, Map> result)
            throws Exception {
        File file = getFile(commitId);
        Writer fw = new FileWriter(file);
        JSONObject.writeJSONString(result, fw);
        fw.flush();
        fw.close();
    }

    @Override
    public Map<String, Map> load(String unitId, String commitId)
            throws Exception {
        if ( null == map ) {
            File uf = getFile(commitId);
            map = (JSONObject) parser.parse(new FileReader(uf));
        }
        return map.get(unitId);
    }
}
