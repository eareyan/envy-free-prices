package algorithms.pricing;

import allocations.objectivefunction.interfaces.ObjectiveFunction;

import com.google.common.collect.ImmutableMap;

import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;

/**
 * This class stores the resulting envy-free prices from LP.
 * It extends MarketPrices.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RestrictedEnvyFreePricesLPSolution <O extends ObjectiveFunction> extends MarketOutcome<Goods, Bidder<Goods>, O> {
  
  /**
   * Status of the LP.
   */
  protected final String Status;
  
  /**
   * The optimalValue of the LP (seller revenue).
   */
  protected final double optimalValue;

  /**
   * Constructor. Receives no parameters.
   */
  public RestrictedEnvyFreePricesLPSolution(MarketAllocation<Goods, Bidder<Goods>, O> marketAllocation, ImmutableMap<Goods, Double> prices, String Status, double optimalValue) {
    super(marketAllocation, prices);
    this.Status = Status;
    this.optimalValue = optimalValue;
  }

  /**
   * Getter.
   * 
   * @return a string containing the LP status.
   */
  public String getStatus() {
    return this.Status;
  }

  @Override
  public String toString() {
    try {
      return "Revenue:\t" + this.sellerRevenue() + "-" + this.Status + "\n";
    } catch (MarketOutcomeException e) {
      System.out.println("MarketPricesException = " + e.getMessage());
    } catch (MarketAllocationException e) {
      System.out.println("MarketAllocationException = " + e.getMessage());
    }
    return null;
  }
}
