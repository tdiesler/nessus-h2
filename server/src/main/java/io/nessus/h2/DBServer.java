package io.nessus.h2;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.h2.tools.Server;

import io.nessus.common.AssertState;
import io.nessus.common.Config;
import io.nessus.common.main.AbstractBot;
import io.nessus.common.utils.DBUtils;

public class DBServer extends AbstractBot<DBServerOptions> {

    public static void main(String... args) throws Exception {

    	URL cfgurl = DBServer.class.getResource("/dbconfig.json");
    	
    	new DBServer(cfgurl)
    		.start(args);
    }

    DBServer(URL cfgurl) throws IOException {
        super(cfgurl);
    }

    @Override
    protected DBServerOptions createOptions() {
        return new DBServerOptions();
    }

    @Override
    protected void doStart(DBServerOptions options) throws Exception {
        
        Connection con = getConnection();

        logInfo("***************************************************");
        logInfo("Starting H2 {}", DBUtils.getH2Version(con));
        logInfo("Version {}", getVersionString());
        logInfo("***************************************************");
        logInfo();
        
        startServer(config);
    }

	static void initConfig(Config config) {
		
		Map<String, String> mapping = new HashMap<>();
		mapping.put("jdbcPort", "JDBC_PORT");
		mapping.put("jdbcUrl", "JDBC_URL");
		mapping.put("jdbcUser", "JDBC_USER");
		mapping.put("jdbcPass", "JDBC_PASS");
		
		// Override with env vars
		
		for (Entry<String, String> en : mapping.entrySet()) {
			String value = System.getenv(en.getValue());
			if (value != null) {
				config.putParameter(en.getKey(), value);
			}
		}
		
		// Override with system properties
		
		for (Entry<String, String> en : mapping.entrySet()) {
			String value = System.getProperty(en.getKey());
			if (value != null) {
				config.putParameter(en.getKey(), value);
			}
		}
	}
	
    public Server startServer(Config config) throws Exception {
    	String serverURL = config.getParameter("jdbcServerURL", String.class);
    	AssertState.notNull(serverURL, "Null jdbcServerURL");
    	AssertState.isTrue(serverURL.startsWith("jdbc:h2:tcp://"), "Protocol not supported: " + serverURL);
    	URL url = new URL(serverURL.replace("jdbc:h2:tcp://", "http://"));
    	String host = url.getHost();
    	String port = "" + url.getPort();
    	String baseDir = url.getPath();
    	AssertState.isTrue("localhost".equals(host) || "127.0.0.1".equals(host), "Host not supported: " + host);
        Server server = Server.createTcpServer("-baseDir", baseDir, "-tcpPort", port, "-tcpAllowOthers").start();
        logInfo("H2 Server: {}{}", server.getURL(), baseDir);
        return server;
    }
    
}
