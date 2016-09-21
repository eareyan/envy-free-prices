package algorithms.pricing;

import structures.MarketAllocation;
import structures.MarketPrices;
import structures.exceptions.MarketPricesException;

/**
 * This class stores the resulting envy-free prices from LP.
 * It extends MarketPrices.
 * 
 * @author Enrique Areyan Viqueira
 */
public class EnvyFreePricesSolutionLP extends MarketPrices {
  
  /**
   * Status of the LP.
   */
  String Status;
  
  /**
   * The optimalValue of the LP (seller revenue).
   */
  double optimalValue;

  /**
   * Constructor. Receives no parameters.
   */
  public EnvyFreePricesSolutionLP() {
    super();
    this.Status = "Empty";
  }

  /**
   * Constructor.
   * @param marketAllocation - a marketAllocation object.
   * @param Status - a string representing the status of the LP.
   */
  public EnvyFreePricesSolutionLP(MarketAllocation marketAllocation, String Status) {
    super();
    this.marketAllocation = marketAllocation;
    this.Status = Status;
  }

  /**
   * Constructor.
   * @param marketAllocation - a marketAllocation object.
   * @param pricesVector - a vector of double containing the prices of users classes.
   * @param Status - a string representing the status of the LP.
   * @param optimalValue - the value (seller revenue) from LP.
   */
  public EnvyFreePricesSolutionLP(MarketAllocation marketAllocation, double[] pricesVector, String Status, double optimalValue) {
    super(marketAllocation, pricesVector);
    this.Status = Status;
    this.optimalValue = optimalValue;
  }

  /**
   * Getter.
   * @return a string containing the LP status.
   */
  public String getStatus() {
    return this.Status;
  }

  @Override
  public String toString() {
    try {
      return "Revenue:\t" + this.sellerRevenuePriceVector() + "-" + this.Status + "\n";
    } catch (MarketPricesException e) {
      System.out.println("MarketPricesException = " + e.getMessage());
    }
    return null;
  }
}
