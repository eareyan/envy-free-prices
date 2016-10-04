package structures.exceptions;

/**
 * Exception thrown when a bidder object could not be created.
 * 
 * @author Enrique Areyan Viqueira
 */
@SuppressWarnings("serial")
public class BidderCreationException extends Exception {

  /**
   * Constructor.
   * 
   * @param info - exception information.
   */
  public BidderCreationException(String info) {
    super(info);
  }
  
}
