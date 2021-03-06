package io.nessus.test.h2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.nessus.common.BasicConfig;
import io.nessus.common.Config;
import io.nessus.common.testing.AbstractTest;

public class H2ConfigTest extends AbstractTest<BasicConfig> {

    @Test
    public void testDefault() throws Exception {
    	
    	Map<String, String> params = new LinkedHashMap<>();
    	params.put("jdbcURL", "jdbc:h2:tcp://127.0.0.1:8092/h2");
    	params.put("jdbcUser", "h2");
    	params.put("jdbcPassword", "");
    	
    	Config exp = new BasicConfig(params);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    	String json = writer.writeValueAsString(exp);
    	logInfo("{}", json);
    	
    	BasicConfig was = mapper.readValue(json, BasicConfig.class);
    	logInfo("{}", was);
    	
    	Assert.assertEquals(exp, was);
    }  
}
