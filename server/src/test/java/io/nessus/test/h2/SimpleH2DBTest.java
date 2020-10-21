package io.nessus.test.h2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import org.junit.Test;

import io.nessus.common.Config;
import io.nessus.h2.AbstractDBTest;

public class SimpleH2DBTest extends AbstractDBTest<Config> {

    @Test
    public void testDefault() throws Exception {

    	try (PreparedStatement stm = getConnection().prepareStatement("SELECT h2version()")) {
    		ResultSet rs = stm.executeQuery();
    		Assert.assertTrue(rs.next());
			Assert.assertEquals("1.4.200", rs.getString(1));
    	}
    }
}
