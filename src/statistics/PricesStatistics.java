package statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.MarketAllocation;
import structures.MarketPrices;

/**
 * This class implements functionality to compute
 * statistics from a market allocation object. 
 * @author Enrique Areyan Viqueira
 */
public class PricesStatistics {

  /**
   * MarketPrices object.
   */
  protected MarketPrices marketPrices;

  /**
   * MarketAllocation object.
   */
  protected MarketAllocation marketAllocation;

  /**
   * PricesVector.
   */
  protected double[] pricesVector;

  /**
   * epsilon parameter.
   */
  protected static double epsilon = 0.0;

  /**
   * Constructor.
   * @param marketPrices - a MarketPrices object.
   */
  public PricesStatistics(MarketPrices marketPrices) {
    this.marketPrices = marketPrices;
    this.marketAllocation = this.marketPrices.getMarketAllocation();
    this.pricesVector = this.marketPrices.getPriceVector();
  }

  /**
   * This function takes a list of UserPrice object -these are tuples (i,p_i)-
   * and a campaign index and returns whether or not there exists a bundle that
   * is cheaper to the currently assigned bundle.
   * @param userList - a list of UserPrices object.
   * @param campaignIndex - a campaign index.
   * @return true if the campaign is envy at current prices.
   */
  public boolean isCampaignEnvyFree(ArrayList<UserPrices> userList, int campaignIndex) {
    // System.out.println("Heuristic for campaign:" + campaignIndex +
    // ", check this many users:" + userList.size());
    double costCheapestBundle = 0.0;
    int impressionsNeeded = this.marketAllocation.getMarket().getCampaign(campaignIndex).getDemand();
    while (impressionsNeeded > 0 && userList.size() != 0) {
      // Get first user from the list and remove it.
      UserPrices userPriceObject = userList.get(0);
      userList.remove(0);
      int userIndex = userPriceObject.getUserIndex(), userSupply = this.marketAllocation.getMarket().getUser(userIndex).getSupply();
      // If the campaign wants this user
      if (this.marketAllocation.getMarket().isConnected(userIndex,campaignIndex)) {
        if (userSupply >= impressionsNeeded) { 
          // Take only as many users as you need.
          costCheapestBundle += impressionsNeeded * this.pricesVector[userIndex];
          impressionsNeeded = 0;
        } else { 
          // Greedily take all of the users.
          costCheapestBundle += userSupply * this.pricesVector[userIndex];
          impressionsNeeded -= userSupply;
        }
      }
    }
    if (impressionsNeeded > 0) {
      // If you cannot be satisfied, you are immediately envy-free
      return true;
    } else {
      /*
       * System.out.println("\tcost of cheapest bundle = "+costCheapestBundle);
       * System
       * .out.println("\tcost of current  bundle = "+this.marketPrices.getBundleCost
       * (campaignIndex));
       * System.out.println("\tnbr  of current  bundle = "+this
       * .marketAllocation.getBundleNumber(campaignIndex));
       * System.out.println("\treward of this campaign = "
       * +this.marketAllocation.
       * getMarket().getCampaign(campaignIndex).getReward());
       * System.out.println("\timpressionsNeeded = " + impressionsNeeded);
       */
      /*
       * There are two cases in which a campaign can be envy: (1) This campaign
       * was satisfied but there exists a cheaper bundle. (2) The campaign was
       * not satisfied and there exists a bundle in its demand set.
       */
      if (this.marketAllocation.getBundleNumber(campaignIndex) >= this.marketAllocation.getMarket().getCampaign(campaignIndex).getDemand()) {
        // Case (1)
        return (this.marketPrices.getBundleCost(campaignIndex) - costCheapestBundle >= PricesStatistics.epsilon);
      } else {
        // Case (2)
        return (this.marketAllocation.getMarket().getCampaign(campaignIndex).getReward() - costCheapestBundle < PricesStatistics.epsilon);
      }
    }
  }
  
  /**
   * Computes the number of campaigns that have envy in this market.
   * @return the number of campaigns that have envy in this market. 
   */
  public int numberOfEnvyCampaigns() {
    // System.out.println("Check Heuristic For Envy-free-ness");
    // Construct a priority queue with users where the priority is price in ascending order.
    int numUsers = this.marketAllocation.getMarket().getNumberUsers();
    ArrayList<UserPrices> listOfUsers = new ArrayList<UserPrices>();
    for (int i = 0; i < numUsers; i++) {
      listOfUsers.add(new UserPrices(i, this.pricesVector[i]));
    }
    Collections.sort(listOfUsers, new UserPriceComparator());
    // Check that each campaign is envy-free w.r.t the previously constructed queue.
    int counter = 0;
    for (int j = 0; j < this.marketAllocation.getMarket().getNumberCampaigns(); j++) {
      // System.out.println("**** check if " + j + " is envy");
      if (!this.isCampaignEnvyFree(new ArrayList<UserPrices>(listOfUsers), j)) {
        // Pass a copy of the queue each time.
        // System.out.println("\t --- > Campaign " + j + " is envy");
        counter++;
      }
    }
    return counter;
  }
  
  /**
   * This method computes violations of the Market Clearance (MC) conditions
   * A violation is one where an unallocated user has a price greater than zero.
   * @return a tuple of two numbers. The first is the number of MC violations, the s
   * second is the ratio of the prices MC violations to the total price of users.
   */
  public double[] computeMarketClearanceViolations() {
    int violations = 0;
    double totalPricesOfUsers = 0.0;
    double totalPricesOfViolatingUsers = 0.0;
    for (int i = 0; i < this.marketAllocation.getMarket().getNumberUsers(); i++) {
      totalPricesOfUsers += this.pricesVector[i];
      if (this.marketAllocation.allocationFromUser(i) == 0 && this.pricesVector[i] > 0) {
        violations++;
        totalPricesOfViolatingUsers += this.pricesVector[i];
      }
    }
    if (totalPricesOfUsers == 0) { // If the price of all users is zero, then
                                   // the ratio should be zero
      return new double[] { violations, 0.0 };
    } else {
      return new double[] { violations, totalPricesOfViolatingUsers / totalPricesOfUsers };
    }
  }
  
  /**
   * Auxiliary class that links users to prices so we can order users by prices.
   * @author Enrique Areyan Viqueira
   */
  class UserPrices {
    /**
     * User index.
     */
    protected int userIndex;
    
    /**
     * User Price.
     */
    protected double userPrice;

    /**
     * Constructor.
     * @param userIndex - user index.
     * @param userPrice - user price.
     */
    public UserPrices(int userIndex, double userPrice) {
      this.userIndex = userIndex;
      this.userPrice = userPrice;
    }

    /**
     * Getter.
     * @return user index.
     */
    public int getUserIndex() {
      return this.userIndex;
    }

    /**
     * Getter.
     * @return user price.
     */
    public double getPrice() {
      return this.userPrice;
    }

    @Override
    public String toString() {
      return "(" + this.userIndex + "," + this.userPrice + ")";
    };
  }
  
  /**
   * Comparator to order users by prices.
   * @author Enrique Areyan Viqueira
   */
  public class UserPriceComparator implements Comparator<UserPrices> {
    @Override
    public int compare(UserPrices o1, UserPrices o2) {
      //Order objects by ascending price
      return Double.compare(o1.getPrice(), o2.getPrice());
    }
  }
}
