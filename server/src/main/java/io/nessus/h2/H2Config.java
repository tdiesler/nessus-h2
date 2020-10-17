package io.nessus.h2;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.nessus.common.BasicConfig;

public class H2Config extends BasicConfig {

    public static final Map<String, String> PROPERTY_MAPPING;
    static {
        Map<String, String> mapping = new LinkedHashMap<>();
        mapping.put("jdbcServerUrl", "JDBC_SERVER_URL");
        mapping.put("jdbcUrl", "JDBC_URL");
        mapping.put("jdbcUser", "JDBC_USER");
        mapping.put("jdbcPassword", "JDBC_PASSWORD");
        PROPERTY_MAPPING = Collections.unmodifiableMap(mapping);
    }
    
    public H2Config(URL cfgurl) throws IOException {
        super(cfgurl);
    }

    @Override
    public void prepare(Map<String, String> mapping) {
        mapping.putAll(PROPERTY_MAPPING);
        super.prepare(mapping);
    }
}
