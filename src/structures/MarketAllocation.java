package structures;


/*
 * This class associates a Market with an Allocation.
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
