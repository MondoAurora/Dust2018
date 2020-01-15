package dust.mj02.sandbox.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dust.mj02.dust.Dust;
import dust.mj02.sandbox.persistence.DustPersistence.SaveContext;
import dust.utils.DustUtilsDev;
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
        
        String cp = uf.getCanonicalFile().getName();
        if ( !cp.startsWith(unitId) ) {
            throw new DustException("unit name mismatch! " + unitId + ".json <> " + cp);
        }
        
        Reader r = new InputStreamReader(new FileInputStream(uf), CHARSET_UTF8);

        Map<String, Map> ret = (JSONObject) parser.parse(r);
        
        r.close();
        
        return ret;
    }
    
    public void restore(String timestamp) {
        File dirPers = new File(pathPersistence);
        File dirHistory = new File(dirPers, pathHistory);
        boolean toLatest = false;
        
        if ( DustUtilsJava.isEmpty(timestamp) ) {
            timestamp = SaveContext.SDF.format(new Date());
            toLatest = true;
        }

        String ext = ".json";
        String search = MessageFormat.format("_{0}" + ext, timestamp);
        
        int postifxLen = search.length();
        int extLen = ext.length();
        
        Map<String, File> currentState = new HashMap<>();

        for (File f : dirPers.listFiles()) {
            String fn = f.getName();
            currentState.put(fn.substring(0, fn.length()-extLen), f);
        }

        Map<String, File> toCopy = new HashMap<>();

        for (File f : dirHistory.listFiles()) {
            String fn = f.getName();
            
            String unitName = fn.substring(0, fn.length()-postifxLen);
            String postfix = fn.substring(unitName.length());
            
            File cf = currentState.get(unitName);
            File hf = toCopy.get(unitName);
            
            if ( null != cf ) {
                int comp = search.compareTo(postfix);                
//                if ( "MiNDTest01".equals(unitName) ) {
//                    DustUtilsDev.dump("here");
//                }
                
                if ( 0 <= comp ) {
                    long cm = cf.lastModified();
                    long fm = f.lastModified();
                    long hm = (null == hf) ? 0 : hf.lastModified();
                    
                    if ( (toLatest || (cm >= fm)) && (fm > hm) ) {
                        toCopy.put(unitName, f);
                    }
                }
            }
        }

        try {
            DustUtilsDev.dump("Restoring timestamp", timestamp);
            
            int count = 0;

            for (Map.Entry<String, File> tc : toCopy.entrySet()) {
                File fc = tc.getValue();
                String unitName = tc.getKey();
                
                if ( fc.lastModified() == currentState.get(unitName).lastModified() ) {
                    // same file
                    continue;
                }
                
                File target = new File(dirPers, tc.getKey() + ext);
                Path pT = target.toPath();
                Path pS = fc.toPath();
                Files.copy(pS, pT, StandardCopyOption.REPLACE_EXISTING);
                DustUtilsDev.dump("Copied", pS, "to", pT);
                ++count;
            }

            DustUtilsDev.dump("Successfully verified", toCopy.size(), "restored", count, "files.");
        } catch (Throwable e) {
            Dust.wrapAndRethrowException("Restore failed", e);
        }
    }
}
