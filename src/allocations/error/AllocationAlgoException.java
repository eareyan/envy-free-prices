package allocations.error;

@SuppressWarnings("serial")
/**
 * This class implements an AllocationException by extending
 * the default Exception object. The AllocationException
 * is thrown when there was an error creating an object of the envy-free library.
 * @author Enrique Areyan Viqueira
 */
public class AllocationAlgoException extends Exception {

  /**
   * The AllocationErrorCodes object.
   */
  protected AllocationAlgoErrorCodes error;

  /**
   * Constructor.
   * @param error - an AllocationErrorCodes object.
   */
  public AllocationAlgoException(AllocationAlgoErrorCodes error) {
    super(error.getDescription());
    this.error = error;
  }

  /**
   * Get Error Object.
   * @return an AllocationErrorCodes object.
   */
  public AllocationAlgoErrorCodes getError() {
    return this.error;
  }
  
}
