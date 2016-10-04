package structures;

import java.util.ArrayList;
import java.util.Collection;

import structures.exceptions.MarketAllocationException;
import allocations.objectivefunction.ObjectiveFunction;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * This class associates a Market with an Allocation.
 * The idea is that an Allocation is an object associated 
 * but separate from a Market.
 * 
 * An allocation is the result of some allocation algorithm.
 * This class provides a common class for any allocation
 * algorithm to report its results.
 * 
 * @author Enrique Areyan Viqueira
 */
public class CopyOfMarketAllocation<G extends Goods, B extends Bidder<G>> {
  
  /**
   * Market that was allocated.
   */
  protected final Market<G,B> market;
  
  /**
   * Allocation for the market.
   */
  protected final ImmutableMultimap<G, Allocation<G, B>> allocGToB;

  protected final ImmutableMultimap<B, Allocation<G, B>> allocBToG;

  /**
   * Objective function. This is the function that the allocation
   * algorithm reported as having used to perform its allocation.
   */
  protected ObjectiveFunction f;
  
  /**
   * Basic constructor. Takes a Market and an allocation.
   * @param m
   * @param allocation
   * @throws MarketAllocationException 
   */
  public CopyOfMarketAllocation(Market<G, B> m, ArrayList<Allocation<G, B>> alloc) throws MarketAllocationException {
    this.market = m;
    if (this.market.bidders.size() * this.market.goods.size() != alloc.size()) {
      throw new MarketAllocationException(
          "Trying to construct a MarketAllocation object for a market with "
              + this.market.bidders.size() + " bidders and "
              + this.market.goods.size()
              + " goods, but with an allocation of size " + alloc.size());
    }
    // Populate the allocGToB and allocBToGmaps
    Multimap<G, Allocation<G,B>> mapGtoB = ArrayListMultimap.create();
    Multimap<B, Allocation<G,B>> mapBtoG = ArrayListMultimap.create();
    for(Allocation<G, B> x: alloc){
      mapGtoB.put(x.good, x);
      mapBtoG.put(x.bidder, x);
    }
    ImmutableMultimap.Builder<G, Allocation<G,B>> gToBBuilder = ImmutableMultimap.builder();
    gToBBuilder.putAll(mapGtoB);
    this.allocGToB = gToBBuilder.build();

    ImmutableMultimap.Builder<B, Allocation<G,B>> bToGBuilder = ImmutableMultimap.builder();
    bToGBuilder.putAll(mapBtoG);
    this.allocBToG = bToGBuilder.build();

  }
  
  /**
   * Second constructor. Takes a Market, an allocation, and an objective function.
   * @param m
   * @param allocation
   * @param f
   * @throws MarketAllocationException 
   */
  public CopyOfMarketAllocation(Market<G, B> m, ArrayList<Allocation<G, B>> alloc,  ObjectiveFunction f) throws MarketAllocationException {
    this(m, alloc);
    this.f = f;
  }
  
  /**
   * Getter.
   * @return the Market object.
   */
  public Market<G, B> getMarket() {
    return this.market;
  }

  /**
   * Getter.
   * 
   * @param good - a good object.
   * @param bidder -  a bidder object.
   * @return the allocation from good to bidder.
   * @throws MarketAllocationException 
   */
  public int getAllocation(G good, B bidder) throws MarketAllocationException {
    Collection<Allocation<G,B>> results = this.allocGToB.get(good);
    if (results.size() == 0) {
      throw new MarketAllocationException("Good not in allocation.");
    }
    for (Allocation<G,B> a : results) {
      if (a.bidder.equals(bidder)) {
        return a.allocation;
      }
    }
    throw new MarketAllocationException("Bidder not in allocation.");
  }
  
  /**
   * Get value of allocation. The value of an allocation is the sum of rewards
   * obtained by the allocation across all bidders. This value depends on the
   * objective function being used.
   * 
   * @return the value of an allocation, i.e., the sum of rewards of allocated bidders.
   * @throws MarketAllocationException in case there is a null allocation 
   */
  public double value() throws MarketAllocationException {
    double totalReward = 0.0;
    // Loop through each bidder to check if it is satisfied.
    for (B bidder : this.market.bidders) {
      // Compute the extra reward attained by the bidder under the current allocation.
      totalReward += this.marginalValue(bidder);
    }
    return totalReward;
  }
  
  /**
   * Computes the marginal value of the allocation for a bidder.
   * 
   * @param bidder - the bidder object.
   * @return the marginal value of the bidder.
   * @throws MarketAllocationException in case an objective function was not defined for this MarketAllocation object.
   */
  public double marginalValue(B bidder) throws MarketAllocationException {
    // Make sure we have an objective function to be able to compute the value of the allocation.
    if (this.f == null){
      throw new MarketAllocationException("An objective function must be defined to compute the value of an allocation");
    } else {
      return this.f.getObjective(bidder.getReward(), bidder.getDemand(), this.getBundleNumber(bidder))
           - this.f.getObjective(bidder.getReward(), bidder.getDemand(), 0);
    }
  }
  
  /**
   * Computes the sum of marginal values of the allocation 
   * for all bidders in the given input list.
   * 
   * @param biddersIndices - an ArrayList of bidder indices.
   * @return the sum of marginal values of bidders in biddersIndices
   * @throws MarketAllocationException in case an objective function is not defined.
   */
  public double marginalValue(ArrayList<B> bidders) throws MarketAllocationException {
    double totalReward = 0.0;
    for (B bidder : bidders) {
      totalReward += this.marginalValue(bidder);
    }
    return totalReward;
  }
  
  /**
   * Checks if a bidder is assigned something at all.
   * 
   * @param bidder - the bidder object.
   * @return true if bidder was allocated at least one good.
   * @throws MarketAllocationException in case the bidder is not found.
   */
  public boolean isBidderBundleZero(B bidder) throws MarketAllocationException {
    ImmutableCollection<Allocation<G, B>> x = this.allocBToG.get(bidder);
    if (x.size() == 0) {
      throw new MarketAllocationException("Bidder not found.");
    }
    for (Allocation<G, B> alloc : x) {
      if (alloc.allocation > 0) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Computes the number of items from good i that were allocated.
   * @param i - a good index.
   * @return the number of items from good i that were allocated.
   * @throws MarketAllocationException  in case the good is not found.
   */
  public int allocationFromGood(G good) throws MarketAllocationException {
    ImmutableCollection<Allocation<G, B>> x = this.allocGToB.get(good);
    if (x.size() == 0) {
      throw new MarketAllocationException("Good not found.");
    }
    int totalAllocation = 0;
    for (Allocation<G, B> alloc : x) {
      totalAllocation += alloc.allocation;
    }
    return totalAllocation;
  }
  
  /**
   * Get current bundle number for a bidder.
   * @param bidder - a bidder object.
   * @return the number of goods allocated to the bidder.
   * @throws MarketAllocationException in case a bidder is not found
   */
  public int getBundleNumber(B bidder) throws MarketAllocationException {
    ImmutableCollection<Allocation<G, B>> x = this.allocBToG.get(bidder);
    if(x.size() == 0){
      throw new MarketAllocationException("Bidder not found.");
    }
    int totalAllocation = 0;
    for (Allocation<G, B> alloc : x) {
      totalAllocation += alloc.allocation;
    }
    return totalAllocation;
  }
  
  /**
   * Helper method to print a matrix representation of the allocation.
   * 
   * @throws MarketAllocationException
   */
  public void printAllocation() throws MarketAllocationException {
    for (G good : this.market.goods) {
      for (B bidder : this.market.bidders) {
        System.out.print("\t " + this.getAllocation(good, bidder));
      }
      System.out.print("\n");
    }
  }
  
}
