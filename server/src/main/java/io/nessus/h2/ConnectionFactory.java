package io.nessus.h2;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.nessus.common.AssertArg;
import io.nessus.common.CheckedExceptionWrapper;
import io.nessus.common.Config;
import io.nessus.common.ConfigSupport;

public final class ConnectionFactory extends ConfigSupport<Config> { 

    private final static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();
    
    public ConnectionFactory(Config config) {
        super(config);
    }
    
    public boolean hasConnection() {
        return threadLocal.get() != null;
    }

    public Connection getConnection() {
        return threadLocal.get();
    }

    public Connection createConnection() throws SQLException {
        
        Connection con = threadLocal.get();
        AssertArg.isTrue(con == null, "Connection already created by this thread");

        con = createConnectionInternal();
        AssertArg.notNull(con, "Cannot obtain connection");
        
        ConnectionWrapper wrapper = new ConnectionWrapper(con);
        threadLocal.set(wrapper);
        
        return wrapper;
    }

	public Connection createConnection(Duration timeout) throws TimeoutException {
		AssertArg.notNull(timeout, "Null timeout");
		
        Connection con = threadLocal.get();
        AssertArg.isTrue(con == null, "Connection already created by this thread");
        
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Connection> future = executor.submit(() -> {
			Connection aux = null;
			while (aux == null) {
				try {
					aux = createConnectionInternal();
				} catch (SQLException ex) {
					logError("{}", ex.toString());
					sleepSafe(500);
				}
			}
			return aux;
		});
		
		try {
			
			con = future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
			
		} catch (TimeoutException ex) {
			throw ex;
		} catch (InterruptedException | ExecutionException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		
        AssertArg.notNull(con, "Cannot obtain connection");
        
        ConnectionWrapper wrapper = new ConnectionWrapper(con);
        threadLocal.set(wrapper);
        
        return wrapper;
	}
	
    private Connection createConnectionInternal() throws SQLException {
        
		String jdbcUrl = config.getParameter("jdbcUrl", String.class);
        String jdbcUser = config.getParameter("jdbcUser", String.class);
        String jdbcPass = config.getParameter("jdbcPassword", "");
        
        Connection con = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass);
        
        AssertArg.notNull(con, "Cannot obtain connection");
        
        ConnectionWrapper wrapper = new ConnectionWrapper(con);
        return wrapper;
    }
    
    private void sleepSafe(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            // ignore
        }
    }
    
    public class ConnectionWrapper implements Connection {
        
        final Connection con;

        private ConnectionWrapper(Connection con) {
            this.con = con;
        }
        
        @Override
        public void close() throws SQLException {
            threadLocal.remove();
            con.close();
        }
        
        // Delegate methods below 
        
        public <I> I unwrap(Class<I> iface) throws SQLException {
            return con.unwrap(iface);
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return con.isWrapperFor(iface);
        }

        public Statement createStatement() throws SQLException {
            return con.createStatement();
        }

        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return con.prepareStatement(sql);
        }

        public CallableStatement prepareCall(String sql) throws SQLException {
            return con.prepareCall(sql);
        }

        public String nativeSQL(String sql) throws SQLException {
            return con.nativeSQL(sql);
        }

        public void setAutoCommit(boolean autoCommit) throws SQLException {
            con.setAutoCommit(autoCommit);
        }

        public boolean getAutoCommit() throws SQLException {
            return con.getAutoCommit();
        }

        public void commit() throws SQLException {
            con.commit();
        }

        public void rollback() throws SQLException {
            con.rollback();
        }

        public boolean isClosed() throws SQLException {
            return con.isClosed();
        }

        public DatabaseMetaData getMetaData() throws SQLException {
            return con.getMetaData();
        }

        public void setReadOnly(boolean readOnly) throws SQLException {
            con.setReadOnly(readOnly);
        }

        public boolean isReadOnly() throws SQLException {
            return con.isReadOnly();
        }

        public void setCatalog(String catalog) throws SQLException {
            con.setCatalog(catalog);
        }

        public String getCatalog() throws SQLException {
            return con.getCatalog();
        }

        public void setTransactionIsolation(int level) throws SQLException {
            con.setTransactionIsolation(level);
        }

        public int getTransactionIsolation() throws SQLException {
            return con.getTransactionIsolation();
        }

        public SQLWarning getWarnings() throws SQLException {
            return con.getWarnings();
        }

        public void clearWarnings() throws SQLException {
            con.clearWarnings();
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return con.createStatement(resultSetType, resultSetConcurrency);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return con.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return con.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return con.getTypeMap();
        }

        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            con.setTypeMap(map);
        }

        public void setHoldability(int holdability) throws SQLException {
            con.setHoldability(holdability);
        }

        public int getHoldability() throws SQLException {
            return con.getHoldability();
        }

        public Savepoint setSavepoint() throws SQLException {
            return con.setSavepoint();
        }

        public Savepoint setSavepoint(String name) throws SQLException {
            return con.setSavepoint(name);
        }

        public void rollback(Savepoint savepoint) throws SQLException {
            con.rollback(savepoint);
        }

        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            con.releaseSavepoint(savepoint);
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return con.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return con.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return con.prepareStatement(sql, autoGeneratedKeys);
        }

        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return con.prepareStatement(sql, columnIndexes);
        }

        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return con.prepareStatement(sql, columnNames);
        }

        public Clob createClob() throws SQLException {
            return con.createClob();
        }

        public Blob createBlob() throws SQLException {
            return con.createBlob();
        }

        public NClob createNClob() throws SQLException {
            return con.createNClob();
        }

        public SQLXML createSQLXML() throws SQLException {
            return con.createSQLXML();
        }

        public boolean isValid(int timeout) throws SQLException {
            return con.isValid(timeout);
        }

        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            con.setClientInfo(name, value);
        }

        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            con.setClientInfo(properties);
        }

        public String getClientInfo(String name) throws SQLException {
            return con.getClientInfo(name);
        }

        public Properties getClientInfo() throws SQLException {
            return con.getClientInfo();
        }

        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return con.createArrayOf(typeName, elements);
        }

        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return con.createStruct(typeName, attributes);
        }

        public void setSchema(String schema) throws SQLException {
            con.setSchema(schema);
        }

        public String getSchema() throws SQLException {
            return con.getSchema();
        }

        public void abort(Executor executor) throws SQLException {
            con.abort(executor);
        }

        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            con.setNetworkTimeout(executor, milliseconds);
        }

        public int getNetworkTimeout() throws SQLException {
            return con.getNetworkTimeout();
        }
    }
}
