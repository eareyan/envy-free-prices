package structures.exceptions;

@SuppressWarnings("serial")
public class AllocationException extends Exception {
  /**
   * Constructor.
   * 
   * @param info - exception information.
   */
  public AllocationException(String info) {
    super(info);
  }
}
