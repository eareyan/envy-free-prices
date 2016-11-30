package structures.factory.reserve;

import java.util.ArrayList;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.BidderCreationException;
import structures.exceptions.MarketCreationException;

/**
 * Basic market, creates a Market<Goods, Bidder<Goods>> with reserve.
 * 
 * @author Enrique Areyan Viqueira
 */
public class BasicMarketWithReserve extends MarketWithReservePrice<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> {

  /**
   * Constructor.
   * 
   * @param market
   * @param reserve
   * @throws BidderCreationException
   * @throws MarketCreationException
   */
  public BasicMarketWithReserve(Market<Goods, Bidder<Goods>> market, double reserve) throws BidderCreationException, MarketCreationException {
    super(market, reserve);
  }

  @Override
  protected Market<Goods, Bidder<Goods>> createMarketWithReserve() throws MarketCreationException {
    ArrayList<Bidder<Goods>> newBidders = new ArrayList<Bidder<Goods>>();
    for (Bidder<Goods> B : this.bidderToBidderMap.keySet()) {
      newBidders.add(B);
    }
    ArrayList<Goods> newGoods = new ArrayList<Goods>(this.market.getGoods());
    return new Market<Goods, Bidder<Goods>>(newGoods, newBidders);
  }

}
