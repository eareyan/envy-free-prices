package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;
import java.util.HashMap;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import statistics.PricesStatistics;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;

/**
 * Abstract class to test algorithms.
 * 
 * @author Enrique Areyan Viqueira
 */
public abstract class Experiments {
  
  public enum Allocations {
    GreedyWelfare, GreedyEgalitarian, OptimalWelfare, OptimalEgalitarian
  }

  /**
   * This method is implemented by a particular type of experiment class. This method receives the number of goods, number of bidders and probability, and runs
   * one experiments, saving the results in the database. This method should also check if we have that result first before running the experiment.
   * 
   * @param numGoods
   * @param numBidders
   * @param prob
   * @param b
   * @param dbLogger
   * @throws SQLException
   * @throws IloException
   * @throws AllocationAlgoException
   * @throws BidderCreationException
   * @throws MarketAllocationException
   * @throws MarketOutcomeException
   * @throws GoodsCreationException
   * @throws AllocationException
   * @throws GoodsException
   * @throws MarketCreationException
   * @throws PrincingAlgoException
   * @throws Exception
   */
  abstract public void runOneExperiment(int numGoods, int numBidders, int k, double p, String distribution, SqlDB dbLogger) throws SQLException, IloException,
      AllocationAlgoException, BidderCreationException, MarketAllocationException, MarketOutcomeException, GoodsCreationException, GoodsException,
      AllocationException, MarketCreationException, PrincingAlgoException, Exception;

  /**
   * Runs the experiments.
   * 
   * @param dbLogger
   * @throws Exception 
   */
  abstract public void runExperiments(SqlDB dbLogger) throws Exception;
  
  /**
   * Main method to run experiments.
   * 
   * @param args
   *          - command line arguments.
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    RunParameters Parameters = new RunParameters(args);
    SqlDB dbLogger = new SqlDB(Parameters.dbProvider, Parameters.dbHost, Parameters.dbPort, Parameters.dbName, Parameters.dbUsername, Parameters.dbPassword);
    Parameters.experimentObject.runExperiments(dbLogger);
  }

  /**
   * Keeps tracks of the statistics.
   * 
   * @param stats
   * @param ps
   * @param id
   * @param optimalWelfare
   * @throws MarketAllocationException
   * @throws MarketOutcomeException
   */
  public void populateStats(HashMap<String, DescriptiveStatistics> stats, @SuppressWarnings("rawtypes") PricesStatistics ps,
      String id, double optimalWelfare) throws MarketAllocationException, MarketOutcomeException {
    this.getDS(stats, id + "Welfare").addValue(ps.getWelfareRatio(optimalWelfare));
    this.getDS(stats, id + "Revenue").addValue(ps.getSellerRevenueRatio(optimalWelfare));
    this.getDS(stats, id + "EF").addValue(ps.getEFViolationsRatio());
    this.getDS(stats, id + "EFLoss").addValue(ps.getRatioLossUtility());
    this.getDS(stats, id + "MC").addValue(ps.getMCViolationsRatio());
    this.getDS(stats, id + "MCLoss").addValue((double) ps.getMarketClearanceViolations().getValue());
    this.getDS(stats, id + "Time").addValue(ps.getTime() / 1000000000.0);
  }

  /**
   * Returns the DescriptiveStatistics objects if it exists or creates it.
   * 
   * @param stats
   * @param id
   * @return
   */
  public DescriptiveStatistics getDS(HashMap<String, DescriptiveStatistics> stats, String id) {
    DescriptiveStatistics ds;
    if (stats.containsKey(id)) {
      ds = stats.get(id);
    } else {
      ds = new DescriptiveStatistics();
      stats.put(id, ds);
    }
    return ds;
  }

}
