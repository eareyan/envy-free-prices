package test;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.GeneralApproximation;
import algorithms.Waterfall;
import algorithms.WaterfallMAXWEQ;
import experiments.UnitDemandExperiments;
import structures.Market;
import structures.MarketFactory;
import structures.MarketPrices;
import unitdemand.EVPApproximation;
import unitdemand.HungarianAlgorithm;
import unitdemand.MWBMatchingAlgorithm;
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
	
	public static void main(String[] args) throws IloException{
		Market randomMarket = MarketFactory.randomMarket(2, 2, 0.75);
		System.out.println(randomMarket);
		
		System.out.println(">>>>>>>>>>>>>>>>>Watefall MaxWEQ");
		WaterfallMAXWEQ wfMaxWEQ = new WaterfallMAXWEQ(randomMarket);
		MarketPrices p = wfMaxWEQ.Solve();
		System.out.println(p.sellerRevenuePriceVector());
		Printer.printVector(p.getPriceVector());
		Printer.printMatrix(p.getMarketAllocation().getAllocation());
		System.out.println("numberOfEnvyCampaigns = " + p.numberOfEnvyCampaigns());
		
		System.exit(-1);
		System.out.println("<<<<<<<<<<<<<<<<<<<General Approximation");
		IloCplex iloObject0 = new IloCplex();
		int[][] efficientAllocation = new EfficientAllocationLP(randomMarket).Solve(iloObject0).get(0);
		GeneralApproximation generalApp = new GeneralApproximation(randomMarket,efficientAllocation);
		System.out.println(generalApp.Solve().sellerRevenuePriceVector());		
	}
}
