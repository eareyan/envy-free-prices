package test.algorithms;

import static org.junit.Assert.fail;

import org.junit.Test;

import singleminded.algorithms.SingleMindedPricingLP;
import singleminded.structures.SingleMindedMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.factory.SingleMindedMarketFactory;
import allocations.greedy.GreedyAllocation;

public class SingleMindedPricingLPTest {

  @Test
  public void testSingleMindedPricingLP() throws Exception {
    for (int n = 1; n < 10; n++) {
      for (int m = 1; m < 10; m++) {
        for (int k = n; k <= n; k++) {
          SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(n, m, k);
          MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = new GreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(market);
          PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> smplp = new SingleMindedPricingLP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(ga).getStatistics();
          if (smplp.listOfEnvyBidders() == null) {
            fail("Null list");
          }
        }
      }
    }
  }

}
