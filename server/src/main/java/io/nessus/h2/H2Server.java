package io.nessus.h2;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

import org.h2.tools.Server;

import io.nessus.common.AssertState;
import io.nessus.common.Config;

public class H2Server extends AbstractH2Main {

    public static void main(String... args) throws Exception {

    	URL cfgurl = H2Server.class.getResource("/dbconfig.yaml");
    	
    	new H2Server(new H2Config(cfgurl))
    		.start(args);
    }

    H2Server(H2Config config) throws IOException {
        super(config);
    }

    @Override
    protected H2Options createOptions() {
        return new H2Options();
    }

    @Override
    protected void doStart(H2Options options) throws Exception {
        
        Connection con = getConnection();

        logInfo("***************************************************");
        logInfo("Starting H2 {}", DBUtils.getH2Version(con));
        logInfo("Version {}", getVersionString());
        logInfo("***************************************************");
        logInfo();
        
        startServer(config);
    }

    public Server startServer(Config config) throws Exception {
    	String serverUrl = config.getParameter("jdbcServerUrl", String.class);
    	String jdbcUrl = config.getParameter("jdbcUrl", String.class);
    	AssertState.notNull(serverUrl, "Null jdbcServerUrl");
    	AssertState.notNull(jdbcUrl, "Null jdbcUrl");
    	AssertState.isTrue(jdbcUrl.startsWith("jdbc:h2:/"), "Invalid local jdbcUrl: " + jdbcUrl);
    	AssertState.isTrue(serverUrl.startsWith("jdbc:h2:tcp://"), "Protocol not supported: " + serverUrl);
    	URL url = new URL(serverUrl.replace("jdbc:h2:tcp://", "http://"));
    	String host = url.getHost();
    	String port = "" + url.getPort();
    	String dbpath = url.getPath();
    	AssertState.isTrue("localhost".equals(host) || "127.0.0.1".equals(host), "Host not supported: " + serverUrl);
    	AssertState.isTrue(dbpath.length() > 0, "No database path: " + serverUrl);
		String baseDir = jdbcUrl.substring(jdbcUrl.indexOf('/'), jdbcUrl.indexOf(dbpath));
		Server server = Server.createTcpServer("-baseDir", baseDir, "-tcpPort", port, "-tcpAllowOthers").start();
        logInfo(String.format("H2 Server: jdbc:h2:%s%s", server.getURL(), dbpath));
        return server;
    }
    
}
