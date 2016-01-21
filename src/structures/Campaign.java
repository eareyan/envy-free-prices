package structures;

/*
 * Represents a single campaign.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Campaign {
	/*
	 * Demand of this campaign
	 */
	protected int demand;
	/*
	 * Reward of this campaign
	 */
	protected double reward;
	/*
	 * Constructor.
	 */
	public Campaign(int demand, double reward){
		this.demand = demand;
		this.reward = reward;
	}
	/*
	 * Getters
	 */
	public int getDemand(){
		return this.demand;
	}
	public double getReward(){
		return this.reward;
	}
}
