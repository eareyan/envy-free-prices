package statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class PlotData {
  
  public static int counter = 0;

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

  public static String generateHeader(Iterable<PlotData> tables) {
    String header = "";
    header += "\\documentclass{article}\n";
    header += "\\usepackage{pgfplots}\n";
    header += "\\usepackage{booktabs}\n";
    header += "\\pgfplotsset{compat=1.11}\n";
    header += "\\usepackage{geometry}\\geometry{a4paper,total={170mm,257mm},left=0mm,top=20mm,}\n";
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
    // graph += "\\figuretitle{" + title + "}\n";
    graph += "\\begin{tikzpicture}[scale=0.9]\n";
    graph += "\t\\begin{axis}[xlabel = " + title + ", ylabel = Average ratio, legend style={at={(0,1)},font=\\tiny} ]\n";
    for (Entry<String, String> algo : algos) {
      graph += "\t\t\\addplot table[x = " + xCol + ", y = " + algo.getValue() + metric.toLowerCase() + "]{\\" + csvFileAlias + "};\n";
      graph += "\t\t\\addlegendentry{" + algo.getKey() + "}\n";
    }
    graph += "\t\\end{axis}\n";
    graph += "\\end{tikzpicture}";
    if (++counter % 2 == 0) {
      graph += "\n";
    }
    return graph;
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
      tables.add(new PlotData("SELECT "+col+", " + getAvgSqlCols(metric.getValue(), algos) + " FROM " + bidders + "_"  + reward + " " + where
          + " GROUP BY "+col+" ORDER BY "+col, bidders + reward + type + metric.getValue(), bidders + "/" + reward + "/" + type + metric.getValue() + ".csv", metric
          .getValue() + " " + bidders + " " + reward + type, "Bidders", col, metric.getValue()));
    }
    return tables;
  }
}
