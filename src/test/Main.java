package test;

//import ilog.concert.IloException;
import statistics.PricesStatistics;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.factory.MarketAllocationFactory;
import structures.factory.RandomMarketFactory;
import structures.factory.UnitMarketFactory;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import util.Printer;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.allocations.GreedyAllocation;
import algorithms.ascendingauction.AscendingAuction;
import experiments.RunParameters;
/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
	
	public static void main(String[] args){
		System.out.println("Market Generation");
		Market m = RandomMarketFactory.RandomKMarket(3,3,.75,2);
		System.out.println(m);
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
