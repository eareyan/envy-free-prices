package statistics;

import java.io.PrintWriter;
import java.util.ArrayList;

import log.SqlDB;

import com.google.common.collect.Iterables;

public class LatexGraphs {

  public static Iterable<PlotData> getSingleMindedPlotData(boolean fixItems) {
    ArrayList<PlotData> tablesUniformOverDemanded = PlotData.getData(fixItems, Util.singleMindedAlgos, "singleminded", "uniform", true, "WHERE m = 10 AND k*m>n");
    ArrayList<PlotData> tablesUniformUnderDemanded = PlotData.getData(fixItems, Util.singleMindedAlgos, "singleminded", "uniform", false, "WHERE m = 5 AND k*m<=n");
    ArrayList<PlotData> tablesElitistOverDemanded = PlotData.getData(fixItems, Util.singleMindedAlgos, "singleminded", "elitist", true, "WHERE m = 10 AND k*m>n");
    ArrayList<PlotData> tablesElitistUnderDemanded = PlotData.getData(fixItems, Util.singleMindedAlgos, "singleminded", "elitist", false, "WHERE m = 5 AND k*m<=n");
    return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded, tablesElitistOverDemanded,
        tablesElitistUnderDemanded));
  }

  public static Iterable<PlotData> getSingletonPlotData(boolean fixItems) {
    ArrayList<PlotData> tablesUniformOverDemanded = PlotData.getData(fixItems, Util.singletonAlgos, "singleton", "uniform", true, "WHERE m = 10 AND p*m>=n");
    ArrayList<PlotData> tablesUniformUnderDemanded = PlotData.getData(fixItems, Util.singletonAlgos, "singleton", "uniform", false, "WHERE m = 10 AND p*m<=n");
    ArrayList<PlotData> tablesElitistOverDemanded = PlotData.getData(fixItems, Util.singletonAlgos, "singleton", "elitist", true, "WHERE m = 10 AND p*m>=n");
    ArrayList<PlotData> tablesElitistUnderDemanded = PlotData.getData(fixItems, Util.singletonAlgos, "singleton", "elitist", false, "WHERE m = 10 AND p*m<=n");
    return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded, tablesElitistOverDemanded,
        tablesElitistUnderDemanded));
  }

  public static Iterable<PlotData> getSizeInterPlotData(boolean fixItems) {
    ArrayList<PlotData> tablesUniformOverDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "sizeinter", "uniform", true, "WHERE m = 10 AND p*m>=n");
    ArrayList<PlotData> tablesUniformUnderDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "sizeinter", "uniform", false, "WHERE m = 10 AND p*m<=n");
    ArrayList<PlotData> tablesElitistOverDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "sizeinter", "elitist", true, "WHERE m = 10 AND p*m>=n");
    ArrayList<PlotData> tablesElitistUnderDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "sizeinter", "elitist", false, "WHERE m = 10 AND p*m<=n");
    return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded, tablesElitistOverDemanded,
        tablesElitistUnderDemanded));
  }

  public static void main(String[] args) throws Exception {
    SqlDB sqlDB = Util.getDB(args);
    String dir = "/home/eareyanv/workspace/envy-free-prices/results/current/latexgraphs/";
    boolean fixItems = false;

    Iterable<PlotData> tables = Iterables.unmodifiableIterable(Iterables.concat(getSingleMindedPlotData(fixItems), getSingletonPlotData(fixItems), getSizeInterPlotData(fixItems)));
    String graphs = PlotData.generateHeader(tables);
    int i = 0;
    for (PlotData t : getSingleMindedPlotData(fixItems)) {
      CollectResults.sqlToCSV(sqlDB, t.sql, dir, t.filelocation);
      graphs += PlotData.generateLatexGraph(Util.singleMindedAlgos, t.title, t.xLabel, t.alias, t.xCol, t.metric);
      i++;
      if (i % 8 == 0) {
        graphs += "\n";
        PlotData.counter = 0;
      }
    }
    i = 0;
    PlotData.counter = 0;
    for (PlotData t : getSingletonPlotData(fixItems)) {
      CollectResults.sqlToCSV(sqlDB, t.sql, dir, t.filelocation);
      graphs += PlotData.generateLatexGraph(Util.singletonAlgos, t.title, t.xLabel, t.alias, t.xCol, t.metric);
      i++;
      if (i % 8 == 0) {
        graphs += "\n";
        PlotData.counter = 0;
      }
    }
    i = 0;
    PlotData.counter = 0;
    for (PlotData t : getSizeInterPlotData(fixItems)) {
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
