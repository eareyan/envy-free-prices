package statistics;

import java.util.Comparator;
import java.util.PriorityQueue;

import structures.MarketAllocation;
import structures.MarketPrices;

public class PricesStatistics {
	
	protected MarketPrices marketPrices;
	
	protected MarketAllocation marketAllocation;
	
	protected double[] pricesVector;
	
	protected static double epsilon = 0.5;
	
	
	public PricesStatistics(MarketPrices marketPrices){
		this.marketPrices = marketPrices;
		this.marketAllocation = this.marketPrices.getMarketAllocation();
		this.pricesVector = this.marketPrices.getPriceVector();
	}
	
    public boolean isCampaignEnvyFree(PriorityQueue<UserPrices> queue, int campaignIndex){
    	//System.out.println("Heuristic for campaign:" + campaignIndex + ", check this many users:" + queue.size());
    	double costCheapestBundle = 0.0;
    	int impressionsNeeded = this.marketAllocation.getMarket().getCampaign(campaignIndex).getDemand();
    	while (impressionsNeeded > 0 && queue.size() != 0){
    		UserPrices userPriceObject = queue.remove();
    		int userIndex = userPriceObject.getUserIndex(), userSupply = this.marketAllocation.getMarket().getUser(userIndex).getSupply();
			if(this.marketAllocation.getMarket().isConnected(userIndex, campaignIndex)){
				if(userSupply >= impressionsNeeded){
					costCheapestBundle += impressionsNeeded * this.pricesVector[userIndex];
					impressionsNeeded = 0;
				}else{
					costCheapestBundle +=  userSupply * this.pricesVector[userIndex];
					impressionsNeeded -= userSupply;
				}
			}
		}
    	//System.out.println("\timpressionsNeeded = "+impressionsNeeded);
    	if(impressionsNeeded > 0){ //If you cannot be satisfied, you are immediately envy-free
    		return true;
    	}else{
        	/*System.out.println("\tcost of cheapest bundle = "+costCheapestBundle);
        	System.out.println("\tcost of current  bundle = "+this.marketPrices.getBundleCost(campaignIndex));
        	System.out.println("\tnbr  of current  bundle = "+this.marketAllocation.getBundleNumber(campaignIndex));
        	System.out.println("\treward of this campaign = "+this.marketAllocation.getMarket().getCampaign(campaignIndex).getReward());*/
    		if(this.marketAllocation.getBundleNumber(campaignIndex) >= this.marketAllocation.getMarket().getCampaign(campaignIndex).getDemand()){//This campaign was satisfied
    			if(this.marketPrices.getBundleCost(campaignIndex) - costCheapestBundle > PricesStatistics.epsilon){
    				//System.out.println("cost = "+cost);
    				//System.out.println("current cost = "+getBundleCost(campaignIndex));
    				return false;
    			}
    			return true;
    		}else{//the campaign was not satisfied
    			if(this.marketAllocation.getMarket().getCampaign(campaignIndex).getReward() - costCheapestBundle > PricesStatistics.epsilon ){
    				return false;
    			}
    			return true;
    		}
    	}
    }
    /*
     * Check if this whole market is envy-free
     */
    public int numberOfEnvyCampaigns(){
		//System.out.println("Check Heuristic For Envy-free-ness");
    	/*
    	 * Construct a priority queue with users where the priority is price in ascending order.
    	 */
    	int numUsers = this.marketAllocation.getMarket().getNumberUsers();
    	PriorityQueue<UserPrices> queue = new PriorityQueue<UserPrices>(numUsers, new UserPriceComparator());
		for(int i=0;i<numUsers;i++){
			 queue.add(new UserPrices(i,this.pricesVector[i]));
		}
		//System.out.println(queue);
    	/*
    	 * Check that each campaign is envy-free for the previously constructed queue.
    	 */
		int counter = 0;
    	for(int j=0;j<this.marketAllocation.getMarket().getNumberCampaigns();j++){
    		if(!this.isCampaignEnvyFree(new PriorityQueue<UserPrices>(queue), j)){//Pass a copy of the queue each time...
    			//System.out.println("Campaign " + j + " is envy");
    			counter++;
    		}
    	}
    	return counter;
    }
	/*
	 * This method computes how many violations to the Walrasian equilibrium, there are.
	 * A violation is one where an unallocated user has a price greater than zero.
	 */
	public double[] computeWalrasianViolations(){
		int violations = 0;
		double totalPricesOfUsers = 0.0;
		double totalPricesOfViolatingUsers = 0.0;
		for(int i=0;i<this.marketAllocation.getMarket().getNumberUsers();i++){
			totalPricesOfUsers += this.pricesVector[i];
			if(this.marketAllocation.allocationFromUser(i) == 0 && this.pricesVector[i] > 0){
				violations++;
				totalPricesOfViolatingUsers += this.pricesVector[i];
			}
		}
		if(totalPricesOfUsers == 0){ //If the price of all users is zero, then the ratio should be zero
			return new double[]{violations,0.0};
		}else{
			return new double[]{violations,totalPricesOfViolatingUsers / totalPricesOfUsers};
		}
	}
    /* Auxiliary class that links users to prices so we can order users by prices*/
    class UserPrices{
    	protected int userIndex;
    	protected double userPrice;
    	public UserPrices(int userIndex,double userPrice){
    		this.userIndex = userIndex;
    		this.userPrice = userPrice;
    	}
    	public int getUserIndex(){return this.userIndex;}
    	public double getPrice(){return this.userPrice;}
    	public String toString(){return "("+this.userIndex+","+this.userPrice+")";};
    }
    /* Order users by prices*/
    public class UserPriceComparator implements Comparator<UserPrices>{
    	@Override
    	public int compare(UserPrices o1, UserPrices o2) {
    		/*
    		 * Order objects by ascending price
    		 */
    		return (int) (o1.getPrice() - o2.getPrice());
    	}
    }	
}
