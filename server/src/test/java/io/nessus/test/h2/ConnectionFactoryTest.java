package io.nessus.test.h2;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;

import org.junit.Assert;
import org.junit.Test;

import io.nessus.common.BasicConfig;
import io.nessus.common.Config;
import io.nessus.common.service.BasicLogService;
import io.nessus.common.testing.AbstractTest;
import io.nessus.h2.ConnectionFactory;

public class ConnectionFactoryTest extends AbstractTest<Config> {

    @Test
    public void testDefault() throws Exception {

    	ConnectionFactory factory = new ConnectionFactory(createConfig());
    	try (Connection con = factory.createConnection()) {
    		Assert.assertSame(con, factory.getConnection());
    		PreparedStatement stm = con.prepareStatement("SELECT h2version()");
    		ResultSet rs = stm.executeQuery();
    		Assert.assertTrue(rs.next());
			Assert.assertEquals("1.4.200", rs.getString(1));
    	}
    }

    @Test
    public void testWithTimeout() throws Exception {

    	ConnectionFactory factory = new ConnectionFactory(createConfig());
    	try (Connection con = factory.createConnection(Duration.ofSeconds(10))) {
    		Assert.assertSame(con, factory.getConnection());
    		PreparedStatement stm = con.prepareStatement("SELECT h2version()");
    		ResultSet rs = stm.executeQuery();
    		Assert.assertTrue(rs.next());
			Assert.assertEquals("1.4.200", rs.getString(1));
    	}
    }

	protected Config createConfig() throws IOException {
    	URL cfgurl = getClass().getResource("/h2config.yaml");
    	BasicConfig config = new BasicConfig(cfgurl);
    	config.putParameter("jdbcUrl", "jdbc:h2:file:/tmp/h2db/nessus");
        config.addService(new BasicLogService());
		return config;
	}
}
