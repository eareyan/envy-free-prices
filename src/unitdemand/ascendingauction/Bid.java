package unitdemand.ascendingauction;

/**
 * This class represents a triplet (user,campaign,bid).
 * 
 * @author Enrique Areyan Viqueira
 */
public class Bid {
  
  /**
   * User index.
   */
	protected int i;
	
	/**
	 * Campaign index.
	 */
	protected int j;
	
	/**
	 * Bid.
	 */
	protected double bid;
	
	/**
	 * Constructor. 
	 * 
	 * @param i - user index.
	 * @param j - campaign index.
	 * @param bid - bid.
	 */
	public Bid(int i,int j,double bid){
		this.i = i;
		this.j = j;
		this.bid = bid;
	}
	
	/**
	 * Getter.
	 * @return user index.
	 */
	public int getUser(){
		return this.i;
	}
	
	/**
	 * Getter.
	 * 
	 * @return campaign index.
	 */
	public int getCampaign(){
		return this.j;
	}
	
	/**
	 * Getter.
	 * 
	 * @return bid.
	 */
	public double getBid(){
		return this.bid;
	}
	
	@Override
	public String toString(){
		return "("+this.i + ","+this.j+","+this.bid+")";
	}
	
}
