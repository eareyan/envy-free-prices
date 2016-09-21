package structures.exceptions;

/**
 * Exception thrown when a campaign object could not be created.
 * 
 * @author Enrique Areyan Viqueira
 */
@SuppressWarnings("serial")
public class MarketCreationException extends Exception {
  
  /**
   * Constructor.
   * 
   * @param info - exception information.
   */
  public MarketCreationException(String info) {
    super(info);
  }

}
