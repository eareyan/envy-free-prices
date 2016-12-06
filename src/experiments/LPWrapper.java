package experiments;

import ilog.concert.IloException;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.comparators.BiddersComparatorBy1ToSqrtIRatio;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.RestrictedEnvyFreePricesLPSolution;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyAllocation;
import allocations.interfaces.AllocationAlgo;
import allocations.optimal.EgalitarianMaxAllocation;
import allocations.optimal.SingleStepWelfareMaxAllocationILP;

/**
 * This class is a wrapper to encapsulate the 2 step process - allocation plus
 * pricing.
 * 
 * @author Enrique Areyan Viqueira
 */
public class LPWrapper {

  public static enum Allocations {
    GreedyWelfare, GreedyEgalitarian, OptimalWelfare, OptimalEgalitarian
  }

  public static PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> getMarketPrices(Market<Goods, Bidder<Goods>> market, Allocations whichAllocAlgo) throws IloException, AllocationException, GoodsException, MarketAllocationException, AllocationAlgoException, BidderCreationException {
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
      allocAlgo = new SingleStepWelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    case OptimalEgalitarian:
      allocAlgo = new EgalitarianMaxAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    }
    // Run the LP using the allocation algorithm from before.
    RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> efp = new RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(allocAlgo.Solve(market));
    efp.setMarketClearanceConditions(false);
    efp.createLP();
    long startTime = System.nanoTime();
    RestrictedEnvyFreePricesLPSolution<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> lpResult = efp.Solve();
    long endTime = System.nanoTime();
    // Returns the prices statistics object. 
    return new PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(lpResult, endTime - startTime);
  }

}
