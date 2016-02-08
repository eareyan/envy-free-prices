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
}
