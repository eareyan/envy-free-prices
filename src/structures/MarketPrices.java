package structures;

import java.text.DecimalFormat;

/*
 * In this class we have an allocated market and prices.
 * Prices could be a vector of prices or a matrix of prices.
 * 
 * This class support common operations on prices for an allocated market,
 * regardless of the algorithm that produced the allocation and/or prices.
 * Such operations might be printing the prices or computing values.
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

	public double getPriceVector(int i){
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
}
