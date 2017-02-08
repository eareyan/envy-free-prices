package experiments;

import java.util.HashMap;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.factory.TACMarketFactory;
import algorithms.pricing.SimplePricing;
import allocations.optimal.EgalitarianMaxAllocationILP;
import allocations.optimal.WelfareMaxAllocationILP;

public class TAC extends Experiments {

  /**
   * Constructor.
   * 
   * @param dbLogger
   * @throws Exception
   */
  @Override
  public void runExperiments(SqlDB dbLogger) throws Exception {
    System.out.println("TAC Experiments:");
    for (int m = 2; m < 101; m++) {
      System.out.print("TAC : m = (" + m + ")");
      this.runOneExperiment(-1, m, -1, -1.0, "", dbLogger);
    }
  }

  /**
   * Run size interchangeable experiments.
   * 
   * @throws Exception
   */
  @Override
  public void runOneExperiment(int numGoods, int numBidders, int k, double p, String distribution, SqlDB dbLogger) throws Exception {
    if (!dbLogger.checkIfRowExists("TAC", numBidders)) {
      System.out.print("\t Adding data... ");
      HashMap<String, DescriptiveStatistics> stats = new HashMap<String, DescriptiveStatistics>();
      for (int i = 0; i < RunParameters.numTrials; i++) {
        // Generate Single-minded random market.
        Market<Goods, Bidder<Goods>> M = TACMarketFactory.RandomTACMarket(numBidders);
        // Optimal Utilitarian Allocation.
        MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> utilitarianMaxAlloc = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(M);
        double optimalWelfare = utilitarianMaxAlloc.getValue();
        // Optimal Egalitarian Allocation.
        MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> egalitarianMaxAlloc = new EgalitarianMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(M);
        double optimalEgalitarian = (double) egalitarianMaxAlloc.getNumberOfWinners();
        // Obtain statistics from all algorithms.
        this.populateStats(stats, new SimplePricing(M).Solve(), "sp", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, SizeInterchangeable.getRevMaxMarketPrices(M, Allocations.GreedyWelfare), "gw", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, SizeInterchangeable.getRevMaxMarketPrices(M, Allocations.GreedyEgalitarian), "ge", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, SizeInterchangeable.getRevMaxMarketPrices(M, Allocations.OptimalWelfare), "ow", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, SizeInterchangeable.getRevMaxMarketPrices(M, Allocations.OptimalEgalitarian), "oe", optimalWelfare, optimalEgalitarian);
      }
      System.out.println("done!");
      dbLogger.saveTAC("TAC", numBidders, stats);
    } else {
      System.out.println("\t Already have data ");
    }
  }

}
