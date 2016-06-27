package allocations.error;
/*
 * This enum provides a list of all errors that might happen
 * when trying to create an object of the envy-free library.
 * 
 * @author Enrique Areyan
 */
public enum AllocationErrorCodes {
	
	CPLEX_FAILED(0, "CPLEX failed in the allocation algorithm"),
	UNKNOWN_ERROR(1, "Unknow error when trying to allocate. CPLEX did not failed but we got a null allocation. "),
	RESERVE_NEGATIVE(2, "Trying to allocate with a negative reserve price."),
	STEP_NEGATIVE(3, "Trying to allocate with a negative step size."),
	ALLOCATION_VECTOR_WRONG_SIZE(4, "The size of the allocation vector must be equal to the number of campaigns."),
	INITIAL_ALLOCATION_NEGATIVE(5, "The initial allocation of a campaign cannot be negative");
	
	private final int code;
	private final String description;
	
	private AllocationErrorCodes(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public int getCode() {
		return code;
	}	

}
