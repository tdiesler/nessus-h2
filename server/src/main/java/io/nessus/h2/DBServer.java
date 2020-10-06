package io.nessus.h2;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import org.h2.tools.Server;
import org.slf4j.Logger;

import io.nessus.common.AssertState;
import io.nessus.common.BasicConfig;
import io.nessus.common.Config;

public class DBServer extends AbstractDBMain<BasicConfig, DBServerOptions> {

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

	static void initConfig(Logger log, Config config) {
		
		Map<String, String> mapping = new LinkedHashMap<>();
		mapping.put("jdbcServerUrl", "JDBC_SERVER_URL");
		mapping.put("jdbcUrl", "JDBC_URL");
		mapping.put("jdbcUser", "JDBC_USER");
		mapping.put("jdbcPass", "JDBC_PASS");
		
		BiFunction<String, String, String> logval = (k, v) -> {
			if (v == null) return null;
			boolean ispw = k.toLowerCase().contains("pass");
			v = ispw && v.length() > 0  ? "*****" : v;
			return v;
		};
		
		// Override with env vars
		
		for (Entry<String, String> en : mapping.entrySet()) {
			String key = en.getKey();
			String value = System.getenv(en.getValue());
			if (value != null) {
				log.debug("Env {}: {}", en.getValue(), logval.apply(key, value));
				config.putParameter(key, value);
			}
		}
		
		// Override with system properties
		
		for (Entry<String, String> en : mapping.entrySet()) {
			String key = en.getKey();
			String value = System.getProperty(key);
			if (value != null) {
				log.debug("Prop {}: {}", key, logval.apply(key, value));
				config.putParameter(en.getKey(), value);
			}
		}
	}
	
    public Server startServer(Config config) throws Exception {
    	String serverUrl = config.getParameter("jdbcServerUrl", String.class);
    	String jdbcUrl = config.getParameter("jdbcUrl", String.class);
    	AssertState.notNull(serverUrl, "Null jdbcServerUrl");
    	AssertState.notNull(jdbcUrl, "Null jdbcUrl");
    	AssertState.isTrue(serverUrl.startsWith("jdbc:h2:tcp://"), "Protocol not supported: " + serverUrl);
    	URL url = new URL(serverUrl.replace("jdbc:h2:tcp://", "http://"));
    	String host = url.getHost();
    	String port = "" + url.getPort();
    	String dbpath = url.getPath();
		String baseDir = jdbcUrl.substring(jdbcUrl.indexOf('/'), jdbcUrl.indexOf(dbpath));
    	AssertState.isTrue("localhost".equals(host) || "127.0.0.1".equals(host), "Host not supported: " + host);
        Server server = Server.createTcpServer("-baseDir", baseDir, "-tcpPort", port, "-tcpAllowOthers").start();
        logInfo(String.format("H2 Server: jdbc:h2:%s%s", server.getURL(), dbpath));
        return server;
    }
    
}
