package io.nessus.h2;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import io.nessus.common.BasicConfig;
import io.nessus.common.Config;
import io.nessus.common.main.AbstractMain;
import io.nessus.common.main.AbstractOptions;

public abstract class AbstractDBMain<C extends Config, T extends AbstractOptions> extends AbstractMain<C, T> {

    protected final ConnectionFactory conFactory;
    
    @SuppressWarnings("unchecked")
	public AbstractDBMain(URL cfgurl) throws IOException {
        this((C) new BasicConfig(cfgurl));
    }

    public AbstractDBMain(C config) {
        super(config);
        this.conFactory = new ConnectionFactory(config);
    }

    public Connection createConnection() throws SQLException {
        return conFactory.createConnection();
    }

    public Connection getConnection() throws SQLException {
        return conFactory.getConnection();
    }

	@Override
	public void startInternal(String... args) throws Exception {
		
        try (Connection con = createConnection()) {

        	super.startInternal(args);
        }
	}
}
