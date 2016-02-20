package algorithms;

import structures.MarketAllocation;
import structures.MarketPrices;
/*
 * This class stores the resulting envy-free prices from LP.
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
	public EnvyFreePricesSolutionLP(String Status){
		super();
		this.Status = Status;
	}
	
	public EnvyFreePricesSolutionLP(MarketAllocation marketAllocation, double[] pricesVector, String Status, double optimalValue){
		super(marketAllocation, pricesVector);
		this.Status = Status;
		this.optimalValue = optimalValue;
	}
	
	public EnvyFreePricesSolutionLP(MarketAllocation marketAllocation, double[][] pricesMatrix, String Status, double optimalValue){
		super(marketAllocation, pricesMatrix);
		this.Status = Status;
		this.optimalValue = optimalValue;
	}
	
	public String getStatus(){
		return this.Status;
	}
	
	public double getOptimalValue(){
		return this.optimalValue;
	}
	
	public String toString(){
		return ""+this.optimalValue;
	}
}
