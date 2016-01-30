package structures;

/*
 * This class associates a Market with an Allocation.
 * The idea is that an allocation is a feature associated 
 * with a market and thus, it should be a separate object.
 * 
 * Also, an allocation is the result of some algorithm
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketAllocation {
	
	protected Market market;
	
	protected int[][] allocation;
		
	public MarketAllocation(Market m, int[][] allocation){
		this.market = m;
		this.allocation = this.cleanMatrix(allocation);
	}
	
	public Market getMarket(){
		return this.market;
	}
	
	public int[][] getAllocation(){
		return this.allocation;
	}
	/*
	 * Get value of allocation. The value of an allocation is defined as the
	 * sum of all the campaigns that are fulfilled by the allocation.
	 */
	public double value(){
		int allocation;
		double totalReward = 0.0;
		/* Loop through each campign to check if it is satisfied*/
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			allocation = 0;
			for(int i=0;i<this.market.getNumberUsers();i++){
				allocation += this.allocation[i][j]; /* add allocation from each user*/
			}
			if(allocation >= this.market.getCampaign(j).getDemand()){
				/* Campaign was satisfied, add this reward */
				totalReward += this.market.getCampaign(j).getReward();
			}
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
	 * This method sets the columns of the matrix to zero if a campaign is not 
	 * completely satisfied. This is in line with the way in which all the conditions
	 * and the analysis has been done so far. This method avoids a matrix where a 
	 * campaign might get some allocation when in fact it is not satisfied.
	 */
	protected int[][] cleanMatrix(int[][] efficientAllocation){
		int totalAllocation = 0;
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			for(int i=0;i<this.market.getNumberUsers();i++){
				totalAllocation += efficientAllocation[i][j];
			}
			if(totalAllocation < this.market.getCampaign(j).getDemand()){//This campaign is not satisfied
				for(int i=0;i<this.market.getNumberUsers();i++){
					efficientAllocation[i][j] = 0;
				}
			}
			totalAllocation = 0;
		}
		return efficientAllocation;
	}    
    /*
     * Helper function to print the allocation matrix
     */
    public String stringAllocationMatrix(){
    	if(this.allocation != null){    	
    		String ret = "Allocation Matrix:\t";
    		for(int i=0;i<this.market.getNumberUsers();i++){
    			ret += "\n";
    			for(int j=0;j<this.market.getNumberCampaigns();j++){
    				ret += "\t"+this.allocation[i][j] ;
    			}	
    		}
    		return ret+"\n";
    	}else{
    		return "There is no allocation for this market";
    	}
    }	
}
