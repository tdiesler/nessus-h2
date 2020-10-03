package io.nessus.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.common.AssertState;
import io.nessus.common.BasicConfig;
import io.nessus.common.Config;
import io.nessus.h2.ConnectionFactory;

public final class DBUtils {

    private static Logger LOG = LoggerFactory.getLogger(DBUtils.class);
    
    public static String getH2Version(Connection con) throws SQLException {
        Statement stm = con.createStatement();
        ResultSet res = stm.executeQuery("select h2version()");
        return res.next() ? res.getString(1) : null;
    }
    
    public static boolean tableExists (Connection con, String tableName) throws SQLException {
        ResultSet res = con.getMetaData().getTables(null, null, tableName.toUpperCase(), null);
        return res.next();
    }
    
    public static void createDatabase(Connection con, String resource) throws SQLException, IOException {

    	List<String> statements =  parseStatements(resource);

    	for (String sqlstm : statements) {
            try (Statement stm = con.createStatement()) {
                stm.execute(sqlstm);
            }
        }
    }
    
    public static void primeDatabase(Connection con, String resource) throws SQLException, IOException {

        List<String> statements =  parseStatements(resource);

        // Fetch the set of empty tables used by the statements
        
        Set<String> emptyTables =  new HashSet<>();
        for (String sqlstm : statements) {
            String tname = getTableName(sqlstm);
            if (tname != null && !emptyTables.contains(tname)) {
                try (PreparedStatement stm = con.prepareStatement("SELECT COUNT(*) FROM " + tname)) {
                    ResultSet res = stm.executeQuery();
                    AssertState.isTrue(res.next());
                    int count = res.getInt(1);
                    if (count == 0) {
                        emptyTables.add(tname);
                    }
                }
            }
        }
        
        for (String sqlstm : statements) {
            try (Statement stm = con.createStatement()) {
                String tname = getTableName(sqlstm);
                if (emptyTables.contains(tname)) {
                    stm.execute(sqlstm);
                }
            }
        }
    }

    private static String getTableName(String sqlstm) {
        
        String upr = sqlstm.toUpperCase();
        String[] toks = upr.split(" ");
        AssertState.isTrue(upr.startsWith("INSERT INTO ") && toks.length > 2, "Unsupported statement: " + sqlstm);
        
        String tname = toks[2];
        return tname;
    }

    private static List<String> parseStatements(String resource) throws IOException {
        
        List<String> result = new ArrayList<>();
        
        try (InputStream in = BasicConfig.class.getResourceAsStream("/" + resource)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            StringBuffer buffer = new StringBuffer();
            String line = br.readLine();
            boolean commentOn = false;
            while (line != null) {

                if (line.length() == 0 || line.startsWith("--")) {
                    line = br.readLine();
                    continue;
                }

                if (line.startsWith("/*")) {
                    line = br.readLine();
                    commentOn = true;
                    continue;
                }
                
                if (line.endsWith("*/")) {
                    line = br.readLine();
                    commentOn = false;
                    continue;
                }
                
                if (commentOn) {
                    line = br.readLine();
                    continue;
                }
                
                buffer.append(line);
                if (line.endsWith(";")) {
                    String stm = buffer.toString();
                    result.add(stm);
                    buffer = new StringBuffer();
                }

                line = br.readLine();
            }
        }
        
        return result;
    }
    
    public static void compactDatabase(Config config) throws SQLException {
        
        LOG.info("Compacting the database ...");
        
        ConnectionFactory factory = new ConnectionFactory(config);
        
        try (Connection con = factory.getConnection()) {
            PreparedStatement stm = con.prepareStatement("SHUTDOWN COMPACT");
            stm.execute();
        } finally {
            factory.createConnection();
        }
    }

    public static void defragDatabase(Config config) throws SQLException {
        
        LOG.info("Defragmenting the database ...");
        
        ConnectionFactory factory = new ConnectionFactory(config);
        
        try (Connection con = factory.getConnection()) {
            PreparedStatement stm = con.prepareStatement("SHUTDOWN DEFRAG");
            stm.execute();
        } finally {
            factory.createConnection();
        }
    }
}
