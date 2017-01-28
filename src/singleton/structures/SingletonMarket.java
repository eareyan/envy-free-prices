package singleton.structures;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.MarketCreationException;

/**
 * Singleton markets.
 * 
 * @author Enrique Areyan Viqueira
 *
 * @param <G>
 * @param <B>
 */
public class SingletonMarket<G extends Goods, B extends Bidder<G>> extends Market<G, B> {

  /**
   * Constructor.
   * 
   * @param market
   * @throws MarketCreationException
   */
  public SingletonMarket(Market<G, B> market) throws MarketCreationException {
    super(market.getGoods(), market.getBidders());
    for (B bidder : this.bidders) {
      if (bidder.getDemand() != 1) {
        throw new MarketCreationException("In a singleton market, all bidders demand exactly one item.");
      }
    }
    for (G good : this.goods) {
      if (good.getSupply() != 1) {
        throw new MarketCreationException("In a singleton market, all goods must be in unit supply.");
      }
    }
  }

}
