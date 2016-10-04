package unitdemand.ascendingauction;

/**
 * This class represents a triplet (good,bidder,bid).
 * 
 * @author Enrique Areyan Viqueira
 */
public class Bid {
  
  /**
   * Good index.
   */
	protected int i;
	
	/**
	 * Bidder index.
	 */
	protected int j;
	
	/**
	 * Bid.
	 */
	protected double bid;
	
	/**
	 * Constructor. 
	 * 
	 * @param i - good index.
	 * @param j - bidder index.
	 * @param bid - bid.
	 */
	public Bid(int i,int j,double bid){
		this.i = i;
		this.j = j;
		this.bid = bid;
	}
	
	/**
	 * Getter.
	 * @return good index.
	 */
	public int getGoodIndex(){
		return this.i;
	}
	
	/**
	 * Getter.
	 * 
	 * @return bidder index.
	 */
	public int getBidderIndex(){
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
