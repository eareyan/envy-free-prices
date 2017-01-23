package test.factory;

import static org.junit.Assert.fail;

import org.junit.Test;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.factory.SingletonMarketFactory;

public class SingletonMarketFactoryTest {

  @Test
  public void testUniformRewardRandomMarket0() throws Exception {
    for (int i = 0; i < 1000; i++) {
      Market<Goods, Bidder<Goods>> singletonMarket = SingletonMarketFactory.uniformRewardRandomMarket(3, 2, 0.5);
      for (Bidder<Goods> b : singletonMarket.getBidders()) {
        if (b.getDemand() != 1) {
          fail("In a singleton market all bidders demand exactly one good.");
        }
      }
    }
  }

  @Test
  public void testUniformRewardRandomMarket1() throws Exception {
    for (int i = 0; i < 1000; i++) {
      Market<Goods, Bidder<Goods>> singletonMarket = SingletonMarketFactory.uniformRewardRandomMarket(3, 2, 0.5);
      for (Bidder<Goods> b : singletonMarket.getBidders()) {
        if (b.getDemand() != 1) {
          fail("In a singleton market all bidders demand exactly one good.");
        }
      }
    }
  }

}
