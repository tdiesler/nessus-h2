package io.nessus.h2;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

import io.nessus.common.AbstractTest;

public abstract class AbstractDBTest extends AbstractTest {

    private ConnectionFactory conFactory;
    
    @Before
    public void before() throws Exception {
    	super.before();
        createConnection();
    }

    @After
    public void after() throws Exception {
        Connection con = getConnection();
        if (con != null) {
            con.close();
        }
        super.after();
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
