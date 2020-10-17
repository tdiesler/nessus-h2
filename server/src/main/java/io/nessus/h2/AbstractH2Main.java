package io.nessus.h2;

import java.sql.Connection;
import java.sql.SQLException;

import io.nessus.common.Config;
import io.nessus.common.main.AbstractMain;
import io.nessus.common.main.AbstractOptions;

public abstract class AbstractH2Main<C extends Config, T extends AbstractOptions> extends AbstractMain<C, T> {

    protected final ConnectionFactory conFactory;
    
    public AbstractH2Main(C config) {
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
