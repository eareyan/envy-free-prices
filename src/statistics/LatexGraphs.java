package statistics;

import java.io.PrintWriter;
import java.util.ArrayList;

import log.SqlDB;

public class LatexGraphs {

  public static Iterable<PlotData> getSingleMindedPlotData(boolean fixItems) {
    ArrayList<PlotData> tablesUniformOverDemanded = PlotData.getData(fixItems, Util.singleMindedAlgos, "singleminded", "uniform", true, "WHERE n = 7 AND k*m > n");
    //ArrayList<PlotData> tablesUniformUnderDemanded = PlotData.getData(fixItems, Util.singleMindedAlgos, "singleminded", "uniform", false, "WHERE n = 8 AND k*m <= n");
    //ArrayList<PlotData> tablesElitistOverDemanded = PlotData.getData(fixItems, Util.singleMindedAlgos, "singleminded", "elitist", true, "WHERE n = 7 AND k*m > n");
    //ArrayList<PlotData> tablesElitistUnderDemanded = PlotData.getData(fixItems, Util.singleMindedAlgos, "singleminded", "elitist", false, "WHERE n = 8 AND k*m <= n");
    //return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded, tablesElitistOverDemanded,
    //    tablesElitistUnderDemanded));
    return tablesUniformOverDemanded;
  }

  /*public static Iterable<PlotData> getSingletonPlotData(boolean fixItems) {
    ArrayList<PlotData> tablesUniformOverDemanded = PlotData.getData(fixItems, Util.singletonAlgos, "singleton", "uniform", true, "WHERE n = 10 AND p*m >= n");
    ArrayList<PlotData> tablesUniformUnderDemanded = PlotData.getData(fixItems, Util.singletonAlgos, "singleton", "uniform", false, "WHERE n = 10 AND p*m <= n");
    ArrayList<PlotData> tablesElitistOverDemanded = PlotData.getData(fixItems, Util.singletonAlgos, "singleton", "elitist", true, "WHERE n = 10 AND p*m >= n");
    ArrayList<PlotData> tablesElitistUnderDemanded = PlotData.getData(fixItems, Util.singletonAlgos, "singleton", "elitist", false, "WHERE n = 10 AND p*m <= n");
    return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded, tablesElitistOverDemanded,
        tablesElitistUnderDemanded));
  }*/

  public static Iterable<PlotData> getSizeInterPlotData(boolean fixItems) {
    //ArrayList<PlotData> tablesUniformOverDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "sizeinter", "uniform", true, "WHERE n = 5 AND p*m >= n AND k > 0");
    ArrayList<PlotData> tablesUniformUnderDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "sizeintersmall", "uniform", false, "WHERE n = 10 AND m <= 18 AND p*m < n AND k < 0");
    //ArrayList<PlotData> tablesElitistOverDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "sizeinter", "elitist", true, "WHERE n = 5 AND p*m >= n AND k > 0");
    //ArrayList<PlotData> tablesElitistUnderDemanded = PlotData.getData(fixItems, Util.sizeInterAlgos, "sizeinter", "elitist", false, "WHERE n = 5 AND p*m <= n AND k < 0");
    //return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded, tablesElitistOverDemanded,
    //    tablesElitistUnderDemanded));
    return tablesUniformUnderDemanded;
  }

  public static void main(String[] args) throws Exception {
    SqlDB sqlDB = Util.getDB(args);
    String dir = "/home/eareyanv/workspace/envy-free-prices/results/current/latexgraphs/";
    boolean fixItems = true;

    //Iterable<PlotData> tables = Iterables.unmodifiableIterable(Iterables.concat(getSingleMindedPlotData(fixItems), getSingletonPlotData(fixItems), getSizeInterPlotData(fixItems)));
    //Iterable<PlotData> tables = Iterables.unmodifiableIterable(Iterables.concat(getSingleMindedPlotData(fixItems), getSizeInterPlotData(fixItems)));
    Iterable<PlotData> tables =  getSizeInterPlotData(fixItems);
    String graphs = PlotData.generateHeader(tables);
    int i = 0;
    /*for (PlotData t : getSingleMindedPlotData(fixItems)) {
      CollectResults.sqlToCSV(sqlDB, t.sql, dir, t.filelocation);
      graphs += PlotData.generateLatexGraph(Util.singleMindedAlgos, t.title, t.xLabel, t.alias, t.xCol, t.metric);
      i++;
      if (i % 8 == 0) {
        graphs += "\n";
        PlotData.counter = 0;
      }
    }
    /*i = 0;
    PlotData.counter = 0;
    for (PlotData t : getSingletonPlotData(fixItems)) {
      CollectResults.sqlToCSV(sqlDB, t.sql, dir, t.filelocation);
      graphs += PlotData.generateLatexGraph(Util.singletonAlgos, t.title, t.xLabel, t.alias, t.xCol, t.metric);
      i++;
      if (i % 8 == 0) {
        graphs += "\n";
        PlotData.counter = 0;
      }
    }*/
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
