package unitdemand.structures;

import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;
import util.NumberMethods;
import util.Printer;

import com.google.common.collect.ImmutableList;

/**
 * Stores the prices of a market allocation and implements methods to compute statistics about the market outcome.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitDemandMarketOutcome {

  /**
   * The market allocation.
   */
  private final UnitDemandMarketAllocation marketAllocation;

  /**
   * The prices.
   */
  private final double[] prices;

  /**
   * Seller revenue, a double.
   */
  protected double sellerRevenue = -1;

  /**
   * List of envy bidders.
   */
  ImmutableList<Integer> listOfEnvyBidders;

  /**
   * Time
   */
  private long time = -1;

  /**
   * Epsilon parameter. Used when computing number of envy bidders.
   */
  protected static double epsilon = 0.00001;

  /**
   * Constructor.
   * 
   * @param marketAllocation
   * @param prices
   * @throws UnitDemandException
   */
  public UnitDemandMarketOutcome(UnitDemandMarketAllocation marketAllocation, double[] prices) throws UnitDemandException {
    if (prices.length != marketAllocation.getValuationMarix().length) {
      throw new UnitDemandException("The length of the prices vector must agree with the number of items.");
    }
    this.marketAllocation = marketAllocation;
    this.prices = prices;
  }

  /**
   * Constructor.
   * 
   * @param marketAllocation
   * @param prices
   * @param time
   * @throws UnitDemandException
   */
  public UnitDemandMarketOutcome(UnitDemandMarketAllocation marketAllocation, double[] prices, long time) throws UnitDemandException {
    this(marketAllocation, prices);
    this.time = time;
  }
  
  /**
   * Getter.
   * 
   * @return the market allocation associated with this outcome.
   */
  public UnitDemandMarketAllocation getMarketAllocation() {
    return this.marketAllocation;
  }

  /**
   * Computes the ratio of the seller revenue w.r.t a given value.
   * 
   * @param value
   * @return the ratio of the seller revenue to the given value.
   */
  public double getSellerRevenueRatio(double value) {
    return NumberMethods.getRatio(this.getSellerRevenue(), value);
  }

  /**
   * Computes the ratio of EF violations to the total number of bidders.
   * 
   * @return
   */
  public double getEFViolationsRatio() {
    return (double) this.numberOfEnvyBidders() / this.marketAllocation.getValuationMarix()[0].length;
  }

  /**
   * Computes the ratio of MC violations to the total number of goods.
   * 
   * @return
   */
  public double getMCViolationsRatio() {
    return (double) this.computeMarketClearanceViolations() / this.marketAllocation.getValuationMarix().length;
  }

  /**
   * Returns the time.
   * 
   * @return
   */
  public double getTime() {
    return this.time;
  }

  /**
   * Getter.
   * 
   * @return array of prices.
   */
  public double[] getPrices() {
    return this.prices;
  }

  /**
   * Getter. Implements singleton.
   * 
   * @return seller revenue under this outcome.
   */
  public double getSellerRevenue() {
    if (this.sellerRevenue == -1) {
      if (this.marketAllocation == null) {
        return 0.0;
      } else {
        double revenue = 0.0;
        for (int i = 0; i < this.marketAllocation.getAllocation().length; i++) {
          for (int j = 0; j < this.marketAllocation.getAllocation()[0].length; j++) {
            if (this.marketAllocation.getAllocation()[i][j] == 1) {
              revenue += prices[i];
            }
          }
        }
        this.sellerRevenue = revenue;
      }
    }
    return this.sellerRevenue;
  }

  /**
   * Compute number of envy-bidders.
   * 
   * @return number of envy-bidders.
   */
  public int numberOfEnvyBidders() {
    if (this.listOfEnvyBidders == null) {
      ImmutableList.Builder<Integer> listOfEnvyBiddersBuilder = ImmutableList.builder();
      outerloop: for (int j = 0; j < this.marketAllocation.getValuationMarix()[0].length; j++) {
        double bidderUtility = this.getBidderUtility(j);
        for (int i = 0; i < this.marketAllocation.getValuationMarix().length; i++) {
          if ((this.marketAllocation.getValuationMarix()[i][j] - this.prices[i]) - bidderUtility > epsilon) {
            listOfEnvyBiddersBuilder.add(j);
            continue outerloop;
          }
        }
      }
      this.listOfEnvyBidders = listOfEnvyBiddersBuilder.build();
    }
    return this.listOfEnvyBidders.size();
  }

  /**
   * Returns a list with the bidders that are envy.
   * 
   * @return
   * @throws MarketAllocationException
   * @throws MarketOutcomeException
   */
  public ImmutableList<Integer> listOfEnvyBidders() {
    if (this.listOfEnvyBidders == null) {
      this.numberOfEnvyBidders();
    }
    return this.listOfEnvyBidders;
  }

  /**
   * Compute the number of goods that are not allocated and not priced at zero.
   * 
   * @return the number of goods that are not allocated and not priced at zero.
   */
  public int computeMarketClearanceViolations() {
    int violations = 0;
    for (int i = 0; i < this.marketAllocation.getValuationMarix().length; i++) {
      if (this.marketAllocation.allocationFromGood(i) == 0 && this.prices[i] > 0) {
        violations++;
      }
    }
    return violations;
  }

  /**
   * Computes the utility of bidder j under this outcome.
   * 
   * @param j - bidder index.
   * @return utility of bidder j under this outcome.
   */
  public double getBidderUtility(int j) {
    for (int i = 0; i < this.marketAllocation.getValuationMarix().length; i++) {
      if (this.marketAllocation.getAllocation()[i][j] == 1) {
        return this.marketAllocation.getValuationMarix()[i][j] - this.prices[i];
      }
    }
    return 0.0;
  }

  /**
   * Prints prices.
   */
  public void printPrices() {
    Printer.printVector(this.prices);
  }

}
