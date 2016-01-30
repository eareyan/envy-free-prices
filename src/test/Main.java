package test;

import java.util.Random;

import experiments.SingletonMarketExperiments;
import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesMatrixLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.Waterfall;
import algorithms.WaterfallPrices;
import singletonmarket.EVPApproximation;
import singletonmarket.HungarianAlgorithm;
import singletonmarket.MaxWEQ;
import structures.Market;
import structures.MarketFactory;
import structures.MarketAllocation;
import structures.MarketPrices;
import util.Printer;

/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
	
	public static void main(String[] args){
		
		Market market = MarketFactory.randomMarket(3, 3, 1.0);
		double [][] costMatrix = SingletonMarketExperiments.getCostMatrixFromMarket(market);
        Printer.printMatrix(costMatrix);
        EVPApproximation EVPAppr = new EVPApproximation(costMatrix);
        EVPAppr.Solve();
        
        MaxWEQ maxWEQ = new MaxWEQ(costMatrix);
        maxWEQ.Solve();
		
		
		/*
		System.out.println("Envy-free prices testing");
		//Create Random Market
		//Market Market = MarketFactory.randomMarket(2, 4, 0.75);
		Market market = MarketFactory.randomMarket(3, 3, 1.0);
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
		Random generator = new Random();
		double maxReward = 100.0;
		double minReward = 1.0;
		double[][] costMatrix = new double[market.getNumberUsers()][market.getNumberCampaigns()];        
        for(int i=0;i<market.getNumberUsers();i++){
        	for(int j=0;j<market.getNumberCampaigns();j++){
        		if(market.isConnected(i, j)){
        			costMatrix[i][j] =-1.0 * generator.nextDouble() * (maxReward - minReward) + minReward;
        		}else{
        			costMatrix[i][j] = Double.MAX_VALUE;
        		}
        	}
        }
        Printer.printMatrix(costMatrix);
        HungarianAlgorithm H = new HungarianAlgorithm(costMatrix);
        int[] result = H.execute();
        int[][] hungarianAllocation = new int[market.getNumberUsers()][market.getNumberCampaigns()];
        for(int i=0;i<result.length;i++){
        	System.out.println("--" + result[i]);
        	//If the assignment is possible and the user is actually connected to the campaigns
        	if(result[i] > -1 && market.isConnected(i, result[i])){
        		hungarianAllocation[i][result[i]] = 1;
        	}
        }
        System.out.println("Hungarian Allocation:");
        Printer.printMatrix(hungarianAllocation);
        MaxWEQ maxWEQ = new MaxWEQ(costMatrix);
        
        MarketPrices maxWEQPrices = new MarketPrices(new MarketAllocation(market,hungarianAllocation), maxWEQ.Solve());
        System.out.println("MaxEQ value = " + maxWEQPrices.sellerRevenuePriceVector());
		*/
		
	}
}
