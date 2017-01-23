package structures.factory;

import structures.Bidder;
import structures.Goods;
import structures.Market;

/**
 * Factory methods to create singleton markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingletonMarketFactory {

  /**
   * Creates a fully parameterizable singleton market.
   * 
   * @param numberGoods
   * @param numberBidders
   * @param minReward
   * @param maxReward
   * @param probabilityConnection
   * @return
   * @throws Exception
   */
  public static Market<Goods, Bidder<Goods>> uniformRewardRandomMarket(int numberGoods, int numberBidders, double probabilityConnection) throws Exception {
    return RandomMarketFactory.randomMarket(numberGoods, 1, 1, numberBidders, 1, 1, RewardsGenerator.getRandomUniformRewardFunction(), probabilityConnection);
  }
  
}
