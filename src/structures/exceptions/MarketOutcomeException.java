package structures.exceptions;

/**
 * Exception thrown in the MarketPrices object.
 * @author Enrique Areyan Viqueira
 */
@SuppressWarnings("serial")
public class MarketOutcomeException extends Exception {

  /**
   * Constructor.
   * @param info - exception information.
   */
  public MarketOutcomeException(String info) {
    super(info);
  }
  
}
