package statistics;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;

import log.SqlDB;

import com.google.common.collect.ImmutableList;

/**
 * Transforms results in a database to a LaTex table.
 * 
 * @author Enrique Areyan Viqueira
 */
public class LatexTables {


  public static void main(String[] args) throws Exception {
    SqlDB sqlDB = Util.getDB(args);
    String results = "\\section{Results}\n";
    results += LatexTables.getSingleMindedResults(sqlDB);
    results += LatexTables.getSingletonResults(sqlDB);
    results += LatexTables.getSizeInterResults(sqlDB);
    System.out.println(results);

    PrintWriter writer = new PrintWriter("/home/eareyanv/workspace/envy-free-prices/results/current/latextable/tables.tex", "UTF-8");
    writer.print(results);
    writer.close();
    sqlDB.closeConnection();
  }

  public static String getSingleMindedResults(SqlDB sqlDB) throws SQLException {
    String table = "\\subsection{Single-Minded}\n";
    table += createTable(sqlDB, "Single-Minded, Uniform Reward, Over Demanded", "singleminded_uniform", Util.singleMindedAlgos, "WHERE k*m > n");
    table += createTable(sqlDB, "Single-Minded, Uniform Reward, Under Demanded", "singleminded_uniform", Util.singleMindedAlgos, "WHERE k*m <= n");
    table += createTable(sqlDB, "Single-Minded, Elitist Reward, Over Demanded", "singleminded_elitist", Util.singleMindedAlgos, "WHERE k*m > n");
    table += createTable(sqlDB, "Single-Minded, Elitist Reward, Under Demanded", "singleminded_elitist", Util.singleMindedAlgos, "WHERE k*m <= n");
    table += "\\newpage\n";
    return table;
  }

  public static String getSingletonResults(SqlDB sqlDB) throws SQLException {
    String table = "\\subsection{Singleton}\n";
    table += createTable(sqlDB, "Singleton, Uniform Reward, Over Demanded", "singleton_uniform", Util.singletonAlgos, "WHERE m > n");
    table += createTable(sqlDB, "Singleton, Uniform Reward, Under Demanded", "singleton_uniform", Util.singletonAlgos, "WHERE m <= n");
    table += createTable(sqlDB, "Singleton, Elitist Reward, Over Demanded", "singleton_elitist", Util.singletonAlgos, "WHERE m > n");
    table += createTable(sqlDB, "Singleton, Elitist Reward, Under Demanded", "singleton_elitist", Util.singletonAlgos, "WHERE m <= n");
    table += "\\newpage\n";
    return table;
  }

  public static String getSizeInterResults(SqlDB sqlDB) throws SQLException {
    String table = "\\subsection{Size-Interchangeable}\n";
    table += createTable(sqlDB, "Size-Interchangeable, Uniform Reward, Over Demanded", "sizeinter_uniform", Util.sizeInterAlgos, "WHERE m > n");
    table += createTable(sqlDB, "Size-Interchangeable, Uniform Reward, Under Demanded", "sizeinter_uniform", Util.sizeInterAlgos, "WHERE m <= n");
    table += createTable(sqlDB, "Size-Interchangeable, Elitist Reward, Over Demanded", "sizeinter_elitist", Util.sizeInterAlgos, "WHERE m > n");
    table += createTable(sqlDB, "Size-Interchangeable, Elitist Reward, Under Demanded", "sizeinter_elitist", Util.sizeInterAlgos, "WHERE m <= n");
    /*
     * table += createTable(sqlDB, "Size-Interchangeable, Uniform Reward, Over Demanded", "sizeinter_uniform", Util.sizeInterAlgos, "WHERE k > 1"); table +=
     * createTable(sqlDB, "Size-Interchangeable, Uniform Reward, Under Demanded", "sizeinter_uniform", Util.sizeInterAlgos, "WHERE k <= 1"); table +=
     * createTable(sqlDB, "Size-Interchangeable, Elitist Reward, Over Demanded", "sizeinter_elitist", Util.sizeInterAlgos, "WHERE k > 1"); table +=
     * createTable(sqlDB, "Size-Interchangeable, Elitist Reward, Under Demanded", "sizeinter_elitist", Util.sizeInterAlgos, "WHERE k <= 1");
     */
    return table;
  }

  /**
   * Given a connection, a title, a table name and a list of algorithms, returns a string with a LaTex table containing results for all metrics defined in this
   * method.
   * 
   * @param sqlDB
   * @param title
   * @param tableName
   * @param algos
   * @param where
   * @return a LaTex table representation of the results.
   * @throws SQLException
   */
  public static String createTable(SqlDB sqlDB, String title, String tableName, List<Entry<String, String>> algos, String where) throws SQLException {

    // Construct the SQL statement.
    String sql = "SELECT ";
    for (Entry<String, String> algo : algos) {
      for (Entry<String, String> metric : Util.metrics) {
        String metricName = algo.getValue() + metric.getValue();
        sql += "round(AVG(" + metricName + ")::numeric, 4) AS " + metricName + ", ";
      }
    }
    sql = sql.substring(0, sql.length() - 2) + " FROM " + tableName + " " + where;
    // System.out.println(sql);

    // Execute SQL statement and prepare results.
    PreparedStatement preparedStatement = (PreparedStatement) sqlDB.getConn().prepareStatement(sql);
    ResultSet rs = preparedStatement.executeQuery();
    rs.next();
    ImmutableList<Integer> data = getNumberBiddersAndItems(sqlDB, tableName, where);
    String table = "\\subsubsection*{" + title + ", " + data.get(0) + " bidders, " + data.get(1) + " goods} \n";
    table += "\\begin{tabular}{|c";
    for (int i = 0; i < Util.metrics.size(); i++)
      table += "|c";
    table += "|}\\hline\n";
    table += String.format("%30s", "\t&");
    for (Entry<String, String> metric : Util.metrics)
      table += metric.getValue() + "\t&";
    table = table.substring(0, table.length() - 1);
    table += "\\\\\\hline\n";
    for (Entry<String, String> algo : algos) {
      table += String.format("%30s", algo.getKey()) + "\t&";
      for (Entry<String, String> metric : Util.metrics) {
        String metricName = algo.getValue() + metric.getValue();
        table += rs.getString(metricName) + "\t&";
      }
      table = table.substring(0, table.length() - 1);
      table += "\\\\\\hline \n";
    }
    table += "\\end{tabular}";
    return table;
  }

  /**
   * Given a connection, a table name, and a where clause, returns a list with the most recent number of bidders and the number of goods already computed.
   * 
   * @param sqlDB
   * @param tableName
   * @param where
   * @return an immutable list with two numbers: number of bidders and number of items.
   * @throws SQLException
   */
  public static ImmutableList<Integer> getNumberBiddersAndItems(SqlDB sqlDB, String tableName, String where) throws SQLException {
    String sql1 = "SELECT n,m FROM " + tableName + " " + where + " ORDER BY n DESC, m DESC";
    PreparedStatement preparedStatement1 = (PreparedStatement) sqlDB.getConn().prepareStatement(sql1);
    ResultSet rs1 = preparedStatement1.executeQuery();
    rs1.next();
    return ImmutableList.of(rs1.getInt(2), rs1.getInt(1));
  }
}
