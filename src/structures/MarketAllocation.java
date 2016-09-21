package structures;

import java.util.ArrayList;

import structures.exceptions.MarketAllocationException;
import allocations.objectivefunction.ObjectiveFunction;

/**
 * This class associates a Market with an Allocation.
 * The idea is that an allocation is an object associated 
 * with a market and thus, it should be a separate from it.
 * 
 * Also, an allocation is the result of some algorithm.
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
   * Objective function.
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
   * @param i - user index.
   * @param j - campaign index.
   * @return the allocation from user i to campaign j.
   */
  public int getAllocation(int i, int j) {
    return this.allocation[i][j];
  }
  
  /**
   * Get value of allocation. The value of an allocation is the sum of rewards
   * obtained by the allocation across all campaigns. This value depends on the
   * objective function being used.
   * 
   * @return the value of an allocation, i.e., the sum of rewards of allocated campaigns.
   * @throws MarketAllocationException in case there is a null allocation 
   */
  public double value() throws MarketAllocationException {
    double totalReward = 0.0;
    // Loop through each campaign to check if it is satisfied.
    for (int j = 0; j < this.market.getNumberCampaigns(); j++) {
      // Compute the extra reward attained by campaign j under the current allocation.
      totalReward += this.marginalValue(j);
    }
    return totalReward;
  }
  
  /**
   * Computes the marginal value of the allocation for campaign j.
   * @param j - campaign index.
   * @return the marginal value of campaign j.
   * @throws MarketAllocationException
   */
  public double marginalValue(int j) throws MarketAllocationException {
    // Make sure we have an objective function to be able to compute the value of the allocation.
    if (this.f == null){
      throw new MarketAllocationException("An objective function must be defined to compute the value of an allocation");
    } else {
      return this.f.getObjective(this.market.campaigns[j].getReward(), this.market.campaigns[j].getDemand(), this.getBundleNumber(j) + this.market.campaigns[j].getAllocationSoFar())
          - this.f.getObjective(this.market.campaigns[j].getReward(), this.market.campaigns[j].getDemand(), this.market.campaigns[j].getAllocationSoFar());
    }
  }
  
  /**
   * Computes the sum of marginal values of the allocation 
   * for all campaigns in the given input list.
   * @param campaignIndices - an ArrayList of campaign indices.
   * @return the sum of marginal values of campaigns in campaignIndices
   * @throws MarketAllocationException in case an objective function is not defined.
   */
  public double marginalValue(ArrayList<Integer> campaignIndices) throws MarketAllocationException {
    double totalReward = 0.0;
    for (Integer j : campaignIndices) {
      totalReward += this.marginalValue(j);
    }
    return totalReward;
  }
  
  /**
   * Checks if a campaign is assign something at all.
   * @param j - campaign index.
   * @return true if j was allocated at least one user.
   */
  public boolean isCampaignBundleZero(int j) {
    for (int i = 0; i < this.market.getNumberUsers(); i++) {
      if (this.allocation[i][j] > 0) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Computes the number of impressions from user i that were allocated.
   * @param i - a user index.
   * @return the number of impressions from user i that were allocated.
   */
  public int allocationFromUser(int i) {
    int totalAllocation = 0;
    for (int j = 0; j < this.market.getNumberCampaigns(); j++) {
      totalAllocation += this.allocation[i][j];
    }
    return totalAllocation;
  }
  
  /**
   * Get current bundle number for campaign j
   * @param j - a campaign index.
   * @return the number of users allocated to j.
   */
  public int getBundleNumber(int j) {
    int totalAllocation = 0;
    for (int i = 0; i < this.market.getNumberUsers(); i++) {
      if (this.allocation[i][j] > 0) {
        totalAllocation += this.allocation[i][j];
      }
    }
    return totalAllocation;
  }
  
  /**
   * Updates an allocation.
   * @param i - a user index.
   * @param j - a campaign index.
   * @param alloc - the new allocation from i to j.
   */
  public void updateAllocationEntry(int i, int j, int alloc) {
    this.allocation[i][j] = alloc;
  }
  
}
