package experiments;

import ilog.concert.IloException;

import java.util.HashMap;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import singleton.structures.SingletonMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.comparators.BiddersComparatorBy1ToSqrtIRatio;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.SingletonMarketFactory;
import structures.factory.UnitDemandMarketAllocationFactory;
import unitdemand.algorithms.EVPApproximation;
import unitdemand.structures.UnitDemandMarketOutcome;
import algorithms.pricing.error.PrincingAlgoException;
import algorithms.pricing.reserveprices.RevMaxHeuristic;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyAllocation;
import allocations.interfaces.AllocationAlgo;
import allocations.optimal.EgalitarianMaxAllocation;
import allocations.optimal.WelfareMaxAllocationILP;

public class Singleton extends Experiments {

  /**
   * Constructor.
   * 
   * @param dbLogger
   * @throws Exception
   */
  @Override
  public void runExperiments(SqlDB dbLogger) throws Exception {
    System.out.println("Singleton Experiments:");
    for (int n = 2; n < RunParameters.numGoods; n++) {
      for (int m = 2; m < RunParameters.numBidder; m++) {
        for (int p = 1; p <= 4; p++) {
          for (String distribution : RunParameters.distributions) {
            System.out.print(distribution + ": (n, m, p) = (" + n + ", " + m + ", " + (0.25 * p) + ")");
            this.runOneExperiment(n, m, -1, 0.25 * p, distribution, dbLogger);
          }
        }
      }
    }
  }

  /**
   * Run size interchangeable experiments.
   * 
   * @throws Exception
   */
  @Override
  public void runOneExperiment(int numGoods, int numBidders, int k, double p, String distribution, SqlDB dbLogger) throws Exception {
    if (!dbLogger.checkIfRowExists("singleton_" + distribution, numGoods, numBidders, p)) {
      System.out.print("\t Adding data... ");
      HashMap<String, DescriptiveStatistics> stats = new HashMap<String, DescriptiveStatistics>();
      for (int i = 0; i < RunParameters.numTrials; i++) {
        // Generate Single-minded random market.
        SingletonMarket<Goods, Bidder<Goods>> M = this.getSingletonMarket(numGoods, numBidders, p, distribution);
        // Efficient Allocation.
        MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> efficientAlloc = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(M);
        double optimalWelfare = efficientAlloc.value();
        // Obtain statistics from all algorithms.
        this.populateStats(stats, new EVPApproximation(UnitDemandMarketAllocationFactory.getValuationMatrixFromMarket(M)).Solve(), "ev", optimalWelfare);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.GreedyWelfare), "gw", optimalWelfare);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.GreedyEgalitarian), "ge", optimalWelfare);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.OptimalWelfare), "ow", optimalWelfare);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.OptimalEgalitarian), "oe", optimalWelfare);
      }
      System.out.println("done!");
      dbLogger.saveSingleton("singleton_" + distribution, numGoods, numBidders, p, stats);
    } else {
      System.out.println("\t Already have data ");
    }
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
  public void populateStats(HashMap<String, DescriptiveStatistics> stats, UnitDemandMarketOutcome ps,
      String id, double optimalWelfare) throws MarketAllocationException, MarketOutcomeException {
    this.getDS(stats, id + "Welfare").addValue(ps.getMarketAllocation().getWelfareRatio(optimalWelfare));
    this.getDS(stats, id + "Revenue").addValue(ps.getSellerRevenueRatio(optimalWelfare));
    this.getDS(stats, id + "EF").addValue(ps.getEFViolationsRatio());
    this.getDS(stats, id + "EFLoss").addValue(0.0);
    this.getDS(stats, id + "MC").addValue(ps.getMCViolationsRatio());
    this.getDS(stats, id + "MCLoss").addValue(0.0);
    this.getDS(stats, id + "Time").addValue(ps.getTime() / 1000000000.0);
  }

  /**
   * Given parameters, return a sample singleton minded market.
   * 
   * @param numGoods
   * @param numBidders
   * @param k
   * @param distribution
   * @return
   * @throws Exception
   */
  public SingletonMarket<Goods, Bidder<Goods>> getSingletonMarket(int numGoods, int numBidders, double p, String distribution) throws Exception {
    switch (distribution) {
    case "Uniform":
      return SingletonMarketFactory.uniformRewardSingletonRandomMarket(numGoods, numBidders, p);
    case "Elitist":
      return SingletonMarketFactory.elitistRewardSingletonRandomMarket(numGoods, numBidders, p);
    default:
      throw new Exception("Unknown distribution: " + distribution);
    }
  }

  /**
   * 
   * @param market
   * @param whichAllocAlgo
   * @return
   * @throws IloException
   * @throws AllocationException
   * @throws GoodsException
   * @throws MarketAllocationException
   * @throws AllocationAlgoException
   * @throws BidderCreationException
   * @throws PrincingAlgoException
   * @throws MarketCreationException
   * @throws MarketOutcomeException
   */
  public PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> getRevMaxMarketPrices(Market<Goods, Bidder<Goods>> market, Allocations whichAllocAlgo) throws IloException, AllocationException, GoodsException, MarketAllocationException, AllocationAlgoException, BidderCreationException, PrincingAlgoException, MarketOutcomeException, MarketCreationException {
    // Determine which allocation algorithm to use.
    AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocAlgo = null;
    switch (whichAllocAlgo) {
    case GreedyWelfare:
      allocAlgo = new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    case GreedyEgalitarian:
      allocAlgo = new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(new BiddersComparatorBy1ToSqrtIRatio<Goods, Bidder<Goods>>());
      break;
    case OptimalWelfare:
      allocAlgo = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    case OptimalEgalitarian:
      allocAlgo = new EgalitarianMaxAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    }
    return new RevMaxHeuristic(market, allocAlgo).getStatistics();
  }

}
