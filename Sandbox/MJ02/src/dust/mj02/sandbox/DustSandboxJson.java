package dust.mj02.sandbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dust.mj02.dust.Dust;

public class DustSandboxJson implements DustSandboxComponents {
    private static String fName = "config.json";

    private static JSONParser parser = new JSONParser();

    private static JSONObject root;

    private static void save() {
        OutputStreamWriter writer;

        try {
            if (null != root) {
                File file = new File(fName);
                writer = new OutputStreamWriter(new FileOutputStream(file), CHARSET_UTF8);
                JSONObject.writeJSONString(root, writer);

                writer.flush();
                writer.close();
            }
        } catch (Throwable t) {
            Dust.wrapAndRethrowException("saving config", t);
        }
    }

    private static void load() {
        try {
            File file = new File(fName);

            if (file.exists()) {
                Reader r = new InputStreamReader(new FileInputStream(file), CHARSET_UTF8);
                root = (JSONObject) parser.parse(r);
            } else {
                root = new JSONObject();
            }
        } catch (Throwable t) {
            Dust.wrapAndRethrowException("loading config", t);
        }

    }

    @SuppressWarnings("unchecked")
    public static <RetType> RetType configGet(String... path) {
        if (null == root) {
            load();
        }

        Object o = root;

        for (String key : path) {
            o = ((JSONObject) o).get(key);
            if (null == o) {
                return null;
            }
        }

        return (RetType) o;
    }

    @SuppressWarnings("unchecked")
    public static void configSet(Object val, String... path) {
        if (null == root) {
            load();
        }

        Object o = root;
        int pl = path.length - 1;

        for (int i = 0; i < pl; ++i) {
            String key = path[i];

            Object no = ((JSONObject) o).get(key);
            if (null == no) {
                no = new JSONObject();
                ((JSONObject) o).put(key, no);
            }
            o = no;
        }
        
        ((JSONObject) o).put(path[pl], val);

        save();
    }
}
