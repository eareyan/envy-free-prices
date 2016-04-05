package unitdemand;


/*
 * This class stores a matching, defined here as a matrix of integers
 * with a 1 at position (i,j) if user i is allocated to campaign j,
 * and 0 otherwise. It also stores the prices of a matching and implements
 * method to get the value of a matching. 
 * 
 * @author Enrique Areyan Viqueira
 */
public class Matching {
	protected double[][] valuationMatrix;
	protected double[] prices;
	protected int[][] matching;
	protected double valueOfMatching = -1;
	protected double sellerRevenue = -1;
	protected static double epsilon = 0.1;
	/*
	 * Constructors.
	 */
	public Matching(double[][] valuationMatrix, int[][] matching, double[] prices){
		this.valuationMatrix = valuationMatrix;
		this.matching = matching;
		this.prices = prices;
	}
	public Matching(double[][] valuationMatrix, int[][] matching){
		this.valuationMatrix = valuationMatrix;
		this.matching = matching;
	}
	/*
	 * Getters.
	 */
	public double[] getPrices(){
		return this.prices;
	}
	public double[][] getValuationMarix(){
		return this.valuationMatrix;
	}
	//Gets value of matching. Implements singleton.
	public double getValueOfMatching(){
		if(this.valueOfMatching == -1){
			double value = 0.0;
			for(int i=0;i<this.valuationMatrix.length;i++){
				for(int j=0;j<this.valuationMatrix[0].length;j++){
					if(this.matching[i][j] == 1){
						value += this.valuationMatrix[i][j];
					}
				}
			}
			this.valueOfMatching = value;
		}
		return this.valueOfMatching;
	}
	
	public int[][] getMatching(){
		return this.matching;
	}
	
	public double getSellerRevenue(){
		if(this.sellerRevenue == -1){
			double revenue = 0.0;
			if(this.matching == null) return 0.0;
			for(int i=0;i<this.matching.length;i++){
				for(int j=0;j<this.matching[0].length;j++){
					if(this.matching[i][j] == 1){
						revenue += prices[i];
					}
				}
			}
			this.sellerRevenue = revenue;
		}
		return this.sellerRevenue;
	}
	public double getCampaignUtility(int j){
		for(int i=0;i<this.valuationMatrix.length;i++){
			if(this.matching[i][j] == 1){
				return this.valuationMatrix[i][j] - this.prices[i];
			}
		}
		return 0.0;
	}
	/*
	 * Compute number of envy-campaigns
	 */
    public int numberOfEnvyCampaigns(){
    	int totalNumberEnvy = 0;
    	for(int j=0;j<this.valuationMatrix[0].length;j++){
    		double campaignUtility = this.getCampaignUtility(j);
    		for(int i=0;i<this.valuationMatrix.length;i++){
    			if((this.valuationMatrix[i][j] - this.prices[i]) - campaignUtility > Matching.epsilon){
    				totalNumberEnvy++;
    			}
    		}
    	}
    	return totalNumberEnvy;
    }
	/*
	 * Compute the number of users that are not allocated
	 * and not priced at zero
	 */
	public int computeWalrasianViolations(){
		int violations = 0;
		for(int i=0;i<this.valuationMatrix.length;i++){
			if(this.allocationFromUser(i) == 0 && this.prices[i]>0){
				violations++;
			}
		}
		return violations;
	}
    public int allocationFromUser(int i){
    	int totalAllocation = 0;
    	for(int j=0;j<this.valuationMatrix[0].length;j++){
    		totalAllocation += this.matching[i][j];
    	}
    	return totalAllocation;
    }	
    @Override
    public String toString(){
    	return  "Revenue:\t"+this.getSellerRevenue() + "\n";
    }
	/*
	 * Computes the maximum weight matching and its value for the argument matrix.
	 */
	public static Matching computeMaximumWeightMatchingValue(double[][] matrix){
		int[] result  = new MWBMatchingAlgorithm(matrix).getMatching();
        int[][] matching = new int[matrix.length][matrix[0].length];
        for(int i=0;i<result.length;i++){
        	//System.out.println("--" + result[i]);
        	//If the assignment is possible and the user is actually connected to the campaigns
        	if(result[i] > -1 && matrix[i][result[i]] > Double.NEGATIVE_INFINITY){
        		matching[i][result[i]] = 1;
        	}
        }
        return new Matching(matrix,matching);
	}    
}
