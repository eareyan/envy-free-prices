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
	protected double[] prices;
	protected int[][] matching;
	protected double valueOfMatching;
	/*
	 * Constructors.
	 */
	public Matching(){
		
	}
	public Matching(double[] prices, int[][] matching){
		this.prices = prices;
		this.matching = matching;
	}
	
	public Matching(int[][] matching,double valueOfMatching){
		this.matching = matching;
		this.valueOfMatching = valueOfMatching;
	}
	public Matching(double[] prices){
		this.prices = prices;
	}
	/*
	 * Getters.
	 */
	public double[] getPrices(){
		return this.prices;
	}
	
	public double getValueOfMatching(){
		return this.valueOfMatching;
	}
	
	public int[][] getMatching(){
		return this.matching;
	}
	
	public double getSellerRevenue(){
		double revenue = 0.0;
		if(this.matching == null) return 0.0;
		for(int i=0;i<this.matching.length;i++){
			for(int j=0;j<this.matching[0].length;j++){
				if(this.matching[i][j] == 1){
					revenue += prices[i];
				}
			}
		}
		return revenue;
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
        double valueOfMatching = 0.0;
        int[][] matching = new int[matrix.length][matrix[0].length];
        for(int i=0;i<result.length;i++){
        	//System.out.println("--" + result[i]);
        	//If the assignment is possible and the user is actually connected to the campaigns
        	if(result[i] > -1 && matrix[i][result[i]] > Double.NEGATIVE_INFINITY){
        		valueOfMatching += matrix[i][result[i]];
        		matching[i][result[i]] = 1;
        	}
        }
        return new Matching(matching,valueOfMatching);
	}    
}
