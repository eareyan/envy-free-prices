package test;

import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.Waterfall;
import algorithms.WaterfallPrices;
import algorithms.allocations.EfficientAllocationILP;
import algorithms.allocations.GreedyAllocation;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import structures.Campaign;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.User;
import structures.factory.MarketAllocationFactory;
import structures.factory.MarketFactory;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import unitdemand.ascendingauction.AscendingAuction;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;
import util.Printer;
import experiments.RunParameters;
import experiments.UnitDemandComparison;
import experiments.UnitDemandExperiments;
//import ilog.concert.IloException;
//import ilog.cplex.IloCplex;

/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
	
	public static void main1(String[] args) throws IloException{
		System.out.println("Testing Waterfall");
		/*Market market = MarketFactory.randomMarket(3,3,1.0);
		System.out.println(market);
		Waterfall WF = new Waterfall(market);
		WaterfallPrices WFSol = WF.Solve();
		Printer.printMatrix(WFSol.getMarketAllocation().getAllocation());*/
		
		int numCamp = 30;
		int numUser = 5;
		
		numUser = numCamp;
		Campaign[] campaigns = new Campaign[numCamp];
		for(int j=0;j<numCamp;j++){
			campaigns[j] = new Campaign(2  /* demand */ ,2*(numCamp - j)/* reward */);
		}
		User[] users = new User[numUser];
		for(int i=0;i<numUser;i++){
			users[i] = new User(1);
		}
		boolean[][] connections = new boolean[numUser][numCamp];
		int counter = 0;
		//connections[0][0] = true;
		//connections[0][numUser-1] = true;
		connections[numUser-1][numUser-1] = true;
		for(int i=0;i<numUser-1;i++){
			connections[i][counter] = true;
			counter++;
			connections[i][counter] = true;
		}
		/*connections[0][0] = true;
		connections[1][0] = true;
		connections[1][1] = true;
		connections[2][1] = true;
		connections[2][2] = true;
		//connections[3][2] = true;
		connections[3][3] = true;*/
		
		Market weirdMarket = new Market(users,campaigns,connections);
		System.out.println(weirdMarket);
		Waterfall WF = new Waterfall(weirdMarket);
		WaterfallPrices WFSol = WF.Solve();
		System.out.println("WF Alloc");
		//Printer.printMatrix(WFSol.getMarketAllocation().getAllocation());	
		System.out.println(WFSol.getMarketAllocation().value());
		
		MarketAllocation y = new MarketAllocation(weirdMarket,new EfficientAllocationILP(weirdMarket).Solve(new IloCplex()).get(0));
		System.out.println("Efficient Alloc");
		//Printer.printMatrix(y.getAllocation());
		System.out.println(y.value());
		
		System.out.println(WFSol.getMarketAllocation().value() / y.value());
	}
	
	
	public static void main2(String[] args) throws IloException {//throws IloException{
		Market market = MarketFactory.randomUnitDemandMarket(3, 3, 0.5);
		unitdemand.lp.LPReservePrices LP = new unitdemand.lp.LPReservePrices(market);
		LP.Solve();
	}
	public static void main3(String[] args){
		for(int j=2;j<20;j++){
			for(int i=2;i<20;i++){
				for(int p=0;p<4;p++){
					for(int k=0;k<RunParameters.numTrials;k++){
					double prob = 0.25 + p*(0.25);		
		System.out.println("Compare MaxWEQ with LP in uniform-unit demand case");
		Market market = MarketFactory.randomUnitDemandMarket(i, j, prob);
		System.out.println(market);
		double [][] valuationMatrix = MarketAllocationFactory.getValuationMatrixFromMarket(market);		
		
		Printer.printMatrix(valuationMatrix);
		MaxWEQ maxWEQ = new MaxWEQ(valuationMatrix);
		Matching maxWEQSol = maxWEQ.Solve();
		System.out.println("1)------MaxWEQ Solution");
		Printer.printMatrix(maxWEQSol.getMatching());
		Printer.printVector(maxWEQSol.getPrices());
		System.out.println(maxWEQSol.getSellerRevenue());
		
		//EnvyFreePricesVectorLP efpvLP = new EnvyFreePricesVectorLP(MarketAllocationFactory.getMaxWeightMatchingAllocation(market));
		EnvyFreePricesVectorLP efpvLP = new EnvyFreePricesVectorLP(MarketAllocationFactory.getMaxWeightMatchingAllocation(MarketFactory.createMarketFromValuationMatrix(valuationMatrix)));
		efpvLP.createLP();
		EnvyFreePricesSolutionLP efpvSol = efpvLP.Solve();
		System.out.println("2)------efPVLP Solution");
		if(efpvSol.getMarketAllocation()!=null){
		Printer.printMatrix(efpvSol.getMarketAllocation().getAllocation());
		Printer.printVector(efpvSol.getPriceVector());
		System.out.println(efpvSol.sellerRevenuePriceVector());
		if(maxWEQSol.getSellerRevenue() != efpvSol.sellerRevenuePriceVector()){
			System.out.println("DIFFERENT");
			System.exit(-1);
		}
		}
				}
			}
		}
		}
		
	}
	
	public static void main(String[] args){
		double[][] valuationMatrix = UnitDemandComparison.getValuationMatrix(2, 3, 0.5);
		Market m = MarketFactory.createMarketFromValuationMatrix(valuationMatrix);
		System.out.println(m);
		valuationMatrix = MarketAllocationFactory.getValuationMatrixFromMarket(m);
		AscendingAuction A = new AscendingAuction(valuationMatrix);
		A.Solve();
	}
	
	public static void main7(String[] args) throws IloException{
		Market market = MarketFactory.randomMarket(3, 3, 1.0);
		System.out.println(market);
		

		MarketAllocation efficient = new MarketAllocation(market,new EfficientAllocationILP(market).Solve(new IloCplex()).get(0));
		Printer.printMatrix(efficient.getAllocation());
		System.out.println();

		GreedyAllocation G0 = new GreedyAllocation(market,0);
		MarketAllocation greedy0 = G0.Solve();
		Printer.printMatrix(greedy0.getAllocation());

		GreedyAllocation G1 = new GreedyAllocation(market,1);
		MarketAllocation greedy1 = G1.Solve();
		Printer.printMatrix(greedy1.getAllocation());

		GreedyAllocation G2 = new GreedyAllocation(market,-1);
		MarketAllocation greedy2 = G2.Solve();
		Printer.printMatrix(greedy2.getAllocation());

		
		
		/* System.out.println(greedy.value() / efficient.value());
		WaterfallPrices waterFallAllocationPrices = new Waterfall(market).Solve();		
		System.out.println();
		Printer.printMatrix(waterFallAllocationPrices.getMarketAllocation().getAllocation());
		System.out.println(waterFallAllocationPrices.getMarketAllocation().value() / efficient.value());
		
		
		MarketAllocation efficient1 = new MarketAllocation(market,new EfficientAllocationLP(market).Solve(new IloCplex()).get(0));
		MarketAllocation greedy1 = new GreedyAllocation(market).Solve();
		MarketAllocation wf1 = new Waterfall(market).Solve().getMarketAllocation();	
		System.out.println(wf1.value() / greedy1.value());
		System.out.println(greedy1.value() / efficient1.value());
		System.out.println(wf1.value() / efficient1.value());*/
		
		
	}
	
	public static void main5(String[] args) throws IloException{
		System.out.println("Quadratic Programming Testing:");
		//Market market = MarketFactory.randomMarket(2, 2, 1.0);
		//Market market = MarketFactory.randomUnitDemandMarket(3, 5, 0.5);
		

		
		
		/*Campaign c1 = new Campaign(2, 25);
		Campaign c2 = new Campaign(3, 45);
		Campaign c3 = new Campaign(2, 25);
		Campaign[] campaigns = new Campaign[3];
		campaigns[0] = c1;
		campaigns[1] = c2;
		campaigns[2] = c3;
		
		User u1 = new User(2);
		User u2 = new User(2);
		User[] users = new User[2];
		users[0] = u1;
		users[1] = u2;
		
		boolean[][] connections = new boolean[2][3];
		connections[0][0] = true;
		connections[0][1] = true;
		
		connections[1][1] = true;
		
		connections[1][2] = true;
		
		Market market = new Market(users,campaigns,connections);*/
		

		Campaign c1 = new Campaign(1, 1);
		Campaign c2 = new Campaign(1, 10);
		Campaign c3 = new Campaign(1, 9);
		Campaign[] campaigns = new Campaign[3];
		campaigns[0] = c1;
		campaigns[1] = c2;
		campaigns[2] = c3;
		
		User u1 = new User(1);
		User u2 = new User(1);
		User[] users = new User[2];
		users[0] = u1;
		users[1] = u2;
		
		boolean[][] connections = new boolean[2][3];
		connections[0][0] = true;
		connections[0][1] = true;
		
		connections[1][0] = true;
		connections[1][1] = true;
		
		connections[1][2] = true;
		
		Market market = new Market(users,campaigns,connections);
		
				System.out.println(market);
		
		int[][] efficientAllocation = new EfficientAllocationILP(market).Solve(new IloCplex()).get(0);
		Printer.printMatrix(efficientAllocation);
		MarketAllocation EffAlloc = new MarketAllocation(market,efficientAllocation);
		EnvyFreePricesVectorLP efpvLP = new EnvyFreePricesVectorLP(EffAlloc);
		efpvLP.createLP();
		Printer.printVector(efpvLP.Solve().getPriceVector());
		System.out.println(EffAlloc.value());
		
		GreedyAllocation G0 = new GreedyAllocation(market,0);
		MarketAllocation greedy0 = G0.Solve();
		Printer.printMatrix(greedy0.getAllocation());
		System.out.println(greedy0.value());
		
	}
	public static void main0(String[] args) throws IloException{
		double reservePrice =10.0;
		System.out.println("Efficient allocation with reserve price of " + reservePrice);
		//Market market = MarketFactory.randomUnitDemandMarket(3, 3, 1.0);
		Market market = MarketFactory.randomMarket(1, 3, 1.0);
		System.out.println(market);
		int[][] efficientAllocation = new EfficientAllocationILP(market).Solve(new IloCplex()).get(0);
		Printer.printMatrix(efficientAllocation);
		System.out.println();
		int[][] efficientAllocationReservePrice = new EfficientAllocationILP(market,reservePrice).Solve(new IloCplex()).get(0);
		Printer.printMatrix(efficientAllocationReservePrice);
		/*System.out.println();
		Printer.printMatrix(MarketAllocationFactory.getMaximumMatchingFromValuationMatrix(MarketAllocationFactory.getValuationMatrixFromMarket(market)));
		System.out.println();		
		Printer.printMatrix(MarketAllocationFactory.getAllocationWithReservePrices(MarketAllocationFactory.getValuationMatrixFromMarket(market), reservePrice));*/
		
	}
	public static void main4(String[] args) throws IloException {//throws IloException{
		for(int i=2;i<20;i++){
			for(int j=2;j<20;j++){
				for(int p=0;p<4;p++){
					///for(int k=0;k<RunParameters.numTrials;k++){
					double prob = 0.25 + p*(0.25);
					//double prob = 1.0;
					System.out.println("(i,j,p) = (" + i + "," + j + "," + prob + ")");
					Market market = MarketFactory.randomUnitDemandMarket(i, j, prob);
					//System.out.println(market);
					double [][] valuationMatrix = MarketAllocationFactory.getValuationMatrixFromMarket(market);
					valuationMatrix = new double[3][2];
					valuationMatrix[0][0] = 39.92;
					valuationMatrix[0][1] = Double.NEGATIVE_INFINITY;

					valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
					valuationMatrix[1][1] = 43.51;

					valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
					valuationMatrix[2][1] = 43.51;					
					market = MarketFactory.createMarketFromValuationMatrix(valuationMatrix);
					System.out.println(market);
					EVPApproximation EVPAllConnected = new EVPApproximation(valuationMatrix,new AllConnectedDummies());
					Matching evpAllConnected = EVPAllConnected.Solve();
					System.out.println("evpAllConnected revenue = \t"+evpAllConnected.getSellerRevenue());
					//Printer.printMatrix(evpAllConnected.getMatching());
					//Printer.printVector(evpAllConnected.getPrices());
					
					/*MaxWEQ maxWEQ = new MaxWEQ(valuationMatrix);
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
