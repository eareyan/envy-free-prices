package test.algorithms;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import singleton.algorithms.SingletonEVP;
import singleton.structures.SingletonMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.factory.SingletonMarketFactory;

public class SingletonEVPTest {

  @Test
  public void testSolve() throws Exception {
    for (int n = 2; n < 10; n++) {
      for (int m = 2; m < 10; m++) {
        for (int p = 1; p <= 4; p++) {
          SingletonMarket<Goods, Bidder<Goods>> market = SingletonMarketFactory.uniformRewardSingletonRandomMarket(n, m, p * 0.25);
          SingletonEVP singletonEVP = new SingletonEVP(market);
          PricesStatistics<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> stats = singletonEVP.Solve();
          assertEquals(stats.getEFViolationsRatio(), 0.0, 0.00000001);
        }
      }
    }
  }

}
