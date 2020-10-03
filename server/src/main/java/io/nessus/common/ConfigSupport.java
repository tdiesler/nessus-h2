package io.nessus.common;


import java.sql.Connection;
import java.sql.SQLException;

import io.nessus.common.service.LogService;
import io.nessus.common.service.Service;
import io.nessus.h2.ConnectionFactory;

public class ConfigSupport extends LogSupport {

    protected final ConnectionFactory conFactory; 
    protected final Config config;
    
    protected ConfigSupport(Config config) {
        AssertArg.notNull(config, "Null config");
        this.config = config;
        this.conFactory = new ConnectionFactory(config);
        config.addService(new LogService());
    }
    
    @Override
    public Config getConfig() {
        return config;
    }

    public <T extends Service> T getService(Class<T> type) {
        return config.getService(type);
    }

    public Connection createConnection() throws SQLException {
        return conFactory.createConnection();
    }

    public Connection getConnection() throws SQLException {
        return conFactory.getConnection();
    }

    protected void sleepSafe(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            // ignore
        }
    }
}
