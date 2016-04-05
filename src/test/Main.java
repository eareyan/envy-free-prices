package test;

//import ilog.concert.IloException;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import statistics.PricesStatistics;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.factory.MarketAllocationFactory;
import structures.factory.MarketFactory;
import structures.factory.RandomMarketFactory;
import structures.factory.UnitMarketFactory;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;
import util.Printer;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.allocations.EfficientAllocationILP;
import algorithms.allocations.GreedyAllocation;
import algorithms.ascendingauction.AscendingAuction;
import algorithms.pricing.lp.reserveprices.GeneralLPReserve;
import algorithms.pricing.lp.reserveprices.SelectAllConnectedUsers;
import experiments.RunParameters;
/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
	
	public static void main(String[] args) throws IloException{
		Market market = RandomMarketFactory.generateOverDemandedMarket(3, 7, 0.25, 2);
		System.out.println(market);
		MarketAllocation efficient = new MarketAllocation(market,new EfficientAllocationILP(market).Solve(new IloCplex()).get(0));
		/*double valueOptAllocaction = efficient.value();
		Printer.printMatrix(efficient.getAllocation());
		System.out.println("valueOptAllocaction = " + valueOptAllocaction);*/
		
		/*algorithms.ascendingauction.AscendingAuction ck = new algorithms.ascendingauction.AscendingAuction(market);
		MarketPrices ckSol = ck.Solve();
		Printer.printMatrix(ckSol.getMarketAllocation().getAllocation());
		Printer.printVector(ckSol.getPriceVector());
		System.out.println(ckSol.getMarketAllocation().value());
		PricesStatistics ckStat = new PricesStatistics(ckSol);
		System.out.println(ckStat.numberOfEnvyCampaigns());
		System.out.println(ckStat.computeWalrasianViolations()[0]);*/
		
		System.out.println("GeneralLPReserve");
		
		GeneralLPReserve lpr = new GeneralLPReserve(market,efficient);
		MarketPrices bla = lpr.Solve();
		Printer.printMatrix(bla.getMarketAllocation().getAllocation());
		Printer.printVector(bla.getPriceVector());
		System.out.println("value = " + bla.getMarketAllocation().value());
		PricesStatistics lprStat = new PricesStatistics(bla);
		System.out.println(lprStat.numberOfEnvyCampaigns());
		System.out.println(lprStat.computeWalrasianViolations()[0]);
		
		/*System.out.println("Regular LP");
		EnvyFreePricesVectorLP e = new EnvyFreePricesVectorLP(efficient);
		e.setWalrasianConditions(false);
		e.createLP();
		EnvyFreePricesSolutionLP x = e.Solve();
		Printer.printMatrix(x.getMarketAllocation().getAllocation());
		Printer.printVector(x.getPriceVector());
		System.out.println(x.sellerRevenuePriceVector());*/
		
	}
	
	
	public static void main7(String[] args) throws IloException{
		Market market = UnitMarketFactory.randomUnitDemandMarket(7,5,.75);
		System.out.println(market);
		unitdemand.lp.LPReservePrices lp = new unitdemand.lp.LPReservePrices(market);
		MarketPrices lpSol = lp.Solve();
		Printer.printMatrix(lpSol.getMarketAllocation().getAllocation());
		Printer.printVector(lpSol.getPriceVector());
		System.out.println("---------LP");
		System.out.println("Seller Revenue = " + lpSol.sellerRevenuePriceVector());
		System.out.println("Value of Matching = " + lpSol.getMarketAllocation().value());
		PricesStatistics p = new PricesStatistics(lpSol);
		System.out.println("There are: "+ p.numberOfEnvyCampaigns() +" envy campaigns");
		System.out.println("There were: "+ p.computeWalrasianViolations()[0] + " many WE violations");
		
		System.out.println("---------EVP");
		double [][] valuationMatrix = MarketAllocationFactory.getValuationMatrixFromMarket(market);
		EVPApproximation evpApp = new EVPApproximation(valuationMatrix , new AllConnectedDummies(valuationMatrix));
		Matching evpSol = evpApp.Solve();
		Printer.printMatrix(evpSol.getMatching());
		Printer.printVector(evpSol.getPrices());
		System.out.println("Seller Revenue = " + evpSol.getSellerRevenue());
		System.out.println("Value of Matching = " + evpSol.getValueOfMatching());
		System.out.println("There are: "+ evpSol.numberOfEnvyCampaigns() +" envy campaigns");
		System.out.println("There were: "+ evpSol.computeWalrasianViolations() + " many WE violations");
		
		System.out.println("---------CK");
		unitdemand.ascendingauction.AscendingAuction A = new unitdemand.ascendingauction.AscendingAuction(valuationMatrix);
		Matching ckSol = A.Solve();
		Printer.printMatrix(ckSol.getMatching());
		Printer.printVector(ckSol.getPrices());
		System.out.println("Seller Revenue = " + ckSol.getSellerRevenue());
		System.out.println("Value of Matching = " + ckSol.getValueOfMatching());
		Printer.printMatrix(valuationMatrix);
		ckSol.numberOfEnvyCampaigns();
		System.out.println("There were: "+ ckSol.computeWalrasianViolations() + " many WE violations");
		
		
		
	}
	
	public static void main3(String[] args) throws Exception{
		/*for(int i=0;i<1000;i++){
			RunParameters R = new RunParameters(new String[]{"grid", "GeneralDemand", " postgresql" , "db.cs.brown.edu", "5432" , "envyfreeresults" , "***" , "***" , i + ""});
			System.out.println(R);
		}*/
		double[][] valuationMatrix = experiments.unit_demand.getValuationMatrix(5, 3, .75);
		Matching maxWeightmatching = Matching.computeMaximumWeightMatchingValue(valuationMatrix);
		Printer.printMatrix(maxWeightmatching.getMatching());
		System.out.println("Value of Matching = " + maxWeightmatching.getValueOfMatching());
		
		
		Printer.printMatrix(valuationMatrix);
		MaxWEQ mweq = new MaxWEQ(valuationMatrix);
		Matching mwSol = mweq.Solve();
		Printer.printMatrix(mwSol.getMatching());
		Printer.printVector(mwSol.getPrices());
		System.out.println("Seller Revenue = " + mwSol.getSellerRevenue());
		System.out.println("Value of Matching = " + mwSol.getValueOfMatching());
		mwSol.numberOfEnvyCampaigns();
		System.out.println("There were: "+ mwSol.computeWalrasianViolations() + " many WE violations");
		
		EVPApproximation evpApp = new EVPApproximation(valuationMatrix , new AllConnectedDummies(valuationMatrix));
		Matching evpSol = evpApp.Solve();
		Printer.printMatrix(evpSol.getMatching());
		Printer.printVector(evpSol.getPrices());
		System.out.println("Seller Revenue = " + evpSol.getSellerRevenue());
		System.out.println("Value of Matching = " + evpSol.getValueOfMatching());
		evpSol.numberOfEnvyCampaigns();
		System.out.println("There were: "+ evpSol.computeWalrasianViolations() + " many WE violations");
		
		unitdemand.ascendingauction.AscendingAuction A = new unitdemand.ascendingauction.AscendingAuction(valuationMatrix);
		Matching ckSol = A.Solve();
		Printer.printMatrix(ckSol.getMatching());
		Printer.printVector(ckSol.getPrices());
		System.out.println("Seller Revenue = " + ckSol.getSellerRevenue());
		System.out.println("Value of Matching = " + ckSol.getValueOfMatching());
		Printer.printMatrix(valuationMatrix);
		ckSol.numberOfEnvyCampaigns();
		System.out.println("There were: "+ ckSol.computeWalrasianViolations() + " many WE violations");
	}
	
	public static void main0(String[] args){
		System.out.println("Market Generation");
		Market m = RandomMarketFactory.RandomKMarket(5,3,1.0,2);
		System.out.println(m);
		System.out.println(m.getSupplyToDemandRatio());
		m = RandomMarketFactory.generateOverDemandedMarket(3,5,.75,1);
		System.out.println(m);
		//m = RandomMarketFactory.generateOverSuppliedMarket(5,20,1.0,3);
		//System.out.println(m);
		System.out.println(m.getSupplyToDemandRatio());
		
		AscendingAuction A = new AscendingAuction(m);
		MarketPrices allocPrices = A.Solve();
		Printer.printMatrix(allocPrices.getMarketAllocation().getAllocation());
		Printer.printVector(allocPrices.getPriceVector());
		PricesStatistics P = new PricesStatistics(allocPrices);
		
		MarketAllocation X = new GreedyAllocation(m).Solve();
		Printer.printMatrix(X.getAllocation());
		
		System.out.println("There are " + P.numberOfEnvyCampaigns() +" many envy campaigns");
	}
	
	
	public static void main1(String[] args) {//throws IloException{
		
		for(int i=3;i<20;i++){
			for(int j=3;j<20;j++){
				for(int p=0;p<4;p++){
					for(int k=0;k<RunParameters.numTrials;k++){
						double prob = 0.25 + p*(0.25);		
						/*Market market = UnitMarketFactory.randomUnitDemandMarket(i, j, prob);
						System.out.println(market);
						MarketAllocation X = MarketAllocationFactory.getMaxWeightMatchingAllocation(market);
						Printer.printMatrix(X.getAllocation());*/
						
						Market market = RandomMarketFactory.randomMarket(i, j, prob);
						System.out.println(market);
						MarketAllocation X = new GreedyAllocation(market).Solve();
						Printer.printMatrix(X.getAllocation());
						
						
						/*EnvyFreePricesSolutionLP efpvLP = new EnvyFreePricesVectorLP(X,true).Solve();
						Printer.printVector(efpvLP.getPriceVector());


						PricesStatistics priceStats = new PricesStatistics(efpvLP);
						int e = priceStats.numberOfEnvyCampaigns();
						double[] weViolations = priceStats.computeWalrasianEqViolations();
						
						System.out.println("numberOfEnvyCampaigns = " + e);
						System.out.println(weViolations[0] + "," + weViolations[1]);
						if(weViolations[0] > 0){
							System.out.println("ENVY!!!!");
							System.exit(-1);
						}*/
					}
				}
			}
		}
	}
	
	
	public static void main2(String[] args){// throws IloException{
		for(int j=2;j<20;j++){
			for(int i=2;i<20;i++){
				for(int p=0;p<4;p++){
					for(int k=0;k<RunParameters.numTrials;k++){
						double prob = 0.25 + p*(0.25);		
						System.out.println("Compare MaxWEQ with LP in uniform-unit demand case");
						Market market = UnitMarketFactory.randomUnitDemandMarket(i, j, prob);
						System.out.println(market);
						double [][] valuationMatrix = MarketAllocationFactory.getValuationMatrixFromMarket(market);		
						
						Printer.printMatrix(valuationMatrix);
						MaxWEQ maxWEQ = new MaxWEQ(valuationMatrix);
						Matching maxWEQSol = maxWEQ.Solve();
						System.out.println("1)------MaxWEQ Solution");
						Printer.printMatrix(maxWEQSol.getMatching());
						Printer.printVector(maxWEQSol.getPrices());
						System.out.println(maxWEQSol.getSellerRevenue());
						
						/*EnvyFreePricesVectorLP efpvLP = new EnvyFreePricesVectorLP(MarketAllocationFactory.getMaxWeightMatchingAllocation(market));
						EnvyFreePricesVectorLP efpvLP = new EnvyFreePricesVectorLP(MarketAllocationFactory.getMaxWeightMatchingAllocation(UnitMarketFactory.createMarketFromValuationMatrix(valuationMatrix)));
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
						}*/
					}
				}
			}
		}
	}
}
