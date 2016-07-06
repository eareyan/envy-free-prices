package algorithms.pricing;

import structures.MarketAllocation;
import structures.MarketPrices;
import structures.exceptions.MarketPricesException;

/*
 * This class stores the resulting envy-free prices from LP.
 * It extends MarketPrices.
 * 
 * @author Enrique Areyan Viqueira
 */
public class EnvyFreePricesSolutionLP extends MarketPrices {
	String Status;
	double optimalValue;
	
	public EnvyFreePricesSolutionLP(){
		super();
		this.Status = "Empty";
	}
	public EnvyFreePricesSolutionLP(MarketAllocation marketAllocation,String Status){
		super();
		this.marketAllocation = marketAllocation;
		this.Status = Status;
	}
	
	public EnvyFreePricesSolutionLP(MarketAllocation marketAllocation, double[] pricesVector, String Status, double optimalValue){
		super(marketAllocation, pricesVector);
		this.Status = Status;
		this.optimalValue = optimalValue;
	}
	
	public String getStatus(){
		return this.Status;
	}
	
	public double xxgetOptimalValue(){
		return this.optimalValue;
	}
	
	public String toString(){
		try {
			return  "Revenue:\t"+this.sellerRevenuePriceVector()+ "-" + this.Status + "\n";
		} catch (MarketPricesException e) {
			System.out.println("MarketPricesException = " + e.getMessage());
		}
		return null;
	}
}
