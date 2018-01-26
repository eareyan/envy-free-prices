package experiments;

import ilog.concert.IloException;

import java.util.HashMap;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import singleton.algorithms.SingletonEVP;
import singleton.structures.SingletonMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.SingletonMarketFactory;
import waterfall.Waterfall;
import algorithms.pricing.error.PrincingAlgoException;
import algorithms.pricing.reserveprices.RevMaxHeuristic;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyAllocationFactory;
import allocations.interfaces.AllocationAlgo;
import allocations.optimal.EgalitarianMaxAllocationILP;
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
        for (Double p : RunParameters.probabilities) {
          for (String distribution : RunParameters.distributions) {
            System.out.print(distribution + ": (n, m, p) = (" + n + ", " + m + ", " + p + ")");
            this.runOneExperiment(n, m, -1, p, distribution, dbLogger);
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
        // Generate Singleton random market.
        SingletonMarket<Goods, Bidder<Goods>> M = this.getSingletonMarket(numGoods, numBidders, p, distribution);
        // Optimal Utilitarian Allocation.
        MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> utilitarianMaxAlloc = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(M);
        double optimalWelfare = utilitarianMaxAlloc.getValue();
        // Optimal Egalitarian Allocation.
        MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> egalitarianMaxAlloc = new EgalitarianMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(M);
        double optimalEgalitarian = (double) egalitarianMaxAlloc.getNumberOfWinners();
        // Obtain statistics from all algorithms.
        this.populateStats(stats, new SingletonEVP(M).Solve(), "ev", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.GreedyWelfare), "gw", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.GreedyEgalitarian), "ge", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.OptimalWelfare), "ow", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.OptimalEgalitarian), "oe", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.WaterFall), "wf", optimalWelfare, optimalEgalitarian);
        this.populateStats(stats, this.getRevMaxMarketPrices(M, Allocations.MaxBidder), "gm", optimalWelfare, optimalEgalitarian);
      }
      System.out.println("done!");
      dbLogger.saveSingleton("singleton_" + distribution, numGoods, numBidders, p, stats);
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
      allocAlgo = GreedyAllocationFactory.GreedyAllocation();
      break;
    case GreedyEgalitarian:
      allocAlgo = GreedyAllocationFactory.GreedyEgalitarianAllocation();
      break;
    case OptimalWelfare:
      allocAlgo = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    case OptimalEgalitarian:
      allocAlgo = new EgalitarianMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    case WaterFall:
      allocAlgo = new Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market);
      break;
    case MaxBidder:
      allocAlgo = GreedyAllocationFactory.GreedyMaxBidderAllocation();
      break;
    }
    return new RevMaxHeuristic(market, allocAlgo).getStatistics();
  }

}
