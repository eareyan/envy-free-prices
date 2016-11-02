package algorithms.pricing.reserveprices;

import java.util.ArrayList;
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
public class MarketWithReservePrice {
  
  /**
   * Input Market.
   */
  protected final Market<Goods, Bidder<Goods>> market;  
  
  /**
   * Reserve Price.
   */
  protected final double reserve;
  
  /**
   * Are there any bidders in the market with reserve?
   */
  protected final boolean biddersInMarketWithReserve;
  
  /**
   * Map from input market bidders to market with reserve bidders.
   */
  protected final Map<Bidder<Goods>,Bidder<Goods>> bidderToBidderMap;
  
  /**
   * Market with reserve. This market is produced in the constructor.
   */
  protected final Market<Goods, Bidder<Goods>> marketWithReserve;
  
  /**
   * Constructor.
   * 
   * @param market - the input market.
   * @param reserve - the reseve price.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws MarketCreationException 
   */
  public MarketWithReservePrice(Market<Goods, Bidder<Goods>> market, double reserve) throws BidderCreationException, MarketCreationException{
    this.market = market;
    this.reserve = reserve;
    this.bidderToBidderMap = new HashMap<Bidder<Goods>,Bidder<Goods>>();
    /*
     * Creates a clone market but subtract the rewards of all bidders by the
     * parameter r times number of demanded items. If the reward of a bidder
     * becomes 0 or negative under the reserve price, throw this bidder out of
     * the market. Call this market with reserve price r. Create a map from
     * bidders in the original market to bidders in the reserve market so that
     * we can later refer to one another.
     */
    // Goods are just a copy of the same goods as the original market.
    ArrayList<Goods> newGoods = new ArrayList<Goods>(this.market.getGoods());
    // Bidders might change...
    ArrayList<Bidder<Goods>> newBidders = new ArrayList<Bidder<Goods>>();
    for (Bidder<Goods> bidder : this.market.getBidders()) {
      double newReward = bidder.getReward() - this.reserve * bidder.getDemand();
      if (newReward > 0) {
        HashSet<Goods> bDemandSet = new HashSet<Goods>();
        for(Goods good : this.market.getGoods()){
          if(bidder.demandsGood(good)){
            bDemandSet.add(good);
          }
        }
        Bidder<Goods> newBidder = new Bidder<Goods>(bidder.getDemand(), newReward , bDemandSet);
        this.bidderToBidderMap.put(bidder, newBidder);
        newBidders.add(newBidder);
      }
    }
    // There could be no surviving bidder. In that case we create a null market.
    if (newBidders.size() > 0) {
      this.marketWithReserve = new Market<Goods, Bidder<Goods>>(newGoods, newBidders);
      this.biddersInMarketWithReserve = true;
    } else {
      this.marketWithReserve = null;
      this.biddersInMarketWithReserve = false;
    }
  }
  
  /**
   * Getter.
   * 
   * @return the input market.
   */
  public Market<Goods, Bidder<Goods>> getMarket(){
    return this.market;
  }
  
  /**
   * Getter.
   * 
   * @return the market with reserve price.
   */
  public Market<Goods, Bidder<Goods>> getMarketWithReservePrice(){
    return this.marketWithReserve;
  }
  
  /**
   * Getter.
   * 
   * @return the bidder to bidder map.
   */
  public Map<Bidder<Goods>,Bidder<Goods>> getBidderToBidderMap(){
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
   * Getter.
   * 
   * @return false if all bidders were dropped by the reserve price.
   */
  public boolean thereBiddersInTheMarketWithReserve(){
    return this.biddersInMarketWithReserve;
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
  public MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> deduceAllocation(MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocForMarketWithReserve) throws MarketAllocationException{
    
    HashBasedTable<Goods,Bidder<Goods>,Integer> deducedAllocation = HashBasedTable.create();
    for(Goods good : this.market.getGoods()){
      for(Bidder<Goods> bidder : this.market.getBidders()){
        if(this.bidderToBidderMap.containsKey(bidder)){
          deducedAllocation.put(good, bidder, allocForMarketWithReserve.getAllocation(good, this.bidderToBidderMap.get(bidder)));
        }else{
          deducedAllocation.put(good, bidder, 0);
        }
      }
    }
    return new MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(this.market, deducedAllocation, allocForMarketWithReserve.getObjectiveFunction());
  }
}
