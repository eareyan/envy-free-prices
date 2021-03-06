package log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Saves results of experiments to a SQL database.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SqlDB {

  /**
   * Connection object.
   */
  private Connection conn;

  /**
   * Constructor.
   * 
   * @param provider
   * @param host
   * @param port
   * @param dbName
   * @param user
   * @param pass
   * @throws SQLException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public SqlDB(String provider, String host, int port, String dbName, String user, String pass) throws SQLException, InstantiationException,
      IllegalAccessException, ClassNotFoundException {
    // Connect to the database. First determine the driver.
    if (provider.equals("postgresql")) {
      Class.forName("org.postgresql.Driver");
    } else if (provider.equals("mysql")) {
      Class.forName("com.mysql.jdbc.Driver");
    }
    this.setConn(DriverManager.getConnection("jdbc:" + provider + "://" + host + ":" + port + "/" + dbName + "?user=" + user + "&password=" + pass));
    if (this.getConn() == null) {
      System.out.println("NOT Connected to database...");
    }
  }

  /**
   * Close a connection.
   * 
   * @param conn
   * @throws SQLException
   */
  public void closeConnection() throws SQLException {
    this.getConn().close();
  }

  /**
   * Check if a row of the database exists.
   * 
   * @param tablename
   * @param n
   * @param m
   * @return true if the row (n,m) exists in tablename
   * @throws SQLException
   */
  public boolean checkIfRowExists(String tablename, int n, int m, int k) throws SQLException {
    String sql = "SELECT * FROM " + tablename + " WHERE n = ? AND m = ? AND k = ?";
    PreparedStatement preparedStatement = (PreparedStatement) this.getConn().prepareStatement(sql);
    preparedStatement.setInt(1, n);
    preparedStatement.setInt(2, m);
    preparedStatement.setInt(3, k);
    return ((ResultSet) preparedStatement.executeQuery()).next();
  }
  
  /**
   * Check if a row of the database exists.
   * 
   * @param tablename
   * @param n
   * @param m
   * @return true if the row (n,m) exists in tablename
   * @throws SQLException
   */
  public boolean checkIfRowExists(String tablename, int n, int m, double p) throws SQLException {
    String sql = "SELECT * FROM " + tablename + " WHERE n = ? AND m = ? AND p = ?";
    PreparedStatement preparedStatement = (PreparedStatement) this.getConn().prepareStatement(sql);
    preparedStatement.setInt(1, n);
    preparedStatement.setInt(2, m);
    preparedStatement.setDouble(3, p);
    return ((ResultSet) preparedStatement.executeQuery()).next();
  }
  
  /**
   * Check if a row exists in the database.
   * 
   * @param tablename
   * @param m
   * @return true if the row exists
   * @throws SQLException
   */
  public boolean checkIfRowExists(String tablename, int m) throws SQLException {
    String sql = "SELECT * FROM " + tablename + " WHERE m = ?";
    PreparedStatement preparedStatement = (PreparedStatement) this.getConn().prepareStatement(sql);
    preparedStatement.setInt(1, m);
    return ((ResultSet) preparedStatement.executeQuery()).next();
  }
  
  /**
   * Check if a row of the complete search for singleminded exist.
   * 
   * @param i
   * @param n
   * @param m
   * @param k
   * @return
   * @throws SQLException
   */
  public boolean checkIfCompleteSearchRowExists(int i, int n, int m, int k, String distribution) throws SQLException {
    String sql = "SELECT * FROM complete_search_singleminded WHERE i = ? AND n = ? AND m = ? AND k = ? AND distribution = ?";
    PreparedStatement preparedStatement = (PreparedStatement) this.getConn().prepareStatement(sql);
    preparedStatement.setInt(1, i);
    preparedStatement.setInt(2, n);
    preparedStatement.setInt(3, m);
    preparedStatement.setInt(4, k);
    preparedStatement.setString(5, distribution);
    return ((ResultSet) preparedStatement.executeQuery()).next();
  }
  
  /**
   * Check if a row of the WE existence for singleminded exists.
   * @param n
   * @param m
   * @param k
   * @param edges
   * @param rewards
   * @return
   * @throws SQLException
   */
  public boolean checkIfWEExistenceRowExists(int n, int m, int k, String edges, String rewards) throws SQLException {
    String sql = "SELECT n FROM we_existence_singleminded WHERE n = ? AND m = ? AND k = ? AND edges = ? AND rewards = ?";
    PreparedStatement preparedStatement = (PreparedStatement) this.getConn().prepareStatement(sql);
    preparedStatement.setInt(1, n);
    preparedStatement.setInt(2, m);
    preparedStatement.setInt(3, k);
    preparedStatement.setString(4, edges);
    preparedStatement.setString(5, rewards);
    return ((ResultSet) preparedStatement.executeQuery()).next();
  }
  
  /**
   * Check if a row of the database exists.
   * 
   * @param tablename
   * @param n
   * @param m
   * @return true if the row (n,m) exists in tablename
   * @throws SQLException
   */
  public boolean checkIfRowExists(String tablename, int n, int m, int k, double p) throws SQLException {
    String sql = "SELECT * FROM " + tablename + " WHERE n = ? AND m = ? AND k = ? AND p = ?";
    PreparedStatement preparedStatement = (PreparedStatement) this.getConn().prepareStatement(sql);
    preparedStatement.setInt(1, n);
    preparedStatement.setInt(2, m);
    preparedStatement.setInt(3, k);
    preparedStatement.setDouble(4, p);
    return ((ResultSet) preparedStatement.executeQuery()).next();
  }
  
  /**
   * Save complete search row.
   * 
   * @param i
   * @param n
   * @param m
   * @param k
   * @param distribution
   * @param time
   * @param num_branches
   * @param num_infeasible
   * @param num_revbound
   * @throws SQLException
   */
  public void saveCompleteSearchRow(int i, int n, int m, int k, String distribution, double time, int num_explored_states, int num_infeasible, int num_revbound) throws SQLException {
    String sql = "INSERT INTO complete_search_singleminded (i, n, m, k, distribution, time, num_explored_states, num_infeasible, num_revbound) " + 
                  "VALUES (?, ? ,?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement preparedStatement = (PreparedStatement) this.getConn().prepareStatement(sql);
    preparedStatement.setInt(1, i);
    preparedStatement.setInt(2, n);
    preparedStatement.setInt(3, m);
    preparedStatement.setInt(4, k);
    preparedStatement.setString(5, distribution);
    preparedStatement.setDouble(6, time);
    preparedStatement.setInt(7, num_explored_states);
    preparedStatement.setInt(8, num_infeasible);
    preparedStatement.setInt(9, num_revbound);
    preparedStatement.execute();
  }
  
  /**
   * Save WE existence row, singleminded.
   * 
   * @param n
   * @param m
   * @param k
   * @param edges
   * @param rewards
   * @param time
   * @param we
   * @throws SQLException
   */
  public void saveWEExistenceRow(int n, int m, int k, String edges, String rewards, double time, int we) throws SQLException {
    String sql = "INSERT INTO we_existence_singleminded (n, m, k, edges, rewards, time, we) " + 
                  "VALUES (?, ? ,?, ?, ?, ?, ?)";
    PreparedStatement preparedStatement = (PreparedStatement) this.getConn().prepareStatement(sql);
    preparedStatement.setInt(1, n);
    preparedStatement.setInt(2, m);
    preparedStatement.setInt(3, k);
    preparedStatement.setString(4, edges);
    preparedStatement.setString(5, rewards);
    preparedStatement.setDouble(6, time);
    preparedStatement.setInt(7, we);
    preparedStatement.execute();
  }

  /**
   * Save single minded experiment. This function is just a handy wrapper for createAndExecuteSQLStatement
   * 
   * @param table_name
   * @param n
   * @param m
   * @param stats
   * @throws SQLException
   */
  public void saveSingleMinded(String table_name, int n, int m, int k, HashMap<String, DescriptiveStatistics> stats) throws SQLException {
    // Add the number of goods.
    DescriptiveStatistics numberOfGoods = new DescriptiveStatistics();
    numberOfGoods.addValue(n);
    stats.put("n", numberOfGoods);
    // Add the number of bidders.
    DescriptiveStatistics numberOfBidders = new DescriptiveStatistics();
    numberOfBidders.addValue(m);
    stats.put("m", numberOfBidders);
    // Add the bound on the size of demand set
    DescriptiveStatistics boundOnDemand = new DescriptiveStatistics();
    boundOnDemand.addValue(k);
    stats.put("k", boundOnDemand);
    // Create and execute SQL statement.
    this.createAndExecuteSQLStatement(table_name, stats);
  }
  
  /**
   * Save size interchangeable experiment. This function is just a handy wrapper for createAndExecuteSQLStatement
   * 
   * @param table_name
   * @param n
   * @param m
   * @param stats
   * @throws SQLException
   */
  public void saveSizeInter(String table_name, int n, int m, int k, double p, HashMap<String, DescriptiveStatistics> stats) throws SQLException {
    // Add the number of goods.
    DescriptiveStatistics numberOfGoods = new DescriptiveStatistics();
    numberOfGoods.addValue(n);
    stats.put("n", numberOfGoods);
    // Add the number of bidders.
    DescriptiveStatistics numberOfBidders = new DescriptiveStatistics();
    numberOfBidders.addValue(m);
    stats.put("m", numberOfBidders);
    // Add the bound on the size of demand set
    DescriptiveStatistics boundOnDemand = new DescriptiveStatistics();
    boundOnDemand.addValue(k);
    stats.put("k", boundOnDemand);
    // Add the type of reward
    DescriptiveStatistics probabilityConnections = new DescriptiveStatistics();
    probabilityConnections.addValue(p);
    stats.put("p", probabilityConnections);
    // Create and execute SQL statement.
    this.createAndExecuteSQLStatement(table_name, stats);
  }
  
  /**
   * Save TAC experiment.
   * 
   * @param table_name
   * @param m
   * @param stats
   * @throws SQLException
   */
  public void saveTAC(String table_name, int m, HashMap<String, DescriptiveStatistics> stats) throws SQLException {
    // Add the number of bidders.
    DescriptiveStatistics numberOfBidders = new DescriptiveStatistics();
    numberOfBidders.addValue(m);
    stats.put("m", numberOfBidders);
    // Create and execute SQL statement.
    this.createAndExecuteSQLStatement(table_name, stats);
  }
  
  /**
   * Save single minded experiment. This function is just a handy wrapper for createAndExecuteSQLStatement
   * 
   * @param table_name
   * @param n
   * @param m
   * @param stats
   * @throws SQLException
   */
  public void saveSingleton(String table_name, int n, int m, double p, HashMap<String, DescriptiveStatistics> stats) throws SQLException {
    // Add the number of goods.
    DescriptiveStatistics numberOfGoods = new DescriptiveStatistics();
    numberOfGoods.addValue(n);
    stats.put("n", numberOfGoods);
    // Add the number of bidders.
    DescriptiveStatistics numberOfBidders = new DescriptiveStatistics();
    numberOfBidders.addValue(m);
    stats.put("m", numberOfBidders);
    // Add the type of reward
    DescriptiveStatistics probabilityConnections = new DescriptiveStatistics();
    probabilityConnections.addValue(p);
    stats.put("p", probabilityConnections);
    // Create and execute SQL statement.
    this.createAndExecuteSQLStatement(table_name, stats);
  }

  /**
   * Creates and executes an insert an SQL statement into the table_name table for the values given in the stats map. The name of the columns are the keys of
   * the map and the values are the corresponding values in the map.
   * 
   * @param table_name - where to insert the data
   * @param stats - a Map <String, DescriptiveStatistics> with the data.
   * @throws SQLException
   */
  public void createAndExecuteSQLStatement(String table_name, HashMap<String, DescriptiveStatistics> stats) throws SQLException {
    String sqlPre = "INSERT INTO " + table_name + " (";
    String sqlPost = "(";
    ArrayList<DescriptiveStatistics> orderedStats = new ArrayList<DescriptiveStatistics>();
    // First create the SQL string and keep track of the order of the statistics in an ArrayList
    for (Entry<String, DescriptiveStatistics> entry : stats.entrySet()) {
      sqlPre += entry.getKey() + ",";
      sqlPost += "?,";
      orderedStats.add(entry.getValue());
    }
    String sql = sqlPre.substring(0, sqlPre.length() - 1) + ") VALUES " + sqlPost.substring(0, sqlPost.length() - 1) + ")";
    PreparedStatement preparedStatement = (PreparedStatement) this.getConn().prepareStatement(sql);
    int i = 1;
    // Set the values in the same order as created by the sql string.
    for (DescriptiveStatistics ds : orderedStats) {
      preparedStatement.setDouble(i++, ds.getMean());
    }
    preparedStatement.execute();
  }

  public Connection getConn() {
    return conn;
  }

  public void setConn(Connection conn) {
    this.conn = conn;
  }

}