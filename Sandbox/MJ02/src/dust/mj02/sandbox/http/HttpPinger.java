package dust.mj02.sandbox.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;

public class HttpPinger implements HttpComponents, DustProcComponents.DustProcPocessor {
    
    URL url;

    @Override
    public void processorProcess() throws Exception {
//        if ( null == url ) 
        {
            String urlAddr = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetAddressUrl, false);
            url = new URL(urlAddr);
        }
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(2*1000);
        conn.setReadTimeout(2*1000);
        conn.connect();        
        
        InputStream is = conn.getInputStream();
        
        OutputStream outStream = System.out;
        
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }        
    }

}
