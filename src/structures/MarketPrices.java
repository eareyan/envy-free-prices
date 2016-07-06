package structures;

import java.text.DecimalFormat;
import java.util.ArrayList;

import structures.exceptions.MarketPricesException;

/*
 * In this class we have an allocated market and prices.
 * Prices are a vector of prices.
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
	 * Market Allocation Object. Contains a market object and an allocation (matrix of integers). 
	 */
	protected MarketAllocation marketAllocation;
	/*
	 * priceVector[i] is the price per impression of user i. 
	 */
	protected double[] pricesVector;
	/*
	 * Basic Constructor. 
	 */
	public MarketPrices(){
		
	}
	/*
	 * Constructor that takes an allocation and a price vector. 
	 */
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
	 * This function computes the seller revenue for the entire market.
	 * Seller revenue is defined as: \sum_{i,j}x_{i,j}p_{i}
	 */
	public double sellerRevenuePriceVector() throws MarketPricesException{
		double value = 0;
		for(int j=0;j<this.marketAllocation.allocation[0].length;j++){
			value += this.sellerRevenueFromCampaign(j);
		}
		return value;
	}
	/*
	 * This function computes the seller revenue only for a list of campaigns L received as parameter.
	 * Seller revenue is defined as: \forall j\inL \sum_{i}x_{i,j}p_{i}
	 */
	public double sellerRevenuePriceVector(ArrayList<Integer> campaignIndices) throws MarketPricesException{
		double value = 0;
		for(Integer j:campaignIndices){
			value += this.sellerRevenueFromCampaign(j);
		}
		return value;
	}	
	/*
	 * This function computes the seller revenue for a given campaign
	 * Seller revenue for a given campaign is defined as: \sum_{i}x_{i,j}p_{i}
	 */
	public double sellerRevenueFromCampaign(int j) throws MarketPricesException{
		if(this.pricesVector == null) throw new MarketPricesException("Ask for seller revenue using price vector but the price vector is null");
		double value = 0;
		for(int i=0;i<this.marketAllocation.allocation.length;i++){
			value += this.marketAllocation.allocation[i][j] * this.pricesVector[i];
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
	public void printPricesVector(){
		DecimalFormat df = new DecimalFormat("#.00"); 
		System.out.println("Prices Vector: ");
    	for(int i=0; i< this.pricesVector.length; i++){
    		System.out.println("P("+i+") = " + df.format(this.pricesVector[i]));
    	}	
	}
	public String toString(){
		try {
			return "" + this.sellerRevenuePriceVector();
		} catch (MarketPricesException e) {
			System.out.println("MarketPricesException = " + e.getMessage());
		}
		return null;
	}
}
