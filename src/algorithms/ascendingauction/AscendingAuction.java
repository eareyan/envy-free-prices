package algorithms.ascendingauction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;

/**
 * 
 * @author Enrique Areyan Viqueira
 */
public class AscendingAuction {
  
  /**
   * Market on which to run the ascending auction.
   */
  protected final Market<Goods, Bidder<Goods>> market;

  /**
   * Epsilon parameter by which prices are incremented.
   */
  protected final double epsilon = 0.0001;

  /**
   * Constructor.
   * 
   * @param market
   */
  public AscendingAuction(Market<Goods, Bidder<Goods>> market) {
    this.market = market;
  }

  /**
   * Solve for a market outcome.
   * @return 
   * @throws MarketAllocationException 
   * @throws MarketOutcomeException 
   */
  public MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> Solve() throws MarketAllocationException, MarketOutcomeException {
    // Initially, we have a zero allocation.
    HashBasedTable<Goods, Bidder<Goods>, Integer> allocation = HashBasedTable.create();
    for(Goods good : this.market.getGoods()){
      for(Bidder<Goods> bidder : this.market.getBidders()){
        allocation.put(good, bidder, 0);
      }
    }
    // Initially, all prices are zero.
    ArrayList<GoodPrice> goodPricesList = new ArrayList<GoodPrice>();
    for (Goods good : this.market.getGoods()) {
      goodPricesList.add(new GoodPrice(good, 0.0));
    }
    // Initially, all bidders are unallocated.
    HashSet<Bidder<Goods>> unallocatedBidders = new HashSet<Bidder<Goods>>();
    for (Bidder<Goods> bidder : this.market.getBidders()) {
      unallocatedBidders.add(bidder);
    }
    boolean continueAuction = true;
    // Keep the auction going until continueAuction is false.
    while (continueAuction) {
      // Try to find an unallocated bidder and a bundle she demands.
      HashMap<Goods, Integer> bundleToAllocate = null;
      Bidder<Goods> bidderToAllocate = null;
      for (Bidder<Goods> unallocatedBidder : unallocatedBidders) {
        bundleToAllocate = computeCheapestBundle(unallocatedBidder, goodPricesList);
        if (bundleToAllocate != null) {
          bidderToAllocate = unallocatedBidder;
          break;
        }
      }
      // Check if we have found a bundle to allocate to a currently unallocated bidder.
      if (bundleToAllocate != null) {
        // Allocate Bundle
        for(Entry<Goods, Integer> entry : bundleToAllocate.entrySet()) {
          allocation.put(entry.getKey(), bidderToAllocate, entry.getValue());
        }
        // Check if an item is overallocated.
        checkSupplyAndUnallocate(bidderToAllocate, allocation, unallocatedBidders, goodPricesList);
        // Take the bidder out of the unallocated map.
        unallocatedBidders.remove(bidderToAllocate);
        // Sort items by price.
        Collections.sort(goodPricesList, new GoodPriceComparatorByPrice());
        //System.out.println(goodPricesList);
      } else {
        // We have have no bundle to allocate, the auction stops.
        continueAuction = false;
      }
    }
    Builder<Goods, Double> result = ImmutableMap.<Goods, Double>builder();
    for(GoodPrice gp : goodPricesList){
      result.put(gp.good, gp.price);
    }
    return new MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(new MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(this.market, allocation, null), result.build());
  }
  
  /**
   * Check if a good is overallocated. If it is, unallocate all bidders that
   * have at least one from this good and increment the price of this good by
   * epsilon.
   * 
   * @param allocation
   * @param unallocatedBidders
   */
  private void checkSupplyAndUnallocate(Bidder<Goods> bidderToAllocate, HashBasedTable<Goods, Bidder<Goods>, Integer> allocation, HashSet<Bidder<Goods>> unallocatedBidders, ArrayList<GoodPrice> goodPricesList) {
    for(Goods good : this.market.getGoods()) {
      int totalAllocation = 0;
      for (Integer alloc : allocation.row(good).values()) {
        totalAllocation += alloc;
      }
      if(totalAllocation > good.getSupply()) {
        // Good is over allocated. Must unallocate all bidders (other than the current bidder) 
        // that currently have at least one from this good.
        for(Bidder<Goods> b : this.market.getBidders()) {
          if(b == bidderToAllocate) continue;
          if(allocation.get(good, b) > 0) {
            //Bidder b has at least one from good g. Must completely unallocate bidder b.
            for(Goods g : this.market.getGoods()) {
              allocation.put(g, b, 0);
            }
            // Mark the bidder as unallocated.
            unallocatedBidders.add(b);
          }
        }
        // Must increase the price of this good by epsilon.
        for(GoodPrice gp : goodPricesList) {
          if(gp.good == good) {
            gp.incrementePrice(this.epsilon);
          }
        }
      }
    }
  }
    
  /**
   * Given a bidder and a list of (Goods, Price), compute the cheapest bundle.
   * Assume the goods are sorted by ascending price (cheapest to more expensive).
   * 
   * @param bidder
   * @param goodPricesList
   * @return
   */
  private HashMap<Goods, Integer> computeCheapestBundle(Bidder<Goods> bidder, ArrayList<GoodPrice> goodPricesList) {
    HashMap<Goods, Integer> bundle = new HashMap<Goods, Integer>();
    int totalAllocation = 0;
    double totalPrice = 0.0;
    // For each good.
    for (GoodPrice g : goodPricesList) {
      // If the bidder demands the good.
      if (bidder.demandsGood(g.good)) {
        // Take as much as needed.
        int alloc = Math.min(bidder.getDemand() - totalAllocation, g.good.getSupply());
        bundle.put(g.good, alloc);
        totalAllocation += alloc;
        totalPrice += alloc * (g.price + this.epsilon);
        //totalPrice += alloc * g.price;
      }
    }
    // Check if the bundle we have constructed is of size the demand of the
    // bidder and if the bundle is not too expensive.
    //System.out.println(totalAllocation +"-" + bidder.getDemand() + "," + totalPrice + "-" + bidder.getReward());
    if (totalAllocation == bidder.getDemand() && totalPrice <= bidder.getReward()) {
      return bundle;
    } else {
      return null;
    }
  }
}
