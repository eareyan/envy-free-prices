package structures;


import java.util.ArrayList;
import java.util.HashSet;

import structures.exceptions.MarketAllocationException;
import allocations.objectivefunction.interfaces.ObjectiveFunction;

import com.google.common.collect.Table;

/**
 * This class associates a Market with an Allocation. The idea is that an Allocation is an object associated but separate from a Market.
 * 
 * An allocation is the result of some allocation algorithm. This class provides a common class for any allocation algorithm to report its results.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketAllocation<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> {

  /**
   * Market that was allocated.
   */
  protected final M market;

  /**
   * Allocation for the market. An allocation is a Table of goods, bidders and an integer denoting the allocation from a good to a bidder.
   */
  protected final Table<G, B, Integer> allocation;

  /**
   * Objective function. This is the function that the allocation algorithm reported as having used to perform its allocation.
   */
  protected final ObjectiveFunction f;

  /**
   * Value of the allocation.
   */
  protected double value = -1.0;

  /**
   * Number of winners.
   */
  protected int numberOfWinners = -1;
  
  /**
   * Set of winners.
   */
  protected HashSet<B> winners;

  /**
   * In case an allocation algorithm produces multiple allocations.
   */
  protected ArrayList<MarketAllocation<M, G, B>> extraAllocations;

  /**
   * Constructor. Takes a Market and an allocation.
   * 
   * @param m
   * @param allocation
   * @throws MarketAllocationException
   */
  public MarketAllocation(M m, Table<G, B, Integer> allocation, ObjectiveFunction f) throws MarketAllocationException {
    this.market = m;
    if (this.market.bidders.size() * this.market.goods.size() != allocation.size()) {
      throw new MarketAllocationException("Trying to construct a MarketAllocation object for a market with " + this.market.bidders.size() + " bidders and "
          + this.market.goods.size() + " goods, but with an allocation of size " + allocation.size());
    }
    this.allocation = allocation;
    this.f = f;
    this.extraAllocations = new ArrayList<MarketAllocation<M, G, B>>();
  }

  /**
   * Getter.
   * 
   * @return the Market object.
   */
  public Market<G, B> getMarket() {
    return this.market;
  }

  /**
   * Getter.
   * 
   * @param good - a good object.
   * @param bidder - a bidder object.
   * @return the allocation from good to bidder.
   * @throws MarketAllocationException
   */
  public int getAllocation(G good, B bidder) throws MarketAllocationException {
    if (!this.allocation.contains(good, bidder)) {
      throw new MarketAllocationException("(Good,Bidder) pair not in allocation.");
    }
    return this.allocation.get(good, bidder);
  }

  /**
   * Get value of allocation. The value of an allocation is the sum of rewards obtained by the allocation across all bidders. This value depends on the
   * objective function being used.
   * 
   * @return the value of an allocation, i.e., the sum of rewards of allocated bidders.
   * @throws MarketAllocationException in case there is a null allocation
   */
  public double getValue() throws MarketAllocationException {
    if (this.value == -1.0) {
      double total = 0.0;
      // Loop through each bidder to check if it is satisfied.
      for (B bidder : this.market.bidders) {
        // Compute the extra reward attained by the bidder under the current allocation.
        total += this.marginalValue(bidder);
      }
      this.value = total;
    }
    return this.value;
  }

  /**
   * Computes the marginal value of the allocation for a bidder.
   * 
   * @param bidder - the bidder object.
   * @return the marginal value of the bidder.
   * @throws MarketAllocationException
   *           in case an objective function was not defined for this MarketAllocation object.
   */
  public double marginalValue(B bidder) throws MarketAllocationException {
    // Make sure we have an objective function to be able to compute the value of the allocation.
    if (this.f == null) {
      throw new MarketAllocationException("An objective function must be defined to compute the value of an allocation");
    } else {
      return this.f.getObjective(bidder.getReward(), bidder.getDemand(), this.allocationToBidder(bidder))
          - this.f.getObjective(bidder.getReward(), bidder.getDemand(), 0);
    }
  }

  /**
   * Checks if a bidder is assigned something at all.
   * 
   * @param bidder - the bidder object.
   * @return true if bidder was allocated at least one good.
   * @throws MarketAllocationException
   *           in case the bidder is not found.
   */
  public boolean isBidderBundleZero(B bidder) throws MarketAllocationException {
    if (!this.allocation.containsColumn(bidder)) {
      throw new MarketAllocationException("Bidder not found.");
    }
    for (Integer alloc : this.allocation.column(bidder).values()) {
      if (alloc > 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Computes the number of items from good i that were allocated.
   * 
   * @param i - a good index.
   * @return the number of items from good i that were allocated.
   * @throws MarketAllocationException
   *           in case the good is not found.
   */
  public int allocationFromGood(G good) throws MarketAllocationException {
    if (!this.allocation.containsRow(good)) {
      throw new MarketAllocationException("Good not found.");
    }
    int totalAllocation = 0;
    for (Integer alloc : this.allocation.row(good).values()) {
      totalAllocation += alloc;
    }
    return totalAllocation;
  }

  /**
   * Get current bundle number for a bidder.
   * 
   * @param bidder - a bidder object.
   * @return the number of goods allocated to the bidder.
   * @throws MarketAllocationException in case a bidder is not found
   */
  public int allocationToBidder(B bidder) throws MarketAllocationException {
    if (!this.allocation.containsColumn(bidder)) {
      throw new MarketAllocationException("Bidder not found.");
    }
    int totalAllocation = 0;
    for (Integer alloc : this.allocation.column(bidder).values()) {
      totalAllocation += alloc;
    }
    return totalAllocation;
  }

  /**
   * Computes the number of bidders that received at least one item.
   * 
   * @return the number of bidders that received at least one item;
   * @throws MarketAllocationException
   */
  public int getNumberOfWinners() throws MarketAllocationException {
    if (this.numberOfWinners == -1) {
      int total = 0;
      for (B bidder : this.market.getBidders()) {
        if (!this.isBidderBundleZero(bidder)) {
          total++;
        }
      }
      this.numberOfWinners = total;
    }
    return this.numberOfWinners;
  }
  
  /**
   * Computes the set of winners.
   * 
   * @return the set of winners
   * @throws MarketAllocationException
   */
  public HashSet<B> getWinnerSet() throws MarketAllocationException {
    if(this.winners == null) {
      this.winners = new HashSet<B>();
      for(B bidder: this.market.getBidders()) {
        if(!this.isBidderBundleZero(bidder)) {
          this.winners.add(bidder);
        }
      }
    }
    return this.winners;
  }

  /**
   * Add an allocation.
   * 
   * @param extraAllocation
   * @throws MarketAllocationException
   */
  public void addAllocation(MarketAllocation<M, G, B> extraAllocation) throws MarketAllocationException {
    if (extraAllocation.market != this.market) {
      throw new MarketAllocationException("The extra allocation must refer to the same market object.");
    }
    this.extraAllocations.add(extraAllocation);
  }

  /**
   * Get the number of allocations.
   * 
   * @return an integer with the number of allocations.
   */
  public int getNumExtraAllocations() {
    return this.extraAllocations.size();
  }
  
  /**
   * Get the extra allocations.
   * 
   * @return an array list with all extra allocations.
   */
  public ArrayList<MarketAllocation<M, G, B>> getExtraAllocations() {
    return this.extraAllocations;
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

  /**
   * Getter.
   * 
   * @return the objective function.
   */
  public ObjectiveFunction getObjectiveFunction() {
    return this.f;
  }

}
