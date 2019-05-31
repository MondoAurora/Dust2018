package dust.mj02.sandbox.http;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import dust.mj02.dust.DustUtils;

public class DustHttpServerJetty extends DustHttpServerBase {
    Server jetty;
    HandlerList handlers;
    ServletContextHandler ctxHandler;

    HashSessionIdManager sessionIdManager;

    @Override
    public void activeInit() throws Exception {
        jetty = new Server();
        handlers = new HandlerList();

        super.activeInit();

        jetty.setHandler(handlers);
        jetty.start();
    }

    @Override
    public void activeRelease() throws Exception {
        if (null != jetty) {
            super.activeRelease();

            Server j = jetty;
            jetty = null;
            handlers = null;
            ctxHandler = null;

            sessionIdManager = null;

            j.stop();
        }
    }

    @Override
    protected void initConnectorSsl(int portSsl) {
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        // Configuring SSL
        SslContextFactory sslContextFactory = new SslContextFactory();

        String str;
        str = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetSslInfoStorePath, false);
        sslContextFactory.setKeyStorePath(ClassLoader.getSystemResource(str).toExternalForm());
        str = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetSslInfoStorePass, false);
        sslContextFactory.setKeyStorePassword(str);
        str = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetSslInfoManagerPass, false);
        sslContextFactory.setKeyManagerPassword(str);

        ServerConnector sslConnector = new ServerConnector(jetty, new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(portSsl);

        jetty.addConnector(sslConnector);
    }

    @Override
    protected void initConnectorPublic(int portPublic, int portSsl) {
        HttpConfiguration http = new HttpConfiguration();

        if (NO_PORT_SET != portSsl) {
            http.addCustomizer(new SecureRequestCustomizer());
            http.setSecurePort(portSsl);
            http.setSecureScheme("https");
        }

        ServerConnector connector = new ServerConnector(jetty);
        connector.addConnectionFactory(new HttpConnectionFactory(http));
        connector.setPort(portPublic);

        jetty.addConnector(connector);
    }

    @Override
    protected void addServlet(String path, HttpServlet servlet) {
        if (null == ctxHandler) {
            ctxHandler = new ServletContextHandler();
            ctxHandler.setContextPath("/*");
            handlers.addHandler(ctxHandler);
        }

        ctxHandler.addServlet(new ServletHolder(servlet), path);
    }

    // private HashSessionIdManager getSessionIdManager() {
    // if (null == sessionIdManager) {
    // sessionIdManager = new HashSessionIdManager();
    // jetty.setSessionIdManager(sessionIdManager);
    // }
    //
    // return sessionIdManager;
    // }

    // public void initHandlers() throws Exception {
    // AbstractHandler h = new AbstractHandler() {
    // public void handle(String target, Request baseRequest, HttpServletRequest
    // request, HttpServletResponse response)
    // throws IOException, ServletException {
    //
    // response.setCharacterEncoding(CHARSET_UTF8);
    // response.setContentType(CONTENT_JSON);
    //
    // response.setStatus(HttpServletResponse.SC_OK);
    // baseRequest.setHandled(true);
    //
    // DustPersistentStorageJsonSingle st = new
    // DustPersistentStorageJsonSingle(null);
    //
    // st.writer = response.getWriter();
    //
    // DustPersistence.commit(st);
    //
    //// InputStream is = new FileInputStream("output/temp/TestSingle.json");
    ////
    //// OutputStream outStream = response.getwOutputStream();
    ////
    //// byte[] buffer = new byte[8 * 1024];
    //// int bytesRead;
    //// while ((bytesRead = is.read(buffer)) != -1) {
    //// outStream.write(buffer, 0, bytesRead);
    //// }
    ////
    //// is.close();
    // }
    // };
    //
    // handlers.addHandler(h);
    // }

}
