package experiments;

import ilog.concert.IloException;
import singleminded.algorithms.SingleMindedGreedyAllocation;
import singleminded.algorithms.SingleMindedPricingLP;
import singleminded.structures.SingleMindedMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.comparators.BiddersComparatorBy1ToSqrtIRatio;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;
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

  public static PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> getMarketPrices(SingleMindedMarket<Goods, Bidder<Goods>> market, Allocations whichAllocAlgo) throws IloException, AllocationException, GoodsException, MarketAllocationException, AllocationAlgoException, BidderCreationException, PrincingAlgoException {
    // Determine which allocation algorithm to use.
    AllocationAlgo<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocAlgo = null;
    switch (whichAllocAlgo) {
    case GreedyWelfare:
      allocAlgo = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    case GreedyEgalitarian:
      allocAlgo = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(new BiddersComparatorBy1ToSqrtIRatio<Goods, Bidder<Goods>>());
      break;
    case OptimalWelfare:
      allocAlgo = new SingleStepWelfareMaxAllocationILP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    case OptimalEgalitarian:
      allocAlgo = new EgalitarianMaxAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
      break;
    }
    long startTime = System.nanoTime();
    MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> alloc = allocAlgo.Solve(market);
    long endTime = System.nanoTime();
    return new SingleMindedPricingLP<Goods, Bidder<Goods>>(alloc).getStatistics(endTime - startTime);
  }

}
