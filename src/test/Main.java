package test;

import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesMatrixLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.Waterfall;
import algorithms.WaterfallPrices;
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
		Market market = MarketFactory.randomMarket(3, 5, 0.15);
		System.out.println(market);
		
		WaterfallPrices waterFallAllocationPrices = new Waterfall(market).Solve();
		System.out.println("\n======Waterfall:======");
		System.out.println("Seller Revenue = " + waterFallAllocationPrices.sellerRevenuePriceMatrix());
		System.out.println(waterFallAllocationPrices.getMarketAllocation().stringAllocationMatrix());
		waterFallAllocationPrices.printPricesMatrix();
		System.out.println("Waterfall allocation value = " + waterFallAllocationPrices.getMarketAllocation().value());
		System.out.println("Error = " + waterFallAllocationPrices.computeViolations());
		
		//System.exit(-1);
		
		//Find Efficient Allocation
		int[][] efficientAllocation = new EfficientAllocationLP(market).Solve().get(0);
		MarketAllocation marketEfficientAllocation = new MarketAllocation(market, efficientAllocation);
		System.out.println("Efficient allocation value = " + marketEfficientAllocation.value());

		//Find Envy-Free Prices, both matrix and vector
		EnvyFreePricesSolutionLP MatrixSol = new EnvyFreePricesMatrixLP(marketEfficientAllocation).Solve();
		EnvyFreePricesSolutionLP VectorSol = new EnvyFreePricesVectorLP(marketEfficientAllocation).Solve();
		
		//Report Solutions for LP programs
		if(MatrixSol.getStatus() == "Optimal"){
			System.out.print("======LP - Matrix:======");
			System.out.println("\nSeller Revenue = " + MatrixSol.sellerRevenuePriceMatrix());
			System.out.println("Objective value = " + MatrixSol.getOptimalValue());
			System.out.println(marketEfficientAllocation.stringAllocationMatrix());
			MatrixSol.printPricesMatrix();
		}
		if(VectorSol.getStatus() == "Optimal"){
			System.out.print("======LP - Vector:======");
			System.out.println("\nSeller Revenue = " + VectorSol.sellerRevenuePriceVector());
			VectorSol.printPricesVector();
		}
	}
}
