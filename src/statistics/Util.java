package statistics;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import log.SqlDB;
import experiments.RunParameters;

public class Util {

  public final static List<Entry<String, String>> baseAlgos = new ArrayList<Entry<String, String>>();
  public final static List<Entry<String, String>> singleMindedAlgos = new ArrayList<Entry<String, String>>();
  public final static List<Entry<String, String>> singletonAlgos = new ArrayList<Entry<String, String>>();
  public final static List<Entry<String, String>> sizeInterAlgos = new ArrayList<Entry<String, String>>();
  public final static List<Entry<String, String>> metrics = new ArrayList<Entry<String, String>>();

  static {
    baseAlgos.add(new AbstractMap.SimpleEntry<String, String>("LP greedy utilitarian", "gw"));
    baseAlgos.add(new AbstractMap.SimpleEntry<String, String>("LP greedy egalitarian", "ge"));
    baseAlgos.add(new AbstractMap.SimpleEntry<String, String>("LP optimal utilitarian", "ow"));
    baseAlgos.add(new AbstractMap.SimpleEntry<String, String>("LP optimal egalitarian", "oe"));
    
    singleMindedAlgos.add(new AbstractMap.SimpleEntry<String, String>("Huang et. al.", "ap"));
    singleMindedAlgos.add(new AbstractMap.SimpleEntry<String, String>("Guruswami et. al. Unl. Supply", "us"));
    singleMindedAlgos.addAll(Util.baseAlgos);

    singletonAlgos.add(new AbstractMap.SimpleEntry<String, String>("Guruswami Envy-Free Approx.", "ev"));
    singletonAlgos.addAll(Util.baseAlgos);
 
    sizeInterAlgos.add(new AbstractMap.SimpleEntry<String, String>("Simple Pricing", "sp"));
    sizeInterAlgos.addAll(Util.baseAlgos);
    
    metrics.add(new AbstractMap.SimpleEntry<String, String>("Welfare", "Welfare"));
    metrics.add(new AbstractMap.SimpleEntry<String, String>("Revenue", "Revenue"));
    metrics.add(new AbstractMap.SimpleEntry<String, String>("EF", "EF"));
    metrics.add(new AbstractMap.SimpleEntry<String, String>("EF Loss", "EFLoss"));
    metrics.add(new AbstractMap.SimpleEntry<String, String>("MC", "MC"));
    metrics.add(new AbstractMap.SimpleEntry<String, String>("MC Loss", "MCLoss"));
    metrics.add(new AbstractMap.SimpleEntry<String, String>("Time", "Time"));
  }

  public static SqlDB getDB(String[] args) throws Exception {
    RunParameters rp = new RunParameters(args);
    return new SqlDB(rp.dbProvider, rp.dbHost, rp.dbPort, rp.dbName, rp.dbUsername, rp.dbPassword);
  }
}
