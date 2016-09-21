package structures.exceptions;

/**
 * Exception thrown in the MarketAllocation object.
 * @author Enrique Areyan Viqueira
 */
@SuppressWarnings("serial")
public class MarketAllocationException extends Exception {

  /**
   * Constructor.
   * @param info - exception information.
   */
  public MarketAllocationException(String info) {
    super(info);
  }
  
}
