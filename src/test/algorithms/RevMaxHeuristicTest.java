package test.algorithms;

import static org.junit.Assert.fail;

import org.junit.Test;

import singleton.structures.SingletonMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.factory.SingletonMarketFactory;
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.reserveprices.RevMaxHeuristic;
import allocations.greedy.GreedyAllocation;

public class RevMaxHeuristicTest<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> {

  @Test
  public void testSearchMetaHeuristicOnSingleton() throws Exception {
    for (int n = 1; n < 8; n++) {
      for (int m = 1; m < 8; m++) {
        for (int p = 1; p <= 4; p++) {
          // SingletonMarket<Goods, Bidder<Goods>> market = SingletonMarketFactory.uniformRewardSingletonRandomMarket(n, m, p*0.25);
          SingletonMarket<Goods, Bidder<Goods>> market = SingletonMarketFactory.elitistRewardSingletonRandomMarket(n, m, p * 0.25);
          GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
          PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> stat = new RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(ga.Solve(market)).getStatistics();
          PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> rmhStats = new RevMaxHeuristic(market, ga).getStatistics();

          if (stat.getSellerRevenue() > rmhStats.getSellerRevenue()) {
            System.out.println(market);
            stat.getMarketOutcome().getMarketAllocation().printAllocation();
            stat.getMarketOutcome().printPrices();
            System.out.println("Revenue of LP = " + stat.getSellerRevenue());
            System.out.println("EF violations = " + stat.listOfEnvyBidders() + "\n");
            rmhStats.getMarketOutcome().getMarketAllocation().printAllocation();
            rmhStats.getMarketOutcome().printPrices();
            System.out.println("Revenue of rev Max = " + rmhStats.getSellerRevenue());
            System.out.println("EF violations = " + rmhStats.listOfEnvyBidders());
            fail("The revenue max heuristic should be as good as the LP");
          }
        }
      }
    }
  }

}
