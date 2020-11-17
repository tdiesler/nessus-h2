package io.nessus.h2;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

import io.nessus.common.BasicConfig;
import io.nessus.common.Config;
import io.nessus.common.service.BasicLogService;
import io.nessus.common.testing.AbstractTest;

public abstract class AbstractDBTest<T extends Config> extends AbstractTest<T> {

    private ConnectionFactory conFactory;
    
    @Before
    public void before() throws Exception {
        createConnection();
    }

    @After
    public void after() throws Exception {
        Connection con = getConnection();
        if (con != null) {
            con.close();
        }
    }

	@Override
    @SuppressWarnings("unchecked")
	protected T createConfig() throws IOException {
    	URL cfgurl = getClass().getResource("/h2config.yaml");
    	BasicConfig config = new BasicConfig(cfgurl);
    	config.putParameter("jdbcUrl", "jdbc:h2:file:/tmp/h2db/nessus");
        config.addService(new BasicLogService());
		return (T) config;
	}

	protected final ConnectionFactory createConnectionFactory() {
        return new ConnectionFactory(getConfig());
    }
    
    protected ConnectionFactory getConnectionFactory() {
        if (conFactory == null) {
            conFactory = createConnectionFactory();
        }
        return conFactory;
    }
    
    protected Connection getConnection() throws SQLException {
        ConnectionFactory factory = getConnectionFactory();
        return factory.getConnection();
    }
    
    protected Connection createConnection() throws SQLException {
        ConnectionFactory factory = getConnectionFactory();
        return factory.createConnection();
    }
}
