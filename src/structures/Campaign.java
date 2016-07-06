package structures;

import structures.exceptions.CampaignCreationException;

/*
 * Represents a single campaign.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Campaign {
	/* Demand of this campaign. */
	protected int demand;
	/* Reward of this campaign. */
	protected double reward;
	/* User access level. Indicates what percentage of visibility the campaign has across all users. */
	protected double level = 1.0;
	/* Reserve Price. Indicates the minimum amount a campaign must pay per impression. */
	protected double reserve = 0.0;
	/* Backpointer to another campaign, default value -1 means no backpointer. */
	protected int backpointer = -1;
	/* Priority. This is used to have a notion of priority among campaigns.*/
	protected int priority = -1;
	/* How many users allocated so far to this campaign */
	protected int allocationSoFar = 0;
	/*
	 * Constructors.
	 */
	public Campaign(int demand, double reward) throws CampaignCreationException{
		if(demand <= 0){
			throw new CampaignCreationException("The demand of a campaign must be a positive integer");
		}
		this.demand = demand;
		if(reward <= 0){
			throw new CampaignCreationException("The reward of a campaign must be a positive double");
		}
		this.reward = reward;
	}
	public Campaign(int demand, double reward, double level, double reserve) throws CampaignCreationException{
		this(demand,reward);
		if(reserve<0.0){
			throw new CampaignCreationException("The reserve price of a campaign must be a positive double at least zero");
		}
		this.level = level;
		this.reserve = reserve;
	}
	public Campaign(int demand, double reward,int backpointer) throws CampaignCreationException{
		this(demand, reward);
		this.backpointer = backpointer;
	}
	public Campaign(int demand, double reward,int backpointer, int priority) throws CampaignCreationException{
		this(demand,reward,backpointer);
		this.priority = priority;
	}
	public Campaign(int demand, double reward, double level,double reserve, int backpointer, int priority) throws CampaignCreationException{
		this(demand,reward,backpointer,priority);
		this.level = level;
		this.reserve = reserve;
	}
	public Campaign(int demand, double reward, double level,double reserve, int backpointer, int priority, int allocationSoFar) throws CampaignCreationException{
		this(demand,reward,level,reserve,backpointer,priority);
		if(allocationSoFar < 0){
			throw new CampaignCreationException("The allocation so far must be a positive (possibly zero) integer");
		}
		this.allocationSoFar = allocationSoFar;
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
	public double getLevel(){
		return this.level;
	}
	public double getReserve(){
		return this.reserve;
	}
	public int getBackpointer(){
		return this.backpointer;
	}
	public int getPriority(){
		return this.priority;
	}
	public int getAllocationSoFar(){
		return this.allocationSoFar;
	}
	/*
	 * Setters
	 */
	public void setReward(double reward){
		this.reward = reward;
	}
	public void setDemand(int demand){
		this.demand = demand;
	}
	public void setReserve(double reserve){
		this.reserve = reserve;
	}
	/*
	 * String representation
	 */
	public String toString(){
		return "(Demand = " + this.getDemand() + ", Reward = " + this.reward + ", Backpointer = " + this.getBackpointer() + ", priority = " + this.getPriority() + ")";
	}
}
