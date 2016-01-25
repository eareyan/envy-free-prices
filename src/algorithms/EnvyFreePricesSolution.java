package algorithms;

import structures.MarketAllocation;
import structures.MarketPrices;
/*
 * This class stores the resulting envy-free prices from LP.
 * 
 * @author Enrique Areyan Viqueira
 */
public class EnvyFreePricesSolution extends MarketPrices {
	String Status;
	
	public EnvyFreePricesSolution(){
		super();
		this.Status = "Empty";
	}
	public EnvyFreePricesSolution(String Status){
		super();
		this.Status = Status;
	}
	
	public EnvyFreePricesSolution(MarketAllocation marketAllocation, double[] pricesVector, String Status){
		super(marketAllocation, pricesVector);
		this.Status = Status;
	}
	
	public EnvyFreePricesSolution(MarketAllocation marketAllocation, double[][] pricesMatrix, String Status){
		super(marketAllocation, pricesMatrix);
		this.Status = Status;
	}
	
	public String getStatus(){
		return this.Status;
	}
}
