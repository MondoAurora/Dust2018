package dust.mj02.sandbox.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.SwingUtilities;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.sandbox.persistence.DustPersistence;
import dust.mj02.sandbox.persistence.DustPersistentStorageJsonSingle;
import dust.utils.DustUtilsJava;

public class DustHttpClient implements DustHttpComponents, DustProcComponents.DustProcPocessor {

    URL url;

    @Override
    public void processorProcess() throws Exception {
        String urlAddr = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetAddressUrl, false);
        url = new URL(urlAddr);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(2 * 1000);
        conn.setReadTimeout(2 * 1000);
        conn.connect();

        InputStream is = conn.getInputStream();

        String modToUpdate = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetClientModuleToUpdate, false);

        if (DustUtilsJava.isEmpty(modToUpdate)) {
            OutputStream outStream = System.out;

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } else {

            DustPersistentStorageJsonSingle st = new DustPersistentStorageJsonSingle(null);
            st.is = is;

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    DustPersistence.update(st, modToUpdate);
                }
            });
        }

    }

}
