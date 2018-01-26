package test.algorithms;

import static org.junit.Assert.fail;

import org.junit.Test;

import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.factory.RandomMarketFactory;
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import allocations.greedy.GreedyAllocationFactory;

public class RestrictedEnvyFreePricesLPTest {

  @Test
  public void testCreateLP() throws Exception {
    for (int n = 1; n < 10; n++) {
      for (int m = 1; m < 10; m++) {
        for (int p = 1; p <= 4; p++) {
          Market<Goods, Bidder<Goods>> market = RandomMarketFactory.randomUniformRewardMarket(n, m, p * 0.25);
          MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = GreedyAllocationFactory.GreedyAllocation().Solve(market);
          PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> refp = new RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(ga).getStatistics();
          if (refp.listOfEnvyBidders() == null) {
            fail("Null list");
          }
        }
      }
    }
  }

}
