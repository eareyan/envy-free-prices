package structures;

import java.util.ArrayList;

import structures.exceptions.MarketAllocationException;
import allocations.objectivefunction.ObjectiveFunction;

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
public class MarketAllocation {
  
  /**
   * Market that was allocated.
   */
  protected Market market;
  
  /**
   * Allocation for the market.
   */
  protected int[][] allocation;
  
  /**
   * Objective function. This is the function that the allocation
   * algorithm reported as having used to perfom its allocation.
   */
  protected ObjectiveFunction f;
  
  /**
   * Basic constructor. Takes a Market and an allocation.
   * @param m
   * @param allocation
   */
  public MarketAllocation(Market m, int[][] allocation) {
    this.market = m;
    this.allocation = allocation;
  }
  
  /**
   * Second constructor. Takes a Market, an allocation, and an objective function.
   * @param m
   * @param allocation
   * @param f
   */
  public MarketAllocation(Market m, int[][] allocation, ObjectiveFunction f) {
    this(m, allocation);
    this.f = f;
  }
  
  /**
   * Getter.
   * @return the Market object.
   */
  public Market getMarket() {
    return this.market;
  }

  /**
   * Getter.
   * @return the allocation matrix.
   */
  public int[][] getAllocation() {
    return this.allocation;
  }

  /**
   * Getter.
   * @param i - good index.
   * @param j - bidder index.
   * @return the allocation from good i to bidder j.
   */
  public int getAllocation(int i, int j) {
    return this.allocation[i][j];
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
    for (int j = 0; j < this.market.getNumberBidders(); j++) {
      // Compute the extra reward attained by bidder j under the current allocation.
      totalReward += this.marginalValue(j);
    }
    return totalReward;
  }
  
  /**
   * Computes the marginal value of the allocation for bidder j.
   * 
   * @param j - bidder index.
   * @return the marginal value of bidder j.
   * @throws MarketAllocationException
   */
  public double marginalValue(int j) throws MarketAllocationException {
    // Make sure we have an objective function to be able to compute the value of the allocation.
    if (this.f == null){
      throw new MarketAllocationException("An objective function must be defined to compute the value of an allocation");
    } else {
      return this.f.getObjective(this.market.bidders[j].getReward(), this.market.bidders[j].getDemand(), this.getBundleNumber(j) + this.market.bidders[j].getAllocationSoFar())
          - this.f.getObjective(this.market.bidders[j].getReward(), this.market.bidders[j].getDemand(), this.market.bidders[j].getAllocationSoFar());
    }
  }
  
  /**
   * Computes the sum of marginal values of the allocation 
   * for all bidder in the given input list.
   * 
   * @param biddersIndices - an ArrayList of bidder indices.
   * @return the sum of marginal values of bidders in biddersIndices
   * @throws MarketAllocationException in case an objective function is not defined.
   */
  public double marginalValue(ArrayList<Integer> biddersIndices) throws MarketAllocationException {
    double totalReward = 0.0;
    for (Integer j : biddersIndices) {
      totalReward += this.marginalValue(j);
    }
    return totalReward;
  }
  
  /**
   * Checks if a bidder is assigned something at all.
   * 
   * @param j - bidder index.
   * @return true if j was allocated at least one good.
   */
  public boolean isBidderBundleZero(int j) {
    for (int i = 0; i < this.market.getNumberGoods(); i++) {
      if (this.allocation[i][j] > 0) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Computes the number of items from good i that were allocated.
   * @param i - a good index.
   * @return the number of items from good i that were allocated.
   */
  public int allocationFromGood(int i) {
    int totalAllocation = 0;
    for (int j = 0; j < this.market.getNumberBidders(); j++) {
      totalAllocation += this.allocation[i][j];
    }
    return totalAllocation;
  }
  
  /**
   * Get current bundle number for bidder j.
   * @param j - a bidder index.
   * @return the number of goods allocated to j.
   */
  public int getBundleNumber(int j) {
    int totalAllocation = 0;
    for (int i = 0; i < this.market.getNumberGoods(); i++) {
      if (this.allocation[i][j] > 0) {
        totalAllocation += this.allocation[i][j];
      }
    }
    return totalAllocation;
  }
  
  /**
   * Updates an allocation.
   * @param i - a good index.
   * @param j - a bidder index.
   * @param alloc - the new allocation from i to j.
   */
  public void updateAllocationEntry(int i, int j, int alloc) {
    this.allocation[i][j] = alloc;
  }
  
}
