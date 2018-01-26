package test.algorithms;

import static org.junit.Assert.fail;

import org.junit.Test;

import singleton.algorithms.SingletonPricingLP;
import singleton.structures.SingletonMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.factory.SingletonMarketFactory;
import allocations.greedy.GreedyAllocationFactory;

public class SingletonPricingLPTest {

  @Test
  public void testLP0() {
    for (int n = 1; n < 10; n++) {
      for (int m = 1; m < 10; m++) {
        for (int p = 1; p <= 4; p++) {
          try {
            SingletonMarket<Goods, Bidder<Goods>> market = SingletonMarketFactory.elitistRewardSingletonRandomMarket(n, m, p * 0.25);
            MarketAllocation<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = GreedyAllocationFactory.<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>GreedyAllocation().Solve(market);
            PricesStatistics<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> stat = new SingletonPricingLP<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(ga).getStatistics();
            stat.getSellerRevenue();
          } catch (Exception e) {
            fail("Found an exception --> " + e);
          }
        }
      }
    }
  }

  @Test
  public void testLP1() throws Exception {
    for (int n = 1; n < 10; n++) {
      for (int m = 1; m < 10; m++) {
        SingletonMarket<Goods, Bidder<Goods>> market = SingletonMarketFactory.uniformRewardSingletonRandomMarket(n, m, 1.0);
        MarketAllocation<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = GreedyAllocationFactory.<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>GreedyAllocation().Solve(market);
        PricesStatistics<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> stat = new SingletonPricingLP<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(ga).getStatistics();
        double price = stat.getMarketOutcome().getPrice(market.getGoods().get(0));
        for (Goods good : market.getGoods()) {
          if (price != stat.getMarketOutcome().getPrice(good)) {
            System.out.println(market);
            ga.printAllocation();
            stat.getMarketOutcome().printPrices();
            fail("We found a distinct price");
          }
        }
      }
    }
  }

}
