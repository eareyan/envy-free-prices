package test.algorithms;

import org.junit.Test;

import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.factory.SingletonMarketFactory;
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.RestrictedEnvyFreePricesLPSolution;
import algorithms.pricing.reserveprices.RevMaxHeuristic;
import allocations.interfaces.AllocationAlgo;
import allocations.optimal.SingleStepWelfareMaxAllocationILP;

public class SearchMetaHeuristicTest <M extends Market<G, B>, G extends Goods, B extends Bidder<G>>{

  @SuppressWarnings("unchecked")
  @Test
  public void testSearchMetaHeuristic() throws Exception {
    Market<Goods, Bidder<Goods>> x = SingletonMarketFactory.uniformRewardRandomMarket(6, 3, 0.5);
    
    SingleStepWelfareMaxAllocationILP<M, G, B> optAlloc = new SingleStepWelfareMaxAllocationILP<M, G, B>();
    //EgalitarianMaxAllocation<M, G, B> optAlloc = new EgalitarianMaxAllocation<M, G, B>();
    System.out.println(x);
    MarketAllocation<M, G, B> y = optAlloc.Solve((M) x);
    RestrictedEnvyFreePricesLP<M, G, B> r = new RestrictedEnvyFreePricesLP<M,G,B>(y);
    //r.setMarketClearanceConditions(true);
    r.createLP();
    RestrictedEnvyFreePricesLPSolution<M, G, B> z = r.Solve();
    PricesStatistics<M, G, B> ps = new PricesStatistics<M, G, B>(z);

    y.printAllocation();
    z.printPrices();
    System.out.println("Revenue of LP = " + z.sellerRevenue());
    System.out.println("EF violations = " + ps.getEFViolationsRatio()+ "\n");

    
    RevMaxHeuristic rmh = new RevMaxHeuristic(x, (AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>) optAlloc);
    MarketOutcome<M, G, B> w = (MarketOutcome<M, G, B>) rmh.Solve();

    w.getMarketAllocation().printAllocation();
    w.printPrices();
    System.out.println("Revenue of rev Max = " + w.sellerRevenue());
    System.out.println("EF violations = " + new PricesStatistics<M, G, B>(w).getEFViolationsRatio());
    
  }

}
