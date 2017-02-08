package statistics;

import java.io.PrintWriter;
import java.util.ArrayList;

import log.SqlDB;

import com.google.common.collect.Iterables;

public class TACLatexGraphs {

  public static Iterable<PlotData> getTACData(boolean fixItems) {
    ArrayList<PlotData> tablesUniformOverDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "TAC", "", true, "WHERE m > 15");
    ArrayList<PlotData> tablesUniformUnderDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "TAC", "", false, "WHERE m <= 15");
    return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded));
  }

  public static void main(String[] args) throws Exception {
    SqlDB sqlDB = Util.getDB(args);
    String dir = "/home/eareyanv/workspace/envy-free-prices/results/current/latexgraphs/";
    boolean fixItems = true;

    Iterable<PlotData> tables = getTACData(fixItems);
    String graphs = PlotData.generateHeader(tables);
    int i = 0;
    for (PlotData t : getTACData(fixItems)) {
      CollectResults.sqlToCSV(sqlDB, t.sql, dir, t.filelocation);
      graphs += PlotData.generateLatexGraph(Util.sizeInterAlgos, t.title, t.xLabel, t.alias, t.xCol, t.metric);
      i++;
      if (i % 8 == 0) {
        graphs += "\n";
        PlotData.counter = 0;
      }
    }

    graphs += PlotData.generateFooter();

    PrintWriter writer = new PrintWriter("/home/eareyanv/workspace/envy-free-prices/results/current/latexgraphs/graphs.tex", "UTF-8");
    writer.print(graphs);
    writer.close();
    sqlDB.closeConnection();

  }

}
