package structures.exceptions;

/**
 * Exception thrown if an operation on a good is invalid.
 * 
 * @author Enrique Areyan Viqueira
 */
@SuppressWarnings("serial")
public class GoodsException extends Exception{
  
  /**
   * Constructor.
   * 
   * @param info - exception information.
   */
  public GoodsException(String info) {
    super(info);
  }

}