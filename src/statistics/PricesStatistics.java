package statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;

/**
 * This class implements functionality to compute
 * statistics from a market allocation object. 
 * Note that, at the moment, the functions implemented in this
 * class assume that the market outcome either completely allocated
 * a bidder or it did not.
 * 
 * @author Enrique Areyan Viqueira
 */
public class PricesStatistics <M extends Market<G, B>, G extends Goods, B extends Bidder<G>>{

  /**
   * MarketPrices object.
   */
  protected MarketOutcome<M, G, B> marketPrices;

  /**
   * epsilon parameter.
   */
  protected static double epsilon = 0.0;

  /**
   * Constructor.
   * 
   * @param marketPrices - a MarketPrices object.
   */
  public PricesStatistics(MarketOutcome<M, G, B> marketPrices) {
    this.marketPrices = marketPrices;
  }

  /**
   * This function takes a list of GoodPrices object - these are tuples (i,p_i)-
   * and a bidder and returns whether or not there exists a bundle that
   * is cheaper to the currently assigned bundle.
   * 
   * @param goodOrderedList - a list of UserPrices object.
   * @param bidder - a bidder object.
   * @return true if the bidder is envy at current prices.
   * @throws MarketAllocationException 
   * @throws MarketOutcomeException 
   */
  public boolean isBidderEnvyFree(ArrayList<GoodPrices> goodOrderedList, B bidder) throws MarketAllocationException, MarketOutcomeException {
    // System.out.println("Heuristic for bidder :" + bidder +
    // ", check this many users:" + userList.size());
    double costCheapestBundle = 0.0;
    int impressionsNeeded = bidder.getDemand();
    while (impressionsNeeded > 0 && goodOrderedList.size() != 0) {
      // Get first user from the list and remove it.
      G good = goodOrderedList.get(0).getGood();
      goodOrderedList.remove(0);
      int userSupply = good.getSupply();
      // If the bidder wants this good
      if (bidder.demandsGood(good)) {
        if (userSupply >= impressionsNeeded) { 
          // Take only as many users as you need.
          costCheapestBundle += impressionsNeeded * this.marketPrices.getPrice(good);
          impressionsNeeded = 0;
        } else { 
          // Greedily take all of the users.
          costCheapestBundle += userSupply * this.marketPrices.getPrice(good);
          impressionsNeeded -= userSupply;
        }
      }
    }
    if (impressionsNeeded > 0) {
      // If you cannot be satisfied, you are immediately envy-free
      return true;
    } else {
      /*
       * There are two cases in which a bidder can be envy: (1) This bidder was
       * satisfied but there exists a bundle in its demand set. (2) The campaign
       * was not satisfied and there exists a bundle in its demand set.
       */
      if (this.marketPrices.getMarketAllocation().allocationToBidder(bidder) >= bidder.getDemand()) {
        // Case (1)
        return (this.marketPrices.getBundleCost(bidder) - costCheapestBundle >= PricesStatistics.epsilon);
      } else {
        // Case (2)
        return (bidder.getReward() - costCheapestBundle < PricesStatistics.epsilon);
      }
    }
  }
  
  /**
   * Computes the number of bidders that have envy in this market.
   * 
   * @return the number of bidders that have envy in this market. 
   * @throws MarketOutcomeException 
   * @throws MarketAllocationException 
   */
  public int numberOfEnvyBidders() throws MarketOutcomeException, MarketAllocationException {
    // Construct a priority queue with users where the priority is price in ascending order.
    ArrayList<GoodPrices> listOfGoods = new ArrayList<GoodPrices>();
    for (G good : this.marketPrices.getMarketAllocation().getMarket().getGoods()) {
      listOfGoods.add(new GoodPrices(good, this.marketPrices.getPrice(good)));
    }
    Collections.sort(listOfGoods, new UserPriceComparator());
    // Check that each bidder is envy-free w.r.t the previously constructed queue.
    int counter = 0;
    for (B bidder : this.marketPrices.getMarketAllocation().getMarket().getBidders()) {
      // System.out.println("**** check if " + j + " is envy");
      if (!this.isBidderEnvyFree(new ArrayList<GoodPrices>(listOfGoods), bidder)) {
        // Pass a copy of the queue each time.
         System.out.println("\t --- > Bidder " + bidder + " is envy");
        counter++;
      }
    }
    return counter;
  }
  
  /**
   * This method computes violations of the Market Clearance (MC) conditions
   * A violation is one where an unallocated user has a price greater than zero.
   * 
   * @return a tuple of two numbers. The first is the number of MC violations, the s
   * second is the ratio of the prices MC violations to the total price of users.
   * @throws MarketOutcomeException 
   * @throws MarketAllocationException 
   */
  public double[] computeMarketClearanceViolations() throws MarketAllocationException, MarketOutcomeException {
    int violations = 0;
    double totalPricesOfUsers = 0.0;
    double totalPricesOfViolatingUsers = 0.0;
    for (G good : this.marketPrices.getMarketAllocation().getMarket().getGoods()) {
      totalPricesOfUsers += this.marketPrices.getPrice(good);
      if (this.marketPrices.getMarketAllocation().allocationFromGood(good) == 0 && this.marketPrices.getPrice(good) > 0) {
        violations++;
        totalPricesOfViolatingUsers += this.marketPrices.getPrice(good);
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
   * Auxiliary class that links goods to prices so we can order goods by prices.
   * 
   * @author Enrique Areyan Viqueira
   */
  class GoodPrices {
    /**
     * User index.
     */
    protected final G good;
    
    /**
     * User Price.
     */
    protected final double price;

    /**
     * Constructor.
     * 
     * @param good - user index.
     * @param price - user price.
     */
    public GoodPrices(G good, double price) {
      this.good = good;
      this.price = price;
    }

    /**
     * Getter.
     * @return user index.
     */
    public G getGood() {
      return this.good;
    }

    /**
     * Getter.
     * @return user price.
     */
    public double getPrice() {
      return this.price;
    }

    @Override
    public String toString() {
      return "(" + this.good + "," + this.price + ")";
    };
  }
  
  /**
   * Comparator to order users by prices.
   * @author Enrique Areyan Viqueira
   */
  public class UserPriceComparator implements Comparator<GoodPrices> {
    @Override
    public int compare(GoodPrices o1, GoodPrices o2) {
      //Order objects by ascending price
      return Double.compare(o1.getPrice(), o2.getPrice());
    }
  }
}
