package structures;

import java.text.DecimalFormat;

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
	/*
	 * Market Allocation Object. Contains a market object and an allocation (matrix of integers) 
	 */
	protected MarketAllocation marketAllocation;
	/*
	 * pricesMatrix[i][j] is the price for campaign j per impression in user class i
	 */
	protected double[][] pricesMatrix;
	/*
	 * priceVector[i] is the price per impression of user i
	 */
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
	public double[] getPriceVector(){
		return this.pricesVector;
	}
	public double getPriceVectorComponent(int i){
		return this.pricesVector[i];
	}
	/*
	 * Seller revenue for price matrix
	 * Seller revenue is defined as \sum_{i,j}x_{i,j}p_{i,j}
	 */
	public double sellerRevenuePriceMatrix(){
		double value = 0;
		for(int i=0;i<this.marketAllocation.allocation.length;i++){
			for(int j=0;j<this.marketAllocation.allocation[0].length;j++){
				value += this.marketAllocation.allocation[i][j] * this.pricesMatrix[i][j];
			}
		}
		return value;
	}
	
	/*
	 * Seller revenue for price vector
	 * Seller revenue is defined as \sum_{i,j}x_{i,j}p_{i}
	 */

	public double sellerRevenuePriceVector(){
		double value = 0;
		if(this.pricesVector == null){
			//throw new Exception("Ask for seller revenue using price vector but this price vector is null");
			return 0.0;
		}
		for(int i=0;i<this.marketAllocation.allocation.length;i++){
			for(int j=0;j<this.marketAllocation.allocation[0].length;j++){
				value += this.marketAllocation.allocation[i][j] * this.pricesVector[i];
			}
		}
		return value;
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
	/*
	 * Printers
	 */
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
	public String toString(){
		return "" + this.sellerRevenuePriceVector();
	}
}
