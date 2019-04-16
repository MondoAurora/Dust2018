package dust.mj02.sandbox.http;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustHttpServer implements DustHttpComponents, DustProcComponents.DustProcActive {

    Server jetty;
    HandlerList handlers;

    HashSessionIdManager sessionIdManager;

    public HashSessionIdManager getSessionIdManager() {
        if (null == sessionIdManager) {
            sessionIdManager = new HashSessionIdManager();
            jetty.setSessionIdManager(sessionIdManager);
        }

        return sessionIdManager;
    }

    public void initConnectors() throws Exception {
        String portPublic = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetServerPublicPort, false);
        String portSsl = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetServerSslPort, false);

        if (null != portPublic) {
            HttpConfiguration http = new HttpConfiguration();

            if (null != portSsl) {
                http.addCustomizer(new SecureRequestCustomizer());
                http.setSecurePort(Integer.parseInt(portSsl));
                http.setSecureScheme("https");
            }

            ServerConnector connector = new ServerConnector(jetty);
            connector.addConnectionFactory(new HttpConnectionFactory(http));
            connector.setPort(Integer.parseInt(portPublic));

            jetty.addConnector(connector);
        }

        if (null != portSsl) {
            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());

            // Configuring SSL
            SslContextFactory sslContextFactory = new SslContextFactory();

            String str;
            str = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetSSLStorePath, false);
            sslContextFactory.setKeyStorePath(ClassLoader.getSystemResource(str).toExternalForm());
            str = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetSSLStorePass, false);
            sslContextFactory.setKeyStorePassword(str);
            str = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetSSLManagerPass, false);
            sslContextFactory.setKeyManagerPassword(str);

            ServerConnector sslConnector = new ServerConnector(jetty, new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(https));
            sslConnector.setPort(Integer.parseInt(portSsl));

            jetty.addConnector(sslConnector);
        }
    }

    public void initHandlers() throws Exception {
        AbstractHandler h = new AbstractHandler() {
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                    throws IOException, ServletException {
                
                response.setCharacterEncoding(UTF8);
                response.setContentType(CONTENT_JSON);
                
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
                
                InputStream is = new FileInputStream("output/temp/TestSingle.json");
                
                OutputStream outStream = response.getOutputStream();
                
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }       
                
                is.close();
            }
        };
        
        handlers.addHandler(h);
    }

    @Override
    public void activeInit() throws Exception {
        jetty = new Server();
        handlers = new HandlerList();

        initConnectors();
        initHandlers();

        jetty.setHandler(handlers);

        jetty.start();
    }

    @Override
    public void activeRelease() throws Exception {
        if (null != jetty) {
            Server j = jetty;
            jetty = null;
            j.stop();
        }
    }

}
