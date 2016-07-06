package structures;

import java.util.ArrayList;

import structures.exceptions.MarketAllocationException;
import allocations.objectivefunction.ObjectiveFunction;

/*
 * This class associates a Market with an Allocation.
 * The idea is that an allocation is an object associated 
 * with a market and thus, it should be a separate from it.
 * 
 * Also, an allocation is the result of some algorithm.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketAllocation {
	/* Market that was allocated. */
	protected Market market;
	/* Allocation for the market. */
	protected int[][] allocation;
	/* Objective function. */
	protected ObjectiveFunction f;
	/*
	 * Basic constructor. Takes a Market and an allocation. 
	 */
	public MarketAllocation(Market m, int[][] allocation){
		this.market = m;
		this.allocation = allocation;
	}
	/*
	 * Second constructor. Takes a Market, an allocation, and an objective function.
	 */
	public MarketAllocation(Market m, int[][] allocation, ObjectiveFunction f){
		this(m,allocation);
		this.f = f;
	}
	/*
	 * Getters
	 */
	public Market getMarket(){
		return this.market;
	}
	public int[][] getAllocation(){
		return this.allocation;
	}
	public int getAllocation(int i, int j){
		return this.allocation[i][j];
	}	
	/*
	 * Get value of allocation. The value of an allocation is the sum
	 * of rewards obtained by the allocation across all campaigns.
	 * This value depends on the objective function being used.
	 */
	public double value() throws MarketAllocationException{
		double totalReward = 0.0;
		/* Loop through each campaign to check if it is satisfied. */
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			/* Compute the extra reward attained by campaign j under the current allocation. */
			totalReward += this.value(j);
		}
		return totalReward;
	}
	/*
	 * Computes the value of the allocation for campaign j. 
	 */
	public double value(int j) throws MarketAllocationException{
		/* Make sure we have an objective function to be able to compute the value of the allocation. */
		if(this.f == null) throw new MarketAllocationException("An objective function must be defined to compute the value of an allocation");
		return this.f.getObjective(this.market.campaigns[j].getReward(), this.market.campaigns[j].getDemand(), this.getBundleNumber(j) + this.market.campaigns[j].getAllocationSoFar()) - this.f.getObjective(this.market.campaigns[j].getReward(), this.market.campaigns[j].getDemand(), this.market.campaigns[j].getAllocationSoFar());
	}
	/*
	 * Computes the value of the allocation for all campaigns in the given input list
	 */
	public double value(ArrayList<Integer> campaignIndices) throws MarketAllocationException{
		double totalReward = 0.0;
		for(Integer j:campaignIndices){
			totalReward += this.value(j);
		}
		return totalReward;
	}
    /*
     * Checks if a campaign is assign something at all
     */
    public boolean isCampaignBundleZero(int j){
    	for(int i=0;i<this.market.getNumberUsers();i++){
    		if(this.allocation[i][j]>0){
    			return false;
    		}
    	}
    	return true;
    }
    /*
     * Returns the number of impressions from user i that were allocated
     */
    public int allocationFromUser(int i){
    	int totalAllocation = 0;
    	for(int j=0;j<this.market.getNumberCampaigns();j++){
    		totalAllocation += this.allocation[i][j];
    	}
    	return totalAllocation;
    }
    /*
     * Get current bundle number for campaign j
     */
    public int getBundleNumber(int j){
    	int totalAllocation = 0;
    	for(int i=0;i<this.market.getNumberUsers();i++){
    		if(this.allocation[i][j]>0){
    			totalAllocation += this.allocation[i][j];
    		}
    	}
    	return totalAllocation;
    }
    /*
     * Update an allocation
     */
    public void updateAllocationEntry(int i,int j,int alloc){
    	this.allocation[i][j] = alloc;
    } 	
}
