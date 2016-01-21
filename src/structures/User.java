package structures;

/*
 * Represents a single user.
 * 
 * @author Enrique Areyan Viqueira
 */
public class User {
	/*
	 * Supply of this user
	 */
	protected int supply;
	/*
	 * Constructor
	 */
	public User(int supply){
		this.supply = supply;
	}
	/*
	 * Getters
	 */
	public int getSupply(){
		return this.supply;
	}
}
