package experiments;

import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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
import structures.factory.RandomMarketFactory;
import algorithms.pricing.SimplePricing;
import algorithms.pricing.error.PrincingAlgoException;
import algorithms.pricing.reserveprices.RevMaxHeuristic;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyAllocation;
import allocations.interfaces.AllocationAlgo;
import allocations.optimal.EgalitarianMaxAllocation;
import allocations.optimal.WelfareMaxAllocationILP;

public class SizeInterchangeable extends Experiments {

  /**
   * Constructor.
   * 
   * @param dbLogger
   * @throws Exception
   */
  @Override
  public void runExperiments(SqlDB dbLogger) throws Exception {
    System.out.println("Size Interchangeable Experiments:");
    List<Integer> coefficients = new ArrayList<Integer>();
    coefficients.add(1);
    coefficients.add(-1);
    coefficients.add(2);
    coefficients.add(-2);
    coefficients.add(3);
    coefficients.add(-3);
    for (int n = 2; n < RunParameters.numGoods; n++) {
      for (int m = 2; m < RunParameters.numBidder; m++) {
        for (Integer k : coefficients) {
          for (int p = 1; p <= 4; p++) {
            for (String distribution : RunParameters.distributions) {
              System.out.print(distribution + ": (n, m, k, p) = (" + n + ", " + m + ", " + k + ", " + (0.25 * p) + ")");
              this.runOneExperiment(n, m, k, 0.25 * p, distribution, dbLogger);
            }
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
    if (!dbLogger.checkIfRowExists("sizeinter_" + distribution, numGoods, numBidders, k, p)) {
      System.out.print("\t Adding data... ");
      HashMap<String, DescriptiveStatistics> stats = new HashMap<String, DescriptiveStatistics>();
      for (int i = 0; i < RunParameters.numTrials; i++) {
        // Generate Single-minded random market.
        Market<Goods, Bidder<Goods>> M = this.getSizeInterMarket(numGoods, numBidders, k, p, distribution);
        // Efficient Allocation.
        MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> efficientAlloc = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(M);
        double optimalWelfare = efficientAlloc.value();
        // Obtain statistics from all algorithms.
        this.populateStats(stats, new SimplePricing(M).Solve(), "sp", optimalWelfare);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.GreedyWelfare), "gw", optimalWelfare);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.GreedyEgalitarian), "ge", optimalWelfare);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.OptimalWelfare), "ow", optimalWelfare);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.OptimalEgalitarian), "oe", optimalWelfare);
      }
      System.out.println("done!");
      dbLogger.saveSizeInter("sizeinter_" + distribution, numGoods, numBidders, k, p, stats);
    } else {
      System.out.println("\t Already have data ");
    }
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
  public Market<Goods, Bidder<Goods>> getSizeInterMarket(int numGoods, int numBidders, int k, double p, String distribution) throws Exception {
    switch (distribution) {
    case "Uniform":
      return (k < 0) ? RandomMarketFactory.generateUniformRewardOverSuppliedMarket(numGoods, numBidders, p, -1 * k) : RandomMarketFactory.generateUniformRewardOverDemandedMarket(numGoods, numBidders, p, k);
    case "Elitist":
      return (k < 0) ? RandomMarketFactory.generateElitistRewardOverSuppliedMarket(numGoods, numBidders, p, -1 * k) : RandomMarketFactory.generateElitistRewardOverDemandedMarket(numGoods, numBidders, p, k);
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
