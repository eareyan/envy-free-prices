package test;

import ilog.concert.IloException;

import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;










//import ilog.concert.IloException;
//import ilog.cplex.IloCplex;
import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.Waterfall;
import algorithms.WaterfallMAXWEQ;
import algorithms.WaterfallPrices;
import algorithms.lp.EnvyFreePricesVectorLPReservePrices;
import algorithms.lp.GeneralApproximation;
import algorithms.lp.GeneralApproximation1;
import algorithms.lp.GeneralApproximation2;
import algorithms.lp.LPDummies;
import experiments.RunParameters;
import experiments.UnitDemandComparison;
import experiments.UnitDemandExperiments;
import structures.Campaign;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import structures.MarketPrices;
import unitdemand.HungarianAlgorithm;
//import unitdemand.LPReservePrices;
import algorithms.lp.reserveprices.AbstractLPReservePrices;
import algorithms.lp.reserveprices.ComplexReservePricesOnlyConnected;
import algorithms.lp.reserveprices.SimpleReservePricesAllConnected;
import algorithms.lp.reserveprices.SimpleReservePricesOnlyConnected;
import unitdemand.MWBMatchingAlgorithm;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;
import unitdemand.evpapprox.AbstractMaxWEQReservePrices;
import unitdemand.evpapprox.OnlyConnectedDummies;
import unitdemand.evpapprox.Plus1ConnectedDummies;
import util.Printer;

/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
	
	public static void main(String[] args) throws IloException {//throws IloException{
		for(int i=2;i<20;i++){
			for(int j=2;j<20;j++){
				for(int p=0;p<4;p++){
					//double prob = 0.25 + p*(0.25);
					double prob = 1.0;
					Market market = MarketFactory.randomUnitDemandMarket(i, j, prob);
					System.out.println(market);
					double [][] valuationMatrix = UnitDemandExperiments.getValuationMatrixFromMarket(market);
					/*valuationMatrix = new double[3][2];
					valuationMatrix[0][0] = 39.92;
					valuationMatrix[0][1] = Double.NEGATIVE_INFINITY;

					valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
					valuationMatrix[1][1] = 43.51;

					valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
					valuationMatrix[2][1] = 43.51;					
					market = MarketFactory.createMarketFromValuationMatrix(valuationMatrix);
					System.out.println(market);*/
					EVPApproximation EVPAllConnected = new EVPApproximation(valuationMatrix,new AllConnectedDummies());
					Matching allConnected = EVPAllConnected.Solve();
					System.out.println(allConnected.getSellerRevenue());
					Printer.printMatrix(allConnected.getMatching());
					Printer.printVector(allConnected.getPrices());
					
					SimpleReservePricesAllConnected SRPAllConnected = new SimpleReservePricesAllConnected(market);
					MarketPrices LPRP = SRPAllConnected.Solve();
					System.out.println(LPRP.sellerRevenuePriceVector());
					Printer.printMatrix(LPRP.getMarketAllocation().getAllocation());
					Printer.printVector(LPRP.getPriceVector());
					
					if(LPRP.sellerRevenuePriceVector() - allConnected.getSellerRevenue()  >= 0.1){
						System.out.println("LPRP was better or equal than EVP");
						System.exit(-1);
					}
				}
			}
		}
	}
}
