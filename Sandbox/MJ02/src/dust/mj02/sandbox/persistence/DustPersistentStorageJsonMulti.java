package dust.mj02.sandbox.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dust.utils.DustUtilsJava;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustPersistentStorageJsonMulti implements
        DustPersistenceComponents, DustPersistenceComponents.PersistentStorage {
    
    private static final String DEF_PATH_PERSISTENCE = "output/persistence";
    private static final String DEF_PATH_HISTORY = "history";

    JSONParser parser = new JSONParser();

    private String pathPersistence = "output/persistence";
    private String pathHistory = "history";

    public static DustPersistentStorageJsonMulti DEFAULT = new DustPersistentStorageJsonMulti(DEF_PATH_PERSISTENCE, DEF_PATH_HISTORY);
    
    public DustPersistentStorageJsonMulti(String pathPersistence,
            String pathHistory) {
        this.pathPersistence = pathPersistence;
        this.pathHistory = pathHistory;
    }
    
    @Override
    public void activeInit() throws Exception {
    }

    @Override
    public void activeRelease() throws Exception {
    }

    private File getFile(String unitId, String commitId) {
        File dirPers = new File(pathPersistence);
        File dirHistory = new File(dirPers, pathHistory);
        dirHistory.mkdirs();

        return DustUtilsJava.isEmpty(commitId)
                ? new File(dirPers, unitId + EXT_JSON)
                : new File(dirHistory, unitId + "_" + commitId + EXT_JSON);
    }

    @Override
    public void save(String commitId, Map<String, Map> result)
            throws Exception {
        for (Map.Entry<String, Map> r : result.entrySet()) {
            Writer fw;
            String key = r.getKey();
            File file = getFile(key, null);
//            fw = new FileWriter(file);
            fw = new OutputStreamWriter(new FileOutputStream(file), CHARSET_UTF8);

            JSONObject.writeJSONString(r.getValue(), fw);
            fw.flush();
            fw.close();

            Files.copy(file.toPath(), getFile(key, commitId).toPath());
        }
    }

    @Override
    public Map<String, Map> load(String unitId, String commitId)
            throws Exception {
        File uf = getFile(unitId, commitId);
        Reader r = new InputStreamReader(new FileInputStream(uf), CHARSET_UTF8);

        return (JSONObject) parser.parse(r);
    }
}
