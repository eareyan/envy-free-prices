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
		this.allocation = allocation;
	}
	
	public Market getMarket(){
		return this.market;
	}
	
	public int[][] getAllocation(){
		return this.allocation;
	}
	/*
	 * Get value of allocation. The value of an allocation is defined as the
	 * sum of all the campaigns that are fullfilled by the allocation.
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
