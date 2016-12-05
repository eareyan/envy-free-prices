package statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.math3.util.Pair;

import com.google.common.collect.ImmutableList;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;

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
  private final MarketOutcome<M, G, B> marketPrices;

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
   * A tuple containing 2 elements. The first element is the number of MC
   * violations. The second element in the proportion of total price of
   * violating MC prices to total prices. Implements singleton.
   */
  protected Pair<Integer, Double> marketClearanceViolations;

  /**
   * Constructor.
   * 
   * @param marketPrices
   *          - a MarketPrices object.
   */
  public PricesStatistics(MarketOutcome<M, G, B> marketPrices) {
    this.marketPrices = marketPrices;
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
   * This function takes a list of GoodPrices object - these are tuples (i,p_i)-
   * and a bidder and returns whether or not there exists a bundle that is
   * cheaper to the currently assigned bundle.
   * 
   * @param goodOrderedList
   *          - a list of UserPrices object.
   * @param bidder
   *          - a bidder object.
   * @return true if the bidder is envy at current prices.
   * @throws MarketAllocationException
   * @throws MarketOutcomeException
   */
  private boolean isBidderEnvyFree(ArrayList<GoodPrices> goodOrderedList, B bidder) throws MarketAllocationException, MarketOutcomeException {
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
       * satisfied but there exists a cheaper bundle than the one it got, and
       * (2) The campaign received nothing and there exists a bundle in its
       * demand set.
       */
      if (this.marketPrices.getMarketAllocation().allocationToBidder(bidder) >= bidder.getDemand()) {
        // Case (1)
        if (PricesStatistics.debug) {
          System.out.println("Case 1:" + this.marketPrices.getBundleCost(bidder) + "," + costCheapestBundle);
          System.out.println("\t" + (this.marketPrices.getBundleCost(bidder) - costCheapestBundle));
        }
        return (this.marketPrices.getBundleCost(bidder) - costCheapestBundle <= PricesStatistics.epsilon);
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
  public int numberOfEnvyBidders() throws MarketOutcomeException,
      MarketAllocationException {
    if (this.listOfEnvyBidders == null) {
      // Initialize the list of envy-bidders.
      ImmutableList.Builder<B> listOfEnvyBiddersBuilder = ImmutableList.builder();
      // Construct a list of goods.
      ArrayList<GoodPrices> listOfGoods = new ArrayList<GoodPrices>();
      for (G good : this.marketPrices.getMarketAllocation().getMarket().getGoods()) {
        listOfGoods.add(new GoodPrices(good, this.marketPrices.getPrice(good)));
      }
      // Sort the list by ascending order of price.
      Collections.sort(listOfGoods, new UserPriceComparator());
      // Check that each bidder is envy-free w.r.t the previously constructed list.
      for (B bidder : this.marketPrices.getMarketAllocation().getMarket().getBidders()) {
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
  public Pair<Integer, Double> getMarketClearanceViolations()
      throws MarketAllocationException, MarketOutcomeException {
    if (this.marketClearanceViolations == null) {
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
      // If the price of all users is zero, then the ratio should be zero.
      if (totalPricesOfUsers == 0) {
        System.out.println("Flag 1 : ");
        this.marketClearanceViolations = new Pair<Integer, Double>(violations, 0.0);
      } else {
        System.out.println("Flag 2 : " + totalPricesOfViolatingUsers / totalPricesOfUsers);
        this.marketClearanceViolations = new Pair<Integer, Double>(violations, totalPricesOfViolatingUsers / totalPricesOfUsers);
      }
    }
    return this.marketClearanceViolations;
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
