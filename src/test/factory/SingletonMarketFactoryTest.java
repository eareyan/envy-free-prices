package test.factory;

import static org.junit.Assert.fail;

import org.junit.Test;

import singleton.structures.SingletonMarket;
import structures.Bidder;
import structures.Goods;
import structures.factory.SingletonMarketFactory;

public class SingletonMarketFactoryTest {

  @Test
  public void testUniformRewardSingletonRandomMarket() throws Exception {
    for (int n = 1; n < 15; n++) {
      for (int m = 1; m < 15; m++) {
        for (int p = 1; p <= 4; p++) {
          SingletonMarket<Goods, Bidder<Goods>> singletonMarket = SingletonMarketFactory.uniformRewardSingletonRandomMarket(n, m, p * 0.25);
          for (Bidder<Goods> b : singletonMarket.getBidders()) {
            if (b.getDemand() != 1) {
              fail("In a singleton market all bidders demand exactly one good.");
            }
          }
          for (Goods g : singletonMarket.getGoods()) {
            if (g.getSupply() != 1) {
              fail("In a singleton market all goods are in unit supply");
            }
          }
        }
      }
    }
  }

  @Test
  public void testElitistRewardSingletonRandomMarket() throws Exception {
    for (int n = 1; n < 15; n++) {
      for (int m = 1; m < 15; m++) {
        for (int p = 1; p <= 4; p++) {
          SingletonMarket<Goods, Bidder<Goods>> singletonMarket = SingletonMarketFactory.uniformRewardSingletonRandomMarket(n, m, p * 0.25);
          for (Bidder<Goods> b : singletonMarket.getBidders()) {
            if (b.getDemand() != 1) {
              fail("In a singleton market all bidders demand exactly one good.");
            }
          }
          for (Goods g : singletonMarket.getGoods()) {
            if (g.getSupply() != 1) {
              fail("In a singleton market all goods are in unit supply");
            }
          }
        }
      }
    }
  }
}
