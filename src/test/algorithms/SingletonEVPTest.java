package test.algorithms;

import org.junit.Test;

import singleton.algorithms.SingletonEVP;
import singleton.structures.SingletonMarket;
import structures.Bidder;
import structures.Goods;
import structures.factory.SingletonMarketFactory;

public class SingletonEVPTest {

  @Test
  public void testSolve() throws Exception {
    SingletonMarket<Goods, Bidder<Goods>> market = SingletonMarketFactory.uniformRewardSingletonRandomMarket(3, 2, 0.85);
    SingletonEVP singletonEVP = new SingletonEVP(market);
    singletonEVP.Solve();
  }

}
