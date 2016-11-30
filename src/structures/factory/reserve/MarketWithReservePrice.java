package structures.factory.reserve;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.BidderCreationException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;

import com.google.common.collect.HashBasedTable;

/**
 * This class relates a given input market and reserve price r with a new
 * reserve price Market where bidders that can't afford the reserve price are no
 * longer part of this new market.
 * 
 * @author Enrique Areyan Viqueira
 */
abstract public class MarketWithReservePrice<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> {
  
  /**
   * Input Market.
   */
  protected final M market;  
  
  /**
   * Reserve Price.
   */
  protected final double reserve;
  
  /**
   * Map from input market bidders to market with reserve bidders.
   */
  protected final Map<B, Bidder<G>> bidderToBidderMap;
  
  /**
   * Market with reserve. This market is produced in the constructor.
   */
  protected M marketWithReserve;
  
  /**
   * Constructor.
   * 
   * @param market - the input market.
   * @param reserve - the reserve price.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws MarketCreationException 
   */
  public MarketWithReservePrice(M market, double reserve) throws BidderCreationException, MarketCreationException{
    this.market = market;
    this.reserve = reserve;
    this.bidderToBidderMap = new HashMap<B, Bidder<G>>();
    /**
     * Creates a clone market but subtract the rewards of all bidders by the
     * parameter r times number of demanded items. If the reward of a bidder
     * becomes 0 or negative under the reserve price, throw this bidder out of
     * the market. Call this market with reserve price r. Create a map from
     * bidders in the original market to bidders in the reserve market so that
     * we can later refer to one another.
     */
    for (B bidder : this.market.getBidders()) {
      double newReward = bidder.getReward() - this.reserve * bidder.getDemand();
      if (newReward > 0) {
        // Recreate the demand set of this bidder.
        HashSet<G> bDemandSet = new HashSet<G>();
        for(G good : this.market.getGoods()){
          if(bidder.demandsGood(good)){
            bDemandSet.add(good);
          }
        }
        Bidder<G> newBidder = new Bidder<G>(bidder.getDemand(), newReward , bDemandSet);
        this.bidderToBidderMap.put(bidder, newBidder);
      }
    }
    // If there are surviving bidders, create the new market. o/w the new market is null.
    this.marketWithReserve = (this.bidderToBidderMap.size() > 0) ? this.createMarketWithReserve() : null;
  }
  
  /**
   * Implementing classes should reconstruct the original market with surviving
   * bidders.
   * 
   * @return a market only with surviving bidders.
   * @throws MarketCreationException
   */
  abstract protected M createMarketWithReserve() throws MarketCreationException;

  /**
   * Getter.
   * 
   * @return the input market.
   */
  public M getMarket(){
    return this.market;
  }
  
  /**
   * Getter.
   * 
   * @return the market with reserve price.
   */
  public M getMarketWithReservePrice(){
    return this.marketWithReserve;
  }
  
  /**
   * Getter.
   * 
   * @return the bidder to bidder map.
   */
  public Map<B, Bidder<G>> getBidderToBidderMap(){
    return this.bidderToBidderMap;
  }
  
  /**
   * Getter.
   * 
   * @return the input reserve price.
   */
  public double getReservePrice(){
    return this.reserve;
  }
  
  /**
   * Getter. If there are no elements in the bidderToBidderMap,
   * then there are no surviving bidders.
   * 
   * @return false if all bidders were dropped by the reserve price.
   */
  public boolean areThereBiddersInTheMarketWithReserve(){
    return this.bidderToBidderMap.size() > 0;
  }
  
  
  /**
   * Given a MarketAllocation for the MarketWithReserve, deduce an allocation
   * for the original market from which the market with reserve was produced.
   * 
   * @param marketWithReserveObject
   * @param allocForMarketWithReserve
   * @return
   * @throws MarketAllocationException
   */
  public MarketAllocation<M, G, B> deduceAllocation(MarketAllocation<M, G, B> allocForMarketWithReserve) throws MarketAllocationException{
    
    HashBasedTable<G, B, Integer> deducedAllocation = HashBasedTable.create();
    for(G good : this.market.getGoods()){
      for(B bidder : this.market.getBidders()){
        if(this.bidderToBidderMap.containsKey(bidder)){
          deducedAllocation.put(good, bidder, allocForMarketWithReserve.getAllocation(good, bidder));
        }else{
          deducedAllocation.put(good, bidder, 0);
        }
      }
    }
    return new MarketAllocation<M, G, B>(this.market, deducedAllocation, allocForMarketWithReserve.getObjectiveFunction());
  }
}
