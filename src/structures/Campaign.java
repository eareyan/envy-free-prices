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
	 * Backpointer to another campaign, default value -1 means no backpointer
	 */
	protected int backpointer = -1;
	/*
	 * Priority. This is used to have a notion of priority among campaigns.
	 */
	protected int priority = -1;
	/*
	 * Constructor.
	 */
	public Campaign(int demand, double reward){
		this.demand = demand;
		this.reward = reward;
	}
	public Campaign(int demand, double reward,int backpointer){
		this.demand = demand;
		this.reward = reward;
		this.backpointer = backpointer;
	}
	public Campaign(int demand, double reward,int backpointer, int priority){
		this(demand,reward,backpointer);
		this.priority = priority;
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
	public int getBackpointer(){
		return this.backpointer;
	}
	public int getPriority(){
		return this.priority;
	}
	/*
	 * Setters
	 */
	public void setReward(double reward){
		this.reward = reward;
	}
	/*
	 * String representation
	 */
	public String toString(){
		return "R = " + this.reward + ", I = " + this.getDemand();
	}
}
