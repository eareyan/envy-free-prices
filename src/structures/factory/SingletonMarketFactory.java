package structures.factory;

import singleton.structures.SingletonMarket;
import structures.Bidder;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;
import structures.rewardfunctions.ElitistRewardFunction;
import structures.rewardfunctions.UniformRewardFunction;

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
   * @throws BidderCreationException
   * @throws MarketCreationException
   * @throws GoodsCreationException
   */
  public static SingletonMarket<Goods, Bidder<Goods>> uniformRewardSingletonRandomMarket(int numberGoods, int numberBidders, double probabilityConnection) throws GoodsCreationException, MarketCreationException, BidderCreationException {
    return new SingletonMarket<Goods, Bidder<Goods>>(RandomMarketFactory.randomMarket(numberGoods, 1, 1, numberBidders, 1, 1, UniformRewardFunction.singletonInstance, probabilityConnection));
  }

  /**
   * Creates an elitist reward singleton random market.
   * 
   * @param numberGoods
   * @param numberBidders
   * @param probabilityConnection
   * @return
   * @throws BidderCreationException
   * @throws MarketCreationException
   * @throws GoodsCreationException
   * @throws Exception
   */
  public static SingletonMarket<Goods, Bidder<Goods>> elitistRewardSingletonRandomMarket(int numberGoods, int numberBidders, double probabilityConnection) throws GoodsCreationException, MarketCreationException, BidderCreationException {
    return new SingletonMarket<Goods, Bidder<Goods>>(RandomMarketFactory.randomMarket(numberGoods, 1, 1, numberBidders, 1, 1, ElitistRewardFunction.singletonInstance, probabilityConnection));
  }

}
