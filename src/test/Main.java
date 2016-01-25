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
		Market market = MarketFactory.randomMarket(3, 5, 1.0);
		System.out.println(market);
		
		MarketPrices waterFallAllocationPrices = new Waterfall(market).Solve();
		System.out.println("\n======Waterfall:======");
		System.out.println("Value = " + waterFallAllocationPrices.valuePriceMatrix());
		System.out.println(waterFallAllocationPrices.getMarketAllocation().stringAllocationMatrix());
		waterFallAllocationPrices.printPricesMatrix();
		
		//System.exit(-1);
		
		//Find Efficient Allocation
		int[][] efficientAllocation = new EfficientAllocationLP(market).Solve().get(0);
		MarketAllocation marketEfficientAllocation = new MarketAllocation(market, efficientAllocation);

		//Find Envy-Free Prices, both matrix and vector
		EnvyFreePricesSolution MatrixSol = new EnvyFreePricesMatrix(marketEfficientAllocation).Solve();
		EnvyFreePricesSolution VectorSol = new EnvyFreePricesVector(marketEfficientAllocation).Solve();
		
		//Report Solutions for LP programs
		if(MatrixSol.getStatus() == "Optimal"){
			System.out.print("======LP - Matrix:======");
			System.out.println("\nMatrix Value = " + MatrixSol.valuePriceMatrix());
			System.out.println(marketEfficientAllocation.stringAllocationMatrix());
			MatrixSol.printPricesMatrix();
		}
		if(VectorSol.getStatus() == "Optimal"){
			System.out.print("======LP - Vector:======");
			System.out.println("\nVector Value = " + VectorSol.valuePriceVector());
			VectorSol.printPricesVector();
		}
	}
}
