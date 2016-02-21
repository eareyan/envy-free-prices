package test;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.EnvyFreePricesVectorLPReservePrices;
import algorithms.GeneralApproximation;
import algorithms.GeneralApproximation1;
import algorithms.Waterfall;
import algorithms.WaterfallMAXWEQ;
import algorithms.WaterfallPrices;
import experiments.UnitDemandExperiments;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import structures.MarketPrices;
import unitdemand.EVPApproximation;
import unitdemand.HungarianAlgorithm;
import unitdemand.Idea1;
import unitdemand.LPReservePrices;
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
		
		for(int i=3;i<20;i++){
			for(int j=3;j<20;j++){
				for(int p=0;p<4;p++){
					double prob = 0.25 + p*(0.25);
		
		Market randomMarket = MarketFactory.randomMarket(i,j, prob);
		//System.out.println(randomMarket);
		GeneralApproximation G = new GeneralApproximation(randomMarket,true);
		//System.out.print(G.Solve() + " ");
		GeneralApproximation G1 = new GeneralApproximation(randomMarket,false);
		//System.out.print(G1.Solve() + " ");
		GeneralApproximation1 G2 = new GeneralApproximation1(randomMarket,true);
		//System.out.print(G2.Solve() + " ");
		GeneralApproximation1 G3 = new GeneralApproximation1(randomMarket,false);
		//System.out.print(G3.Solve() + " ");
		
		System.out.println(((EnvyFreePricesSolutionLP) G2.Solve()).getOptimalValue() / ((EnvyFreePricesSolutionLP) G.Solve()).getOptimalValue());
		
		EnvyFreePricesVectorLP EFP = new EnvyFreePricesVectorLP(new Waterfall(randomMarket).Solve().getMarketAllocation(),new IloCplex());
		EFP.createLP();
		//System.out.println(EFP.Solve());
		//System.out.println(((EnvyFreePricesSolutionLP) EFP.Solve()).getOptimalValue() / ((EnvyFreePricesSolutionLP) G1.Solve()).getOptimalValue());

				}
			}
		}
		/*Market randomUnitDemandMarket = MarketFactory.randomUnitDemandMarket(3, 7, 1.0);
		//System.out.println(randomUnitDemandMarket);
		GeneralApproximation G2 = new GeneralApproximation(randomUnitDemandMarket);
		G2.Solve();
		GeneralApproximation1 G3 = new GeneralApproximation1(randomUnitDemandMarket);
		G3.Solve();*/
		
		
		/*System.out.println(">>>>>>>>>>>>>>>>>Watefall MaxWEQ");
		WaterfallMAXWEQ wfMaxWEQ = new WaterfallMAXWEQ(randomMarket);
		MarketPrices p = wfMaxWEQ.Solve();
		System.out.println("wfMaxWEQ Seller Revenue = " + p.sellerRevenuePriceVector());
		Printer.printVector(p.getPriceVector());
		Printer.printMatrix(p.getMarketAllocation().getAllocation());
		System.out.println("numberOfEnvyCampaigns = " + p.numberOfEnvyCampaigns());
		
		//System.exit(-1);
		System.out.println("<<<<<<<<<<<<<<<<<<<General Approximation");
		GeneralApproximation generalApp = new GeneralApproximation(randomMarket);
		generalApp.Solve();*/
		
		
		/*Market randomMarket = MarketFactory.randomMarket(3, 3, 0.5);
		System.out.println(randomMarket);
		WaterfallMAXWEQ wfMaxWEQ = new WaterfallMAXWEQ(randomMarket);
		Printer.printVector(wfMaxWEQ.Solve().getPriceVector());*/
		
		//for(int i=2;i<20;i++){
		//	for(int j=2;j<20;j++){
		//		for(int p=0;p<4;p++){
		//			double prob = 0.25 + p*(0.25);
		//			Market market = MarketFactory.randomUnitDemandMarket(i, j, prob);
					//Market market = MarketFactory.randomUnitDemandMarket(3, 2, 0.5);
					
					
		//			double [][] valuationMatrix = UnitDemandExperiments.getValuationMatrixFromMarket(market);
					
					/*valuationMatrix[0][0] = 31.622638835480853;
					valuationMatrix[0][1] = Double.NEGATIVE_INFINITY;
					valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
					valuationMatrix[1][1] = 99.68128095651791;*/
					
		//			Printer.printMatrix(valuationMatrix);
					//Market market = MarketFactory.randomMarket(3, 3, 0.5);
					//System.out.println(market);					
		//			EVPApproximation evpApp = new EVPApproximation(valuationMatrix);
		///			evpApp.Solve();
					/*WaterfallPrices waterFallAllocationPricesMin = new Waterfall(market).Solve();
					WaterfallPrices waterFallAllocationPricesMax = new Waterfall(market,false).Solve();
					Printer.printMatrix(waterFallAllocationPricesMin.getMarketAllocation().getAllocation());
					System.out.println("******");
					Printer.printMatrix(waterFallAllocationPricesMax.getMarketAllocation().getAllocation());*/
					/*
					double [][] valuationMatrix = UnitDemandExperiments.getValuationMatrixFromMarket(market);
					//System.out.print(i+","+j + "," + prob);
					//System.out.println(market);
	
					EVPApproximation evpApp = new EVPApproximation(valuationMatrix);
					evpApp.Solve();
					
					LPReservePrices lpReservePrices = new LPReservePrices(market);
					lpReservePrices.Solve();*/
				//}
			//}
		//}
		
		/*
		 MaxWEQ maxWEQ = new MaxWEQ(valuationMatrix);
		
		Printer.printMatrix(valuationMatrix);
		/* First, get the maximum matching allocation.*/
       /* int[][] maximumMatchingAllocation = UnitDemandExperiments.getMaximumMatchingFromValuationMatrix(valuationMatrix);
        MarketAllocation marketMaxMatchingAllocation = new MarketAllocation(market, maximumMatchingAllocation);
       Printer.printMatrix(maximumMatchingAllocation);
        IloCplex iloObject;
		try {
			iloObject = new IloCplex();
	        EnvyFreePricesSolutionLP VectorSol = new EnvyFreePricesVectorLP(marketMaxMatchingAllocation).Solve(iloObject);
	        Printer.printVector(VectorSol.getPriceVector());
	        Printer.printVector(maxWEQ.Solve().getPrices());
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}
}
