package structures.factory;

import singleton.structures.SingletonMarket;
import structures.Bidder;
import structures.Goods;

/**
 * Factory methods to create singleton markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingletonMarketFactory {

  /**
   * Creates a uniform reward singleton random market.
   * 
   * @param numberGoods
   * @param numberBidders
   * @param probabilityConnection
   * @return
   * @throws Exception
   */
  public static SingletonMarket<Goods, Bidder<Goods>> uniformRewardSingletonRandomMarket(int numberGoods, int numberBidders, double probabilityConnection)
      throws Exception {
    return new SingletonMarket<Goods, Bidder<Goods>>(RandomMarketFactory.randomMarket(numberGoods, 1, 1, numberBidders, 1, 1,
        RewardsGenerator.getRandomUniformRewardFunction(), probabilityConnection));
  }

  /**
   * Creates an elitist reward singleton random market.
   * 
   * @param numberGoods
   * @param numberBidders
   * @param probabilityConnection
   * @return
   * @throws Exception
   */
  public static SingletonMarket<Goods, Bidder<Goods>> elitistRewardSingletonRandomMarket(int numberGoods, int numberBidders, double probabilityConnection)
      throws Exception {
    return new SingletonMarket<Goods, Bidder<Goods>>(RandomMarketFactory.randomMarket(numberGoods, 1, 1, numberBidders, 1, 1,
        RewardsGenerator.getElitistRewardFunction(), probabilityConnection));
  }

}
