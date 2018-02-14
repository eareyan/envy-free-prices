package test.factory;

import org.junit.Test;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.factory.SingleMindedMarketFactory;
import structures.rewardfunctions.UniformRewardFunction;

public class SingleMindedMarketFactoryTest {

  @Test
  public void testRandomParametrizedSingleMindedMarket() throws Exception {
    SingleMindedMarket<Goods, Bidder<Goods>> singlemindedmarket = SingleMindedMarketFactory.createRandomParametrizedSingleMindedMarket(3, 3, 0.5, UniformRewardFunction.singletonInstance);
    System.out.println(singlemindedmarket);
  }


}
