package structures.exceptions;

/**
 * Exception thrown when a goods object could not be created.
 * 
 * @author Enrique Areyan Viqueira
 */
@SuppressWarnings("serial")
public class GoodsCreationException extends GoodsException{
  
  /**
   * Constructor.
   * 
   * @param info - exception information.
   */
  public GoodsCreationException(String info) {
    super(info);
  }

}
