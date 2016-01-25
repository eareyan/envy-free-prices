package test;

import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesMatrix;
import algorithms.EnvyFreePricesVector;
import algorithms.EnvyFreePricesSolution;
import algorithms.Waterfall;
import structures.Market;
import structures.MarketFactory;
import structures.MarketAllocation;
import structures.MarketPrices;

/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
	
	public static void main(String[] args){
		
		System.out.println("Envy-free prices testing");
		//Create Random Market
		//Market Market = MarketFactory.randomMarket(2, 4, 0.75);
		Market Market = MarketFactory.randomMarket(3, 3, 1.0);
		System.out.println(Market);
		
		MarketPrices waterFallAllocationPrices = new Waterfall(Market).Solve();
		System.out.println("Waterfall Prices: " + waterFallAllocationPrices.valuePriceMatrix());
		
		//System.exit(-1);
		
		//Find Efficient Allocation
		int[][] EfficientAllocation = new EfficientAllocationLP(Market).Solve().get(0);
		MarketAllocation MarketAllocation = new MarketAllocation(Market, EfficientAllocation);
		System.out.println(MarketAllocation.stringAllocationMatrix());

		//Find Envy-Free Prices, both matrix and vector
		EnvyFreePricesSolution MatrixSol = new EnvyFreePricesMatrix(MarketAllocation).Solve();
		EnvyFreePricesSolution VectorSol = new EnvyFreePricesVector(MarketAllocation).Solve();
		
		//Report Solutions
		if(MatrixSol.getStatus() == "Optimal"){
			MatrixSol.printPricesMatrix();
			System.out.println("\nMatrix Value = " + MatrixSol.valuePriceMatrix());
		}
		if(VectorSol.getStatus() == "Optimal"){
			VectorSol.printPricesVector();
			System.out.println("\nVector Value = " + VectorSol.valuePriceVector());
		}
	}
}
