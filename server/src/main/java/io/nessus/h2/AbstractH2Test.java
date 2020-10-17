package io.nessus.h2;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

import io.nessus.common.BasicConfig;
import io.nessus.common.testing.AbstractTest;

public abstract class AbstractH2Test extends AbstractTest<BasicConfig> {

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
