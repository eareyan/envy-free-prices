package structures.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;

/**
 * Markets can be created in different ways.
 * This class implements methods to create Market Objects.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketFactory {
  
  /**
   * Clones a market. This method deep copies goods and bidders.
   * 
   * @param market - a market object.
   * @return a clone of market.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException in case a good could not be created.
   * @throws MarketCreationException 
   */
  public static Market<Goods, Bidder<Goods>> cloneMarket(Market<Goods, Bidder<Goods>> market) throws BidderCreationException, GoodsCreationException, MarketCreationException {
    // Deep Copy the ArrayList of Goods.
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (Goods good : market.getGoods()) {
      goods.add(new Goods(good.getSupply()));
    }
    // Deep Copy the ArrayList of Bidders
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (Bidder<Goods> bidder : market.getBidders()) {
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      for (Goods good : market.getGoods()) {
        if (bidder.demandsGood(good)) {
          bDemandSet.add(good);
        }
      }
      bidders.add(new Bidder<Goods>(bidder.getDemand(), bidder.getReward(), bDemandSet));
    }

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }

  /**
   * Transpose market, i.e., interchange goods for bidders. This means: a good
   * i with supply N_i becomes a bidder j with demand N_i and a new random
   * reward. A bidder j with demand I_j becomes a good i with supply I_j. The
   * connections are preserved, i.e. if (i,j) is connected in the original
   * market, then it will be in the transposed market. This is to say, if 
   * bidder j demands good i, then good j will be demanded by bidder i in the
   * new market.
   * 
   * @param market - a Market object
   * @return a market object with bidders and goods interchanged.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException in case a good could not be created.
   * @throws MarketCreationException 
   */
  public static Market<Goods, Bidder<Goods>> transposeMarket(Market<Goods, Bidder<Goods>> market) throws BidderCreationException, GoodsCreationException, MarketCreationException {
    Random generator = new Random();
    // For each bidder of the input market, create a good.
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (Bidder<Goods> bidder : market.getBidders()) {
      goods.add(new Goods(bidder.getDemand()));
    }
    // For each good of the input market, create a bidder.
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (int i = 0 ; i < market.getNumberGoods(); i++) {
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      // Compute the bidder demand set.
      for(int j = 0; j < market.getNumberBidders(); j ++){
        if(market.getBidders().get(j).demandsGood(market.getGoods().get(i))){
          bDemandSet.add(goods.get(j));
        }
      }
      bidders.add(new Bidder<Goods>(market.getGoods().get(i).getSupply(), generator.nextDouble() * (RandomMarketFactory.defaultMaxReward - RandomMarketFactory.defaultMinReward) + RandomMarketFactory.defaultMinReward , bDemandSet));
    }
    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }
}
