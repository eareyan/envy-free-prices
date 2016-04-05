package algorithms.allocations.greedy;

/*
 * This auxiliary data structure keeps track of the remaining supply of users
 * so that they can be ordered before being assigned to campaigns.
 */
public class UserSupply{
	protected int i;
	protected int remainingSupply;
	public UserSupply(int i,int remainingSupply){
		this.i = i;
		this.remainingSupply = remainingSupply;
	}
	public int getId(){
		return this.i;
	}
	public int getRemainingSupply(){
		return this.remainingSupply;
	}
	public String toString(){
		return "("+i+","+this.remainingSupply+")";
	}
}
