package statistics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import log.SqlDB;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import experiments.RunParameters;

/**
 * Performs different operation to collect results.
 * 
 * @author Enrique Areyan Viqueira
 */
public class CollectResults {

  public static void main(String[] args) throws Exception {
    RunParameters rp = new RunParameters(args);
    SqlDB sqlDB = new SqlDB(rp.dbProvider, rp.dbHost, rp.dbPort, rp.dbName, rp.dbUsername, rp.dbPassword);
    String dir = "/home/eareyanv/workspace/envy-free-prices/results/current/";

    ArrayList<Entry<String, String>> sqlStatements = new ArrayList<Entry<String, String>>();
    sqlStatements.add(new AbstractMap.SimpleEntry<String, String>("SELECT * FROM singleminded_uniform ORDER BY n,m,k", "singleminded_uniform.csv"));
    sqlStatements.add(new AbstractMap.SimpleEntry<String, String>("SELECT * FROM singleminded_elitist ORDER BY n,m,k", "singleminded_elitist.csv"));
    sqlStatements.add(new AbstractMap.SimpleEntry<String, String>("SELECT * FROM singleton_uniform ORDER BY n,m,p", "singleton_uniform.csv"));
    sqlStatements.add(new AbstractMap.SimpleEntry<String, String>("SELECT * FROM singleton_elitist ORDER BY n,m,p", "singleton_elitist.csv"));
    sqlStatements.add(new AbstractMap.SimpleEntry<String, String>("SELECT * FROM sizeinter_uniform ORDER BY n,m,p,k", "sizeinter_uniform.csv"));
    sqlStatements.add(new AbstractMap.SimpleEntry<String, String>("SELECT * FROM sizeinter_elitist ORDER BY n,m,p,k", "sizeinter_elitist.csv"));
    for (Entry<String, String> sqlAndFile : sqlStatements) {
      sqlToCSV(sqlDB, sqlAndFile.getKey(), dir, sqlAndFile.getValue());
    }
  }

  public static void sqlToCSV(SqlDB sqlDB, String sql, String dir, String fileName) throws SQLException, IOException {
    CopyManager copyManager = new CopyManager((BaseConnection) sqlDB.getConn());
    File file = new File(dir + fileName);
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    copyManager.copyOut("COPY (" + sql + ") TO STDOUT WITH (FORMAT CSV, HEADER)", fileOutputStream);
  }
}
