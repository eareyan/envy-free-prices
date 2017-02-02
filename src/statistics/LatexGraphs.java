package statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Iterables;

import log.SqlDB;

public class LatexGraphs {
  
  public static int counter = 0;

  public static class PlotData {
    String sql;
    String alias;
    String filelocation;
    String title;
    String xLabel;
    String xCol;
    String metric;

    public PlotData(String sql, String alias, String filelocation, String title, String xLabel, String xCol, String metric) {
      this.sql = sql;
      this.alias = alias;
      this.filelocation = filelocation;
      this.title = title;
      this.xLabel = xLabel;
      this.xCol = xCol;
      this.metric = metric;
    }
  }

  public static String getAvgSqlCols(String metric, List<Entry<String, String>> algos) {
    String ret = "";
    for (Entry<String, String> algo : algos) {
      ret += "AVG(" + algo.getValue() + metric + ") AS " + algo.getValue() + metric + ",";
    }
    return ret.substring(0, ret.length() - 1);
  }

  public static ArrayList<PlotData> getData(boolean fixItems, List<Entry<String, String>> algos, String bidders, String reward, boolean overDemanded, String where) {
    ArrayList<PlotData> tables = new ArrayList<PlotData>();
    String type = overDemanded ? "OverDemanded" : "UnderDemanded";
    String col = fixItems ? "m" : "n";
    for (Entry<String, String> metric : Util.metrics) {
      tables.add(new PlotData("SELECT "+col+", " + getAvgSqlCols(metric.getValue(), algos) + " FROM " + bidders + "_" + reward + " " + where
          + " GROUP BY "+col+" ORDER BY "+col, bidders + reward + type + metric.getValue(), bidders + "/" + reward + "/" + type + metric.getValue() + ".csv", metric
          .getValue() + " " + bidders + " " + reward + type, "Bidders", col, metric.getValue()));
    }
    return tables;
  }

  public static Iterable<PlotData> getSingleMindedPlotData() {
    ArrayList<PlotData> tablesUniformOverDemanded = getData(true, Util.singleMindedAlgos, "singleminded", "uniform", true, "WHERE n = 10 AND k*m>n");
    ArrayList<PlotData> tablesUniformUnderDemanded = getData(true, Util.singleMindedAlgos, "singleminded", "uniform", false, "WHERE n = 10 AND k*m<=n");
    ArrayList<PlotData> tablesElitistOverDemanded = getData(true, Util.singleMindedAlgos, "singleminded", "elitist", true, "WHERE n = 10 AND k*m>n");
    ArrayList<PlotData> tablesElitistUnderDemanded = getData(true, Util.singleMindedAlgos, "singleminded", "elitist", false, "WHERE n = 10 AND k*m<=n");
    return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded, tablesElitistOverDemanded, tablesElitistUnderDemanded));
  }

  public static Iterable<PlotData> getSingletonPlotData() {
    ArrayList<PlotData> tablesUniformOverDemanded = getData(true, Util.singletonAlgos, "singleton", "uniform", true, "WHERE n = 5 AND p*m>=n");
    ArrayList<PlotData> tablesUniformUnderDemanded = getData(true, Util.singletonAlgos, "singleton", "uniform", false, "WHERE n = 10 AND p*m<=n");
    ArrayList<PlotData> tablesElitistOverDemanded = getData(true, Util.singletonAlgos, "singleton", "elitist", true, "WHERE n = 5 AND p*m>=n");
    ArrayList<PlotData> tablesElitistUnderDemanded = getData(true, Util.singletonAlgos, "singleton", "elitist", false, "WHERE n = 10 AND p*m<=n");
    return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded, tablesElitistOverDemanded, tablesElitistUnderDemanded));
  }
  
  public static Iterable<PlotData> getSizeInterPlotData() {
    ArrayList<PlotData> tablesUniformOverDemanded = getData(true, Util.sizeInterAlgos, "sizeinter", "uniform", true, "WHERE n = 7 AND p*m>=n");
    ArrayList<PlotData> tablesUniformUnderDemanded = getData(true, Util.sizeInterAlgos, "sizeinter", "uniform", false, "WHERE n = 7 AND p*m<=n");
    ArrayList<PlotData> tablesElitistOverDemanded = getData(true, Util.sizeInterAlgos, "sizeinter", "elitist", true, "WHERE n = 7 AND p*m>=n");
    ArrayList<PlotData> tablesElitistUnderDemanded = getData(true, Util.sizeInterAlgos, "sizeinter", "elitist", false, "WHERE n = 7 AND p*m<=n");
    return Iterables.unmodifiableIterable(Iterables.concat(tablesUniformOverDemanded, tablesUniformUnderDemanded, tablesElitistOverDemanded, tablesElitistUnderDemanded));
  }

  public static void main(String[] args) throws Exception {
    SqlDB sqlDB = Util.getDB(args);
    String dir = "/home/eareyanv/workspace/envy-free-prices/results/current/latexgraphs/";

    Iterable<PlotData> tables = Iterables.unmodifiableIterable(Iterables.concat(getSingleMindedPlotData(), getSingletonPlotData(), getSizeInterPlotData()));
    System.out.println(generateHeader(tables));
    int i = 0;
    for (PlotData t : getSingleMindedPlotData()) {
      CollectResults.sqlToCSV(sqlDB, t.sql, dir, t.filelocation);
      System.out.println(generateLatexGraph(Util.singleMindedAlgos, t.title, t.xLabel, t.alias, t.xCol, t.metric));
      i++;
      if (i % 14 == 0)
        System.out.println("\\clearpage");
    }

    for (PlotData t : getSingletonPlotData()) {
      CollectResults.sqlToCSV(sqlDB, t.sql, dir, t.filelocation);
      System.out.println(generateLatexGraph(Util.singletonAlgos, t.title, t.xLabel, t.alias, t.xCol, t.metric));
      i++;
      if (i % 14 == 0)
        System.out.println("\\clearpage");
    }
    
    for (PlotData t : getSizeInterPlotData()) {
      CollectResults.sqlToCSV(sqlDB, t.sql, dir, t.filelocation);
      System.out.println(generateLatexGraph(Util.sizeInterAlgos, t.title, t.xLabel, t.alias, t.xCol, t.metric));
      i++;
      if (i % 14 == 0)
        System.out.println("\\clearpage");
    }

    System.out.println(generateFooter());
  }

  public static String generateHeader(Iterable<PlotData> tables) {
    String header = "";
    header += "\\documentclass{article}\n";
    header += "\\usepackage{pgfplots}\n";
    header += "\\usepackage{booktabs}\n";
    header += "\\pgfplotsset{compat=1.11}\n";
    header += "\\usepackage{geometry}\\geometry{a4paper,total={170mm,257mm},left=20mm,top=20mm,}\n";
    for (PlotData t : tables) {
      header += "\\pgfplotstableread[col sep=comma]{" + t.filelocation + "}\\" + t.alias + "\n";
    }
    header += "\\newcommand*{\\figuretitle}[1]{{\\centering\\textbf{#1}\\par\\medskip}}\n";
    header += "\\begin{document}\n";
    return header;
  }

  public static String generateFooter() {
    return "\\end{document}\n";
  }

  public static String generateLatexGraph(List<Entry<String, String>> algos, String title, String xLabel, String csvFileAlias, String xCol, String metric) {

    String graph = "";
    //graph += "\\figuretitle{" + title + "}\n";
    graph += "\\begin{tikzpicture}[scale=1]\n";
    graph += "\t\\begin{axis}[xlabel = " + title + ", ylabel = Average ratio, legend style={at={(1,0.58)},font=\\tiny} ]\n";
    for (Entry<String, String> algo : algos) {
      graph += "\t\t\\addplot table[x = " + xCol + ", y = " + algo.getValue() + metric.toLowerCase() + "]{\\" + csvFileAlias + "};\n";
      graph += "\t\t\\addlegendentry{" + algo.getKey() + "}\n";
    }
    graph += "\t\\end{axis}\n";
    graph += "\\end{tikzpicture}";
    if(++counter %2 == 0) {
      graph += "\n";
    }
    return graph;
  }
}
