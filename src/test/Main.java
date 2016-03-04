package test;

import ilog.concert.IloException;
import structures.Market;
import structures.MarketFactory;
import structures.MarketPrices;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;
import util.Printer;
import experiments.RunParameters;
import experiments.UnitDemandExperiments;
//import ilog.concert.IloException;
//import ilog.cplex.IloCplex;

/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
	
	public static void main2(String[] args) throws IloException {//throws IloException{
		Market market = MarketFactory.randomUnitDemandMarket(3, 3, 0.5);
		unitdemand.lp.LPReservePrices LP = new unitdemand.lp.LPReservePrices(market);
		LP.Solve();
	}
	public static void main(String[] args) throws IloException {//throws IloException{
		for(int i=2;i<20;i++){
			for(int j=2;j<20;j++){
				for(int p=0;p<4;p++){
					///for(int k=0;k<RunParameters.numTrials;k++){
					double prob = 0.25 + p*(0.25);
					//double prob = 1.0;
					System.out.println("(i,j,p) = (" + i + "," + j + "," + prob + ")");
					Market market = MarketFactory.randomUnitDemandMarket(i, j, prob);
					//System.out.println(market);
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
					Matching evpAllConnected = EVPAllConnected.Solve();
					System.out.println("evpAllConnected revenue = \t"+evpAllConnected.getSellerRevenue());
					//Printer.printMatrix(evpAllConnected.getMatching());
					//Printer.printVector(evpAllConnected.getPrices());
					
					MaxWEQ maxWEQ = new MaxWEQ(valuationMatrix);
					Matching maxWeqSOL= maxWEQ.Solve();
					 System.out.println("MaxWEQ revenue = \t\t" + maxWeqSOL.getSellerRevenue());
					/*if(maxWeqSOL.getSellerRevenue() - evpAllConnected.getSellerRevenue()  >= 0.1){
						System.out.println("MAXWEQ was better or equal than EVP");
						System.exit(-1);
					}*/
					/*algorithms.lp.reserveprices.LPReservePrices SRPAllConnected = new algorithms.lp.reserveprices.LPReservePrices(market,new algorithms.lp.reserveprices.SelectAllConnectedUsers(), new algorithms.lp.reserveprices.SetReservePricesSimple());
					MarketPrices LPRP = SRPAllConnected.Solve();
					System.out.println("LPSRP revenue = \t\t" + LPRP.sellerRevenuePriceVector());*/
					unitdemand.lp.LPReservePrices LPRPUnitDemand = new unitdemand.lp.LPReservePrices(market);
					MarketPrices LPRP = LPRPUnitDemand.Solve();
					System.out.println("LPSRP revenue = \t\t" + LPRP.sellerRevenuePriceVector());
					//Printer.printMatrix(LPRP.getMarketAllocation().getAllocation());
					//Printer.printVector(LPRP.getPriceVector());
					
					if(LPRP.sellerRevenuePriceVector() < evpAllConnected.getSellerRevenue()){
						System.out.println("EVPApp was better or equal than LPRP");
						System.exit(-1);
					}
					//}
				}
			}
		}
	}
}
