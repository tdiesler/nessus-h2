package io.nessus.common;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.LogManager;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.nessus.common.BasicConfig.ConfigSerializer;
import io.nessus.common.service.Service;

@JsonSerialize(using = ConfigSerializer.class)
public class BasicConfig implements Config {

    private final Map<String, Service> services = new LinkedHashMap<>();
    private final Parameters params;
    
    static {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }
    
    public BasicConfig(URL cfgurl) throws IOException {
    	AssertArg.notNull(cfgurl, "Null cfgurl");
    	ObjectMapper mapper = new ObjectMapper();
		Config cfg = mapper.readValue(cfgurl, BasicConfig.class);
		this.params = cfg.getParameters();
    }

    @JsonCreator
    public BasicConfig(Map<String, ? extends Object> params) {
        this.params = new Parameters(params);
    }

    public BasicConfig(Parameters params) {
        this.params = new Parameters(params);
    }

    @Override
    public Parameters getParameters() {
        return new Parameters(params);
    }

    @Override
    public <T> T getParameter(String name, Class<T> type) {
        return params.get(name, type);
    }

    @Override
    public <T> T getParameter(String name, T defaultValue) {
        return params.get(name, defaultValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T putParameter(String name, T value) {
        T resval;
        if (value != null) resval = params.put(name, value);
        else resval = (T) params.remove(name);
        return resval;
    }

    @Override
    public <T extends Service> void addService(T service) {
    	services.put(service.getType(), service);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Service> T getService(Class<T> type) {
        T result = (T) services.get(type.getName());
        if (result == null) {
            for (Service srv : services.values()) {
                if (type.isAssignableFrom(srv.getClass())) {
                    result = (T) srv;
                    break;
                }
            }
        }
        return result;
    }

    @Override
	public void initServices() {
    	services.values().forEach(srv -> srv.init(this));
	}

    
    @Override
	public int hashCode() {
		return params.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof BasicConfig)) return false;
		BasicConfig other = (BasicConfig) obj;
		return params.equals(other.params);
	}

	@Override
	public String toString() {
		return params.toString();
	}

	public static class ConfigSerializer extends JsonSerializer<Config> {

        @Override
        public void serialize(Config value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        	Map<String, Object> map = value.getParameters().toMap();
            jgen.writeObject(map);
        }
    }
}
