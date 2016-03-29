package unitdemand.ascendingauction;

public class Bid {
	protected int i; /*User*/
	protected int j; /*Campaign*/
	protected double bid; /* Bid */
	
	public Bid(int i,int j,double bid){
		this.i = i;
		this.j = j;
		this.bid = bid;
	}
	
	public int getUser(){
		return this.i;
	}
	public int getCampaign(){
		return this.j;
	}
	public double getBid(){
		return this.bid;
	}
	public String toString(){
		return "("+this.i + ","+this.j+","+this.bid+")";
	}
}
