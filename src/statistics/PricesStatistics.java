package statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.commons.math3.util.Pair;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;
import util.NumberMethods;

import com.google.common.collect.ImmutableList;

/**
 * This class implements functionality to compute statistics from a market
 * allocation object. Note that, at the moment, the functions implemented in
 * this class assume that the market outcome either completely allocated a
 * bidder or it did not.
 * 
 * @author Enrique Areyan Viqueira
 */
public class PricesStatistics<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> {

  /**
   * MarketPrices object.
   */
  private final MarketOutcome<M, G, B> marketOutcome;
  
  /**
   * Time taken to compute the outcome.
   */
  private long time = -1;

  /**
   * epsilon parameter. This class computes envy-freeness violations up to
   * epsilon, to account for numerical error.
   */
  private static final double epsilon = 0.01;

  /**
   * Prints information if true.
   */
  private static final boolean debug = false;

  /**
   * An immutable list of envy bidders. Implements singleton.
   */
  private ImmutableList<B> listOfEnvyBidders;
  
  /**
   * Map from a bidder B to a tuple <Integer,Double> where the integer denotes
   * the number of impressions needed to satisfy the bidder (0 if the bidder is
   * satisfied under the allocation), and double is the cost of the cheapest
   * bundle available.
   */
  private HashMap<B, Pair<Integer, Double>> envynessMeasures;

  /**
   * A tuple containing 2 elements. The first element is the number of MC
   * violations. The second element in the proportion of total price of
   * violating MC prices to total prices. Implements singleton.
   */
  protected Pair<Integer, Double> marketClearanceViolations;

  /**
   * Constructor.
   * 
   * @param marketPrices - a MarketPrices object.
   */
  public PricesStatistics(MarketOutcome<M, G, B> marketOutcome) {
    this.marketOutcome = marketOutcome;
  }
  
  /**
   * Constructor.
   * 
   * @param marketPrices - a MarketPrices object.
   * @param time - taken to compute the result
   */
  public PricesStatistics(MarketOutcome<M, G, B> marketOutcome, long time) {
    this.marketOutcome = marketOutcome;
    this.time = time;
  }

  /**
   * Returns a list with the bidders that are envy.
   * 
   * @return
   */
  public ImmutableList<B> listOfEnvyBidders() {
    return this.listOfEnvyBidders;
  }

  /**
   * This function takes a list of ordered GoodPrices and computes two things:
   * (1) the impressions needed to satisfy a bidder and (2) the cost of the
   * cheapest available bundle.
   * 
   * @param goodOrderedList
   * @param bidder
   * @return a pair with envyness measures: (1) impressions needed and (2) cost of cheapest bundle
   * @throws MarketOutcomeException
   */
  private Pair<Integer, Double> envynessMeasures(ArrayList<GoodPrices> goodOrderedList, B bidder) throws MarketOutcomeException {
    if (!this.envynessMeasures.containsKey(bidder)) {
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
            costCheapestBundle += impressionsNeeded * this.marketOutcome.getPrice(good);
            impressionsNeeded = 0;
          } else {
            // Greedily take all of the users.
            costCheapestBundle += userSupply * this.marketOutcome.getPrice(good);
            impressionsNeeded -= userSupply;
          }
        }
      }
      this.envynessMeasures.put(bidder, new Pair<Integer, Double>(impressionsNeeded, costCheapestBundle));
    }
    return this.envynessMeasures.get(bidder);
  }
  
  /**
   * This function takes a list of GoodPrices object - these are tuples (i,p_i)-
   * and a bidder and returns whether or not there exists a bundle that is
   * cheaper to the currently assigned bundle.
   * 
   * @param goodOrderedList - a list of UserPrices object.
   * @param bidder - a bidder object.
   * @return true if the bidder is envy at current prices.
   * @throws MarketAllocationException
   * @throws MarketOutcomeException
   */
  private boolean isBidderEnvyFree(ArrayList<GoodPrices> goodOrderedList, B bidder) throws MarketAllocationException, MarketOutcomeException {
    Pair<Integer, Double> envynessMeasures = this.envynessMeasures(goodOrderedList, bidder);
    int impressionsNeeded = envynessMeasures.getKey();
    double costCheapestBundle = envynessMeasures.getValue();
    if (impressionsNeeded > 0) {
      // If you cannot be satisfied, you are immediately envy-free
      return true;
    } else {
      /*
       * There are two cases in which a bidder can be envy: (1) This bidder was
       * satisfied but there exists a cheaper bundle than the one it got, and
       * (2) The campaign received nothing and there exists a bundle in its
       * demand set.
       */
      if (this.marketOutcome.getMarketAllocation().allocationToBidder(bidder) >= bidder.getDemand()) {
        // Case (1)
        if (PricesStatistics.debug) {
          System.out.println("Case 1:" + this.marketOutcome.getBundleCost(bidder) + "," + costCheapestBundle);
          System.out.println("\t" + (this.marketOutcome.getBundleCost(bidder) - costCheapestBundle));
        }
        return (this.marketOutcome.getBundleCost(bidder) - costCheapestBundle <= PricesStatistics.epsilon);
      } else {
        // Case (2)
        if (PricesStatistics.debug) {
          System.out.println("Case 2: " + bidder.getReward() + "," + costCheapestBundle);
          System.out.println("\t" + (bidder.getReward() - costCheapestBundle));
        }
        return (bidder.getReward() - costCheapestBundle <= PricesStatistics.epsilon);
      }
    }
  }

  /**
   * Computes the number of bidders that have envy in this market. Implements
   * singleton for the listOfEnvyBidders.
   * 
   * @return the number of bidders that have envy in this market.
   * @throws MarketOutcomeException
   * @throws MarketAllocationException
   */
  public int numberOfEnvyBidders() throws MarketOutcomeException, MarketAllocationException {
    if (this.listOfEnvyBidders == null) {
      this.envynessMeasures = new HashMap<B, Pair<Integer,Double>>();
      // Initialize the list of envy-bidders.
      ImmutableList.Builder<B> listOfEnvyBiddersBuilder = ImmutableList.builder();
      // Construct a list of goods.
      ArrayList<GoodPrices> listOfGoods = new ArrayList<GoodPrices>();
      for (G good : this.marketOutcome.getMarketAllocation().getMarket().getGoods()) {
        listOfGoods.add(new GoodPrices(good, this.marketOutcome.getPrice(good)));
      }
      // Sort the list by ascending order of price.
      Collections.sort(listOfGoods, new UserPriceComparator());
      // Check that each bidder is envy-free w.r.t the previously constructed list.
      for (B bidder : this.marketOutcome.getMarketAllocation().getMarket().getBidders()) {
        // System.out.println("**** check if " + j + " is envy");
        if (!this.isBidderEnvyFree(new ArrayList<GoodPrices>(listOfGoods), bidder)) {
          // Pass a copy of the goods' list each time.
          if (PricesStatistics.debug) {
            System.out.println("\t --- > Bidder " + bidder + " is envy");
          }
          listOfEnvyBiddersBuilder.add(bidder);
        }
      }
      this.listOfEnvyBidders = listOfEnvyBiddersBuilder.build();
    }
    return this.listOfEnvyBidders.size();
  }
  
  /**
   * Computes a measure of loss utility defined as
   * (\sum_{j\notinW} (R_j - P_j)) / \sum_{j\notinW} R_j, in words,
   * the ratio of lost utility to total utility among losers.
   *  
   * @return Ratio of lost utility.
   * @throws MarketAllocationException
   * @throws MarketOutcomeException 
   */
  public double getRatioLossUtility() throws MarketAllocationException, MarketOutcomeException {
    this.numberOfEnvyBidders();
    //System.out.println("*************** computing ratio "+this.listOfEnvyBidders().size()+" ****************");
    double numerator = 0.0;
    double denominator = 0.0;
    if(this.listOfEnvyBidders.size() == 0) {
      return 0.0;
    }
    for(B bidder : this.listOfEnvyBidders) {
      //System.out.println("---> \t " + bidder + " , " + this.envynessMeasures.get(bidder).getKey() + "," + this.envynessMeasures.get(bidder).getValue());
      //System.out.println("\t\t " + (bidder.getReward() - this.envynessMeasures.get(bidder).getValue()));
      numerator += bidder.getReward() - this.envynessMeasures.get(bidder).getValue();
      denominator += bidder.getReward();
    }
    return numerator / denominator;
  }

  /**
   * This method computes violations of the Market Clearance (MC) conditions A
   * violation is one where an unallocated user has a price greater than zero.
   * This class implements singleton.
   * 
   * @return a tuple of two numbers. The first is the number of MC violations,
   *         the s second is the ratio of the prices MC violations to the total
   *         price of users.
   * @throws MarketOutcomeException
   * @throws MarketAllocationException
   */
  public Pair<Integer, Double> getMarketClearanceViolations() throws MarketAllocationException, MarketOutcomeException {
    if (this.marketClearanceViolations == null) {
      int violations = 0;
      double totalPricesOfUsers = 0.0;
      double totalPricesOfViolatingUsers = 0.0;
      for (G good : this.marketOutcome.getMarketAllocation().getMarket().getGoods()) {
        totalPricesOfUsers += this.marketOutcome.getPrice(good);
        if (this.marketOutcome.getMarketAllocation().allocationFromGood(good) == 0 && this.marketOutcome.getPrice(good) > 0) {
          violations++;
          totalPricesOfViolatingUsers += this.marketOutcome.getPrice(good);
        }
      }
      // If the price of all users is zero, then the ratio should be zero.
      if (totalPricesOfUsers == 0) {
        this.marketClearanceViolations = new Pair<Integer, Double>(violations, 0.0);
      } else {
        this.marketClearanceViolations = new Pair<Integer, Double>(violations, totalPricesOfViolatingUsers / totalPricesOfUsers);
      }
    }
    return this.marketClearanceViolations;
  }
  
  /**
   * Wrapper to get the seller revenue for the associated outcome.
   * 
   * @return the seller revenue for the outcome.
   * @throws MarketOutcomeException
   * @throws MarketAllocationException
   */
  public double getSellerRevenue() throws MarketOutcomeException, MarketAllocationException {
    return this.marketOutcome.sellerRevenue();
  }
  
  /**
   * Wrapper to get the value of the allocation for the associated outcome.
   * 
   * @return the value of the allocation for the outcome.
   * @throws MarketAllocationException
   */
  public double getWelfare() throws MarketAllocationException {
    return this.marketOutcome.getMarketAllocation().value();
  }
  
  /**
   * Computes the ratio of the seller revenue w.r.t a given value.
   * 
   * @param value
   * @return the ratio of the seller revenue w.r.t a given value.
   * @throws MarketOutcomeException
   * @throws MarketAllocationException
   */
  public double getSellerRevenueRatio(double value) throws MarketOutcomeException, MarketAllocationException {
    return NumberMethods.getRatio(this.getSellerRevenue() , value);
  }
  
  /**
   * Computes the ratio of the welfare w.r.t a given value.
   * @param value
   * @return the ratio of the welfare w.r.t a given value.
   * @throws MarketAllocationException
   */
  public double getWelfareRatio(double value) throws MarketAllocationException {
    return NumberMethods.getRatio(this.getWelfare() , value);
  }
  
  /**
   * Computes the ratio of EF violations to the total number of bidders.
   * 
   * @return the ratio of EF violations to the total number of bidders.
   * @throws MarketOutcomeException
   * @throws MarketAllocationException
   */
  public double getEFViolationsRatio() throws MarketOutcomeException, MarketAllocationException {
    return (double) this.numberOfEnvyBidders() / this.marketOutcome.getMarketAllocation().getMarket().getNumberBidders();
  }

  /**
   * Computes the ratio of MC violations to the total number of goods.
   * 
   * @return the ratio of MC violations to the total number of goods.
   * @throws MarketOutcomeException 
   * @throws MarketAllocationException 
   */
  public double getMCViolationsRatio() throws MarketAllocationException, MarketOutcomeException {
    return (double) this.getMarketClearanceViolations().getKey() / this.marketOutcome.getMarketAllocation().getMarket().getNumberGoods();
  }
  
  /**
   * Getter.
   * 
   * @return the time taken to compute the result.
   */
  public long getTime() {
    return this.time;
  }
  
  /**
   * Getter.
   * 
   * @return the market outcome associated to these statistics.
   */
  public MarketOutcome<M, G, B> getMarketOutcome() {
    return this.marketOutcome;
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
     * @param good
     *          - user index.
     * @param price
     *          - user price.
     */
    public GoodPrices(G good, double price) {
      this.good = good;
      this.price = price;
    }

    /**
     * Getter.
     * 
     * @return user index.
     */
    public G getGood() {
      return this.good;
    }

    /**
     * Getter.
     * 
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
   * 
   * @author Enrique Areyan Viqueira
   */
  public class UserPriceComparator implements Comparator<GoodPrices> {
    @Override
    public int compare(GoodPrices o1, GoodPrices o2) {
      // Order objects by ascending price
      return Double.compare(o1.getPrice(), o2.getPrice());
    }
  }
}
