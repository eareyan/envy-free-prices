package allocations.error;

/**
 * This enum provides a list of all errors that might happen
 * when trying to create an object of the envy-free library.
 * 
 * @author Enrique Areyan Viqueira
 */
public enum AllocationErrorCodes {

  CPLEX_FAILED(0, "CPLEX failed in the allocation algorithm"),
  UNKNOWN_ERROR(1, "Unknow error when trying to allocate. CPLEX did not failed but we got a null allocation. "), 
  RESERVE_NEGATIVE(2, "Trying to allocate with a negative reserve price."), 
  STEP_NEGATIVE(3,"Trying to allocate with a negative step size."), 
  ALLOCATION_VECTOR_WRONG_SIZE(4, "The size of the allocation vector must be equal to the number of campaigns."), 
  INITIAL_ALLOCATION_NEGATIVE(5, "The initial allocation of a campaign cannot be negative");

  /**
   * code is an integer representing the error.
   */
  private final int code;
  
  /**
   * description is an string representation of the error.
   */
  private final String description;

  /**
   * Constructor.
   * @param code - an integer.
   * @param description - a string.
   */
  private AllocationErrorCodes(int code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Getter.
   * @return the string description of the error.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Getter.
   * @return the integer representation of the error.
   */
  public int getCode() {
    return code;
  }

}
