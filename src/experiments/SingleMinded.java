package experiments;

import ilog.concert.IloException;

import java.util.HashMap;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import singleminded.algorithms.SingleMindedApproxWE;
import singleminded.algorithms.SingleMindedPricingLP;
import singleminded.algorithms.UnlimitedSupplyApproximation;
import singleminded.structures.SingleMindedMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.factory.SingleMindedMarketFactory;
import waterfall.Waterfall;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyAllocationFactory;
import allocations.interfaces.AllocationAlgo;
import allocations.optimal.EgalitarianMaxAllocationILP;
import allocations.optimal.WelfareMaxAllocationILP;

public class SingleMinded extends Experiments {

  /**
   * Constructor.
   * 
   * @param dbLogger
   * @throws Exception
   */
  public void runExperiments(SqlDB dbLogger) throws Exception {
    System.out.println("Single-minded Experiments:");
    for (String distribution : RunParameters.distributions) {
      for (int n = 2; n < RunParameters.numGoods; n++) {
        for (int m = 2; m < RunParameters.numBidder; m++) {
          for (int k = 1; k <= n; k++) {
            System.out.print(distribution + ": (n, m, k) = (" + n + ", " + m + ", " + k + ")");
            this.runOneExperiment(n, m, k, -1, distribution, dbLogger);
          }
        }
      }
    }
  }

  /**
   * Run single-minded experiments.
   * 
   * @throws Exception
   */
  @Override
  public void runOneExperiment(int numGoods, int numBidders, int k, double p, String distribution, SqlDB dbLogger) throws Exception {
    if (!dbLogger.checkIfRowExists("singleminded_" + distribution, numGoods, numBidders, k)) {
      System.out.print("\t Adding data... ");
      HashMap<String, DescriptiveStatistics> stats = new HashMap<String, DescriptiveStatistics>();
      for (int i = 0; i < RunParameters.numTrials; i++) {
        // Generate Single-minded random market.
        SingleMindedMarket<Goods, Bidder<Goods>> M = this.getSingleMindedMarket(numGoods, numBidders, k, distribution);
        // Optimal Utilitarian Allocation.
        MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> utilitarianMaxAlloc = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(M);
        double optimalWelfare = utilitarianMaxAlloc.getValue();
        // Optimal Egalitarian Allocation.
        MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> egalitarianMaxAlloc = new EgalitarianMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(M);
        double optimalEgalitarian = (double) egalitarianMaxAlloc.getNumberOfWinners();
        // Obtain statistics from all algorithms.
        this.populateStats(stats, new SingleMindedApproxWE(M).Solve(), "ap", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, new UnlimitedSupplyApproximation(M).Solve(), "us", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getLPMarketPrices(M, Allocations.GreedyWelfare), "gw", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getLPMarketPrices(M, Allocations.GreedyEgalitarian), "ge", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getLPMarketPrices(M, Allocations.OptimalWelfare), "ow", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getLPMarketPrices(M, Allocations.OptimalEgalitarian), "oe", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getLPMarketPrices(M, Allocations.WaterFall), "wf", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getLPMarketPrices(M, Allocations.MaxBidder), "mb", optimalWelfare, optimalEgalitarian);
      }
      System.out.println("done!");
      dbLogger.saveSingleMinded("singleminded_" + distribution, numGoods, numBidders, k, stats);
    } else {
      System.out.println("\t Already have data ");
    }
  }

  /**
   * Given parameters, return a sample single minded market.
   * 
   * @param numGoods
   * @param numBidders
   * @param k
   * @param distribution
   * @return
   * @throws Exception
   */
  public SingleMindedMarket<Goods, Bidder<Goods>> getSingleMindedMarket(int numGoods, int numBidders, int k, String distribution) throws Exception {
    switch (distribution) {
    case "Uniform":
      return SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(numGoods, numBidders, k);
    case "Elitist":
      return SingleMindedMarketFactory.elitistRewardRandomSingleMindedMarket(numGoods, numBidders, k);
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
   */
  public PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> getLPMarketPrices(SingleMindedMarket<Goods, Bidder<Goods>> market,
      Allocations whichAllocAlgo) throws IloException, AllocationException, GoodsException, MarketAllocationException, AllocationAlgoException,
      BidderCreationException, PrincingAlgoException {
    // Determine which allocation algorithm to use.
    AllocationAlgo<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocAlgo = null;
    switch (whichAllocAlgo) {
    case GreedyWelfare:
      allocAlgo = GreedyAllocationFactory.<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>GreedyAllocation();
      break;
    case GreedyEgalitarian:
      allocAlgo = GreedyAllocationFactory.<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>GreedyEgalitarianAllocation();
      break;
    case OptimalWelfare:
      allocAlgo = new WelfareMaxAllocationILP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    case OptimalEgalitarian:
      allocAlgo = new EgalitarianMaxAllocationILP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    case WaterFall:
      allocAlgo = new Waterfall<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market);
      break;
    case MaxBidder:
      allocAlgo = GreedyAllocationFactory.<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>GreedyMaxBidderAllocation();
      break;
    }
    long startTime = System.nanoTime();
    MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> alloc = allocAlgo.Solve(market);
    long endTime = System.nanoTime();
    return new SingleMindedPricingLP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(alloc).getStatistics(endTime - startTime);
  }
}
