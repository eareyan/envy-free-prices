package test;

import experiments.UnitDemandExperiments;
import structures.Market;
import structures.MarketFactory;
import unitdemand.EVPApproximation;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import unitdemand.MaxWEQReservePrices;
import util.Printer;

/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
	
	public static void main(String[] args){
		
		Market market = MarketFactory.randomMarket(3, 3, 0.75);
		double [][] costMatrix = UnitDemandExperiments.getCostMatrixFromMarket(market);
        Printer.printMatrix(costMatrix);
        Matching M = UnitDemandExperiments.getMaximumMatchingFromCostMatrix(costMatrix);
        int[][] maximumMatchingAllocation = M.getMatching();
        //double[] rewards = M.getPrices();
        //Printer.printMatrix(maximumMatchingAllocation);
		System.out.println("****maxweqsolution*****");
        MaxWEQ maxWEQ = new MaxWEQ(costMatrix);
        Matching maxweqMatching = maxWEQ.Solve(); 
		Printer.printVector(maxweqMatching.getPrices());
		Printer.printMatrix(maxweqMatching.getMatching());
		System.out.println(maxweqMatching.getSellerRevenue());
		
		EVPApproximation evpApp = new EVPApproximation(costMatrix);
		System.out.println("****evpsolution*****");
		Matching evpMatching = evpApp.Solve();
		Printer.printVector(evpMatching.getPrices());
		Printer.printMatrix(evpMatching.getMatching());
		System.out.println(evpMatching.getSellerRevenue());
		//System.out.println("------maxWEQReserve------");
		//MaxWEQReservePrices maxWEQReserve = new MaxWEQReservePrices(costMatrix,new Double[]{1.0,1.0,1.0});
		//maxWEQReserve.Solve();
		
	}
}
