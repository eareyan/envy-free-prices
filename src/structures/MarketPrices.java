package structures;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.PriorityQueue;


/*
 * In this class we have an allocated market and prices.
 * Prices could be a vector of prices or a matrix of prices.
 * 
 * This class support common operations on prices for an allocated market,
 * regardless of the algorithm that produced the allocation and/or prices.
 * Such operations might be printing the prices or computing values such
 * as seller revenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketPrices {
	
	protected MarketAllocation marketAllocation;
	
	protected double[][] pricesMatrix;

	protected double[] pricesVector;
	
	public MarketPrices(){
		
	}
	
	public MarketPrices(MarketAllocation marketAllocation, double[][] pricesMatrix){
		this.marketAllocation = marketAllocation;
		this.pricesMatrix = pricesMatrix;
	}

	public MarketPrices(MarketAllocation marketAllocation, double[] pricesVector){
		this.marketAllocation = marketAllocation;
		this.pricesVector = pricesVector;
	}
	/*
	 * Getters
	 */
	public MarketAllocation getMarketAllocation(){
		return this.marketAllocation;
	}

	public double getPriceVectorComponent(int i){
		return this.pricesVector[i];
	}
	public double sellerRevenuePriceMatrix(){
		double value = 0;
		for(int i=0;i<this.marketAllocation.allocation.length;i++){
			for(int j=0;j<this.marketAllocation.allocation[0].length;j++){
				value += this.marketAllocation.allocation[i][j] * this.pricesMatrix[i][j];
			}
		}
		return value;
	}
	
	public double[] getPriceVector(){
		return this.pricesVector;
	}

	public double sellerRevenuePriceVector(){
		double value = 0;
		for(int i=0;i<this.marketAllocation.allocation.length;i++){
			for(int j=0;j<this.marketAllocation.allocation[0].length;j++){
				value += this.marketAllocation.allocation[i][j] * this.pricesVector[i];
			}
		}
		return value;
	}
	
	public void printPricesMatrix(){
		DecimalFormat df = new DecimalFormat("#.00"); 
		System.out.println("Prices Matrix: ");
    	for(int i=0; i< this.pricesMatrix.length; i++){
    		for(int j=0; j<this.pricesMatrix[0].length; j++){
    			System.out.print(df.format(this.pricesMatrix[i][j]) + "\t");
    		}
    		System.out.println("");
    	}
	}
	public void printPricesVector(){
		DecimalFormat df = new DecimalFormat("#.00"); 
		System.out.println("Prices Vector: ");
    	for(int i=0; i< this.pricesVector.length; i++){
    		System.out.println("P("+i+") = " + df.format(this.pricesVector[i]));
    	}	
	}
	
    /*
     * Get current bundle cost for a campaign
     */
    public double getBundleCost(int j){
    	double cost = 0.0;
    	for(int i=0;i<this.marketAllocation.getMarket().getNumberUsers();i++){
    		if(this.marketAllocation.allocation[i][j]>0){
    			cost += this.marketAllocation.allocation[i][j] * this.pricesVector[i];
    		}
    	}
    	return cost;
    }	
	
    public boolean isCampaignEnvyFree(PriorityQueue<UserPrices> queue, int campaignIndex){
    	//System.out.println("Heuristic for campaign:" + campaignIndex + ", check this many users:" + queue.size());
    	double cost = 0.0;
    	int impressionsNeeded = this.marketAllocation.getMarket().getCampaign(campaignIndex).getDemand();
    	while (impressionsNeeded > 0 && queue.size() != 0){
    		UserPrices userPriceObject = queue.remove();
    		int userIndex = userPriceObject.getUserIndex();
			if(this.marketAllocation.getMarket().isConnected(userIndex, campaignIndex)){
				//System.out.println(u);
				if(this.marketAllocation.getMarket().getUser(userIndex).getSupply()>= impressionsNeeded){
					cost += impressionsNeeded * this.pricesVector[userIndex];
					impressionsNeeded = 0;
				}else{
					cost += this.pricesVector[userIndex] * this.marketAllocation.getMarket().getUser(userIndex).getSupply();
					impressionsNeeded -= this.marketAllocation.getMarket().getUser(userIndex).getSupply();
				}
			}
		}
    	//System.out.println("impressionsNeeded = "+impressionsNeeded);
    	//System.out.println("cost of cheapest bundle = "+cost);
    	//System.out.println("cost of current  bundle = "+getBundleCost(campaignIndex));
    	//System.out.println("nbr  of current  bundle = "+getBundleNumber(campaignIndex));
    	//System.out.println("reward of this campaign = "+this.campaigns[campaignIndex].getReward());
    	//if(impressionsNeeded > 0 || getBundleCost(campaignIndex) <= cost){
    	if(impressionsNeeded > 0){ //If you cannot be satisfied, you are inmediately envy-free
    		//System.out.println("true");
    		return true;
    	}else{
    		if(this.marketAllocation.getBundleNumber(campaignIndex) >= this.marketAllocation.getMarket().getCampaign(campaignIndex).getDemand()){//This campaign was satisfied
    			if(getBundleCost(campaignIndex) - cost > 0.01){
    				//System.out.println("cost = "+cost);
    				//System.out.println("current cost = "+getBundleCost(campaignIndex));
    				return false;
    			}
    			return true;
    		}else{//the campaign was not satisfied
    			if(cost < this.marketAllocation.getMarket().getCampaign(campaignIndex).getReward()){
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
    			System.out.println("Campaign " + j + " is envy");
    			counter++;
    		}
    	}
    	return counter;
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
	/*
	 * This method computes how many violations to the Walrasian equilibrium, there are.
	 * A violation is one where an unallocated user has a price greater than zero.
	 */
	public double[] computeWalrasianEqViolations(){
		int violations = 0;
		double totalPricesOfUsers = 0.0;
		double totalPricesOfViolatingUsers = 0.0;
		for(int i=0;i<this.marketAllocation.getMarket().getNumberUsers();i++){
			totalPricesOfUsers += this.getPriceVectorComponent(i);
			if(this.marketAllocation.allocationFromUser(i) == 0 && this.getPriceVectorComponent(i) > 0){
				violations++;
				totalPricesOfViolatingUsers += this.getPriceVectorComponent(i);
			}
		}
		if(totalPricesOfUsers == 0){ //If the price of all users is zero, then the ratio should be zero
			return new double[]{violations,0.0};
		}else{
			return new double[]{violations,totalPricesOfViolatingUsers / totalPricesOfUsers};
		}
	}
	/*
	 * This method computes how many campaigns are envy-free for the unit demand case
	 */
	public int computeEFViolationUnitDemand(){
		int violations = 0;
		double campaignCurrentProfit = 0.0;
		outerloop:
		for(int j=0;j<this.marketAllocation.getMarket().getNumberCampaigns();j++){
			if(this.marketAllocation.isCampaignBundleZero(j)){
				//System.out.println("\t" + j + " zero bundle");
				campaignCurrentProfit = 0.0;
			}else{
				//System.out.println("\t" + j + " NOT zero bundle");
				campaignCurrentProfit = this.marketAllocation.getMarket().getCampaign(j).getReward() -this.getBundleCost(j);
			}
			//System.out.println("Campaign #"+j+" profit = " + campaignCurrentProfit);
			for(int i=0;i<this.marketAllocation.getMarket().getNumberUsers();i++){
				if(this.marketAllocation.getMarket().isConnected(i, j)){
					//System.out.println("Profit by user " + i + " = " + (this.marketAllocation.getMarket().getCampaign(j).getReward() - this.getPriceVectorComponent(i)));
					if(this.marketAllocation.getMarket().getCampaign(j).getReward() - this.getPriceVectorComponent(i) > campaignCurrentProfit){
						violations++;
						//System.out.println("***** Campaign " + j + " is ENVY!, it would rather have one of " + i);
						//System.out.println(this.marketAllocation.getMarket().getCampaign(j).getReward() - this.getPriceVectorComponent(i));
						//System.out.println(campaignCurrentProfit);
						//System.exit(-1);
						continue outerloop;
					}
				}
			}
		}
		return violations;
	}
	public String toString(){
		return "" + this.sellerRevenuePriceVector();
	}
}
