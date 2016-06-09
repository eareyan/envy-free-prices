package allocations.error;

@SuppressWarnings("serial")
public class AllocationException extends Exception{

	protected AllocationErrorCodes error;
	/*
	 * Constructor
	 */
	public AllocationException(AllocationErrorCodes error){
		super(error.getDescription());
		this.error = error;
	}
	/*
	 * Get Error Object
	 */
	public AllocationErrorCodes getError(){
		return this.error;
	}
}
