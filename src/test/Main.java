package test;

import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesMatrix;
import algorithms.EnvyFreePricesVector;
import algorithms.EnvyFreePricesSolution;
import algorithms.Waterfall;
import structures.Market;
import structures.MarketFactory;
import structures.MarketAllocation;

/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
	
	public static void main(String[] args){
		
		System.out.println("Envy-free prices testing");
		//Create Random Market
		Market Market = MarketFactory.randomMarket(2, 4, 0.75);
		System.out.println(Market);
		
		Waterfall Waterfall = new Waterfall(Market);
		Waterfall.Solve();
		
		System.exit(-1);
		
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
			System.out.println("\nMatrix Value = " + MatrixSol.valuePriceMatrix(EfficientAllocation));
		}
		if(VectorSol.getStatus() == "Optimal"){
			VectorSol.printPricesVector();
			System.out.println("\nVector Value = " + VectorSol.valuePriceVector(EfficientAllocation));
		}
	}
}
