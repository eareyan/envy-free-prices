package algorithms.ascendingauction;

/*
 * This class represents a pair (i,p), where i is the id of a user
 * and p is its price. This class is needed mainly to be able to 
 * sort prices of user classes.
 * @author Enrique Areyan Viqueira
 */
public class UserPrice {
	/*
	 * User id
	 */
	protected int i;
	/*
	 * User price
	 */
	protected double price;
	
	public UserPrice(int i, double price){
		this.i = i;
		this.price = price;
	}
	/*
	 * Getters
	 */
	public int getI(){
		return this.i;
	}
	
	public double getPrice(){
		return this.price;
	}
	/*
	 * Update price.
	 */
	public void updatePrice(double price){
		this.price = price;
	}
	
	public String toString(){
		return "("+this.i+","+this.price+")";
	}
}
