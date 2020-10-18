package io.nessus.h2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import io.nessus.common.main.AbstractMain;

public abstract class AbstractH2Main extends AbstractMain<H2Config, H2Options> {

    protected final ConnectionFactory conFactory;
    
    public AbstractH2Main(H2Config config) {
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
		
	    H2Options options = parseArguments(args);
        
        prepare(new LinkedHashMap<>(), options);
        
        try (Connection con = createConnection()) {

            doStart(options);
        }
	}
}
