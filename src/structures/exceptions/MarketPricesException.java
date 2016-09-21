package structures.exceptions;

/**
 * Exception thrown in the MarketPrices object.
 * @author Enrique Areyan Viqueira
 */
@SuppressWarnings("serial")
public class MarketPricesException extends Exception {

  /**
   * Constructor.
   * @param info - exception information.
   */
  public MarketPricesException(String info) {
    super(info);
  }
  
}
