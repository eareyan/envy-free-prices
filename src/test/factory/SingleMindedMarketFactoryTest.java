package test.factory;

import org.junit.Test;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.factory.RewardsGenerator;
import structures.factory.SingleMindedMarketFactory;

public class SingleMindedMarketFactoryTest {

  @Test
  public void testRandomParametrizedSingleMindedMarket() throws Exception {
    SingleMindedMarket<Goods, Bidder<Goods>> singlemindedmarket = SingleMindedMarketFactory.createRandomParametrizedSingleMindedMarket(3, 3, 0.5, RewardsGenerator.getRandomUniformRewardFunction());
    System.out.println(singlemindedmarket);
  }


}
