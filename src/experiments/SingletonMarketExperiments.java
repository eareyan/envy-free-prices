package experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import singletonmarket.EVPApproximation;
import singletonmarket.HungarianAlgorithm;
import singletonmarket.Matching;
import singletonmarket.MaxWEQ;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import structures.MarketPrices;
import util.Printer;

public class SingletonMarketExperiments {
	
	/*
	 * Given a market, return a cost matrix to be input to the Hungarian algorithm. 
	 * This matrix encodes the fact that a campaign is not connected to a user by 
	 * assigning an infinite cost. For a connected campaign, we add a random valuation
	 * between 1 and 100 for each edge. 
	 */
	public static double[][] getCostMatrixFromMarket(Market market){
		Random generator = new Random();
		double maxReward = 100.0;
		double minReward = 1.0;
		double[][] costMatrix = new double[market.getNumberUsers()][market.getNumberCampaigns()];        
        for(int i=0;i<market.getNumberUsers();i++){
        	for(int j=0;j<market.getNumberCampaigns();j++){
        		if(market.isConnected(i, j)){
        			costMatrix[i][j] =-1.0 * (generator.nextDouble() * (maxReward - minReward) + minReward);
        		}else{
        			costMatrix[i][j] = Double.MAX_VALUE;
        		}
        	}
        }
        return costMatrix;
	}
	
	/*
	 * Given a cost matrix, run the Hungarian algorithm and return the max weight matching.
	 */
	public static Matching getMaximumMatchingFromCostMatrix(double[][] costMatrix){
		int[] result = new HungarianAlgorithm(costMatrix).execute();
        /* Initialize allocation as all disconnected */
        int[][] hungarianAllocationInteger = new int[costMatrix.length][costMatrix[0].length];
        for(int i=0;i<costMatrix.length;i++){
        	for(int j1=0;j1<costMatrix[0].length;j1++){
        		hungarianAllocationInteger[i][j1] = 0;
        	}
        }
        double[] rewards = new double[costMatrix[0].length];
        for(int i=0;i<result.length;i++){
        	//If the assignment is possible and the user is actually connected to the campaigns
        	if(result[i] > -1 && costMatrix[i][result[i]] < Double.MAX_VALUE){
            	rewards[result[i]] = -1.0 * costMatrix[i][result[i]];        		
            	hungarianAllocationInteger[i][result[i]] = 1;
        	}
        }
        return new Matching(rewards,hungarianAllocationInteger);
	}
	
	public static void main(String[] args){
		/*
		 * This method test the single demand case.
		 * We test MaxWEQ, EVPApprox and LP.
		 * We record revenue and execution time in each case.
		 * For the LP, we record the number of violations to the Walrasian Equilibirum
		 * and the number of violations of the Envy Free condition in the general case.
		 */
		int numUsers = 21 , startUsers = 5;
		int numCampaigns = 21;
		int numTrials = 100;
		
		for(int i=startUsers;i<numUsers;i++){
			for(int j=2;j<numCampaigns;j++){
				for(int p=0;p<4;p++){
					//double prob = 0.25 + p*(0.25);
					double prob = 1.0;
					System.out.println(" n = " + i + ", m = " + j + ", prob = " + prob);
					DescriptiveStatistics maxWEQRevenue = new DescriptiveStatistics();
					DescriptiveStatistics evpAppRevenue = new DescriptiveStatistics();
					DescriptiveStatistics lpRevenue = new DescriptiveStatistics();
					DescriptiveStatistics lpWEViolations = new DescriptiveStatistics();
					DescriptiveStatistics lpEFViolations = new DescriptiveStatistics();
					for(int t=0;t<numTrials;t++){
						/*
						 * Create random market. Then get cost matrix for the market. 
						 * Next, get the maximum matching allocation.
						 */
						Market market = MarketFactory.randomMarket(i, j, prob);
						double [][] costMatrix = SingletonMarketExperiments.getCostMatrixFromMarket(market);
				        //Printer.printMatrix(costMatrix);
				        Matching M = getMaximumMatchingFromCostMatrix(costMatrix);
				        int[][] maximumMatchingAllocation = M.getMatching();
				        double[] rewards = M.getPrices();
				        //Printer.printMatrix(maximumMatchingAllocation);
				        MaxWEQ maxWEQ = new MaxWEQ(costMatrix);
				        EVPApproximation evpApp = new EVPApproximation(costMatrix);
				        /* This new market is going to be used as input to our algorithm. */
				        Market inputMarket = MarketFactory.singletonMarket(market.getNumberUsers(), market.getNumberCampaigns(), market.getConnections(), rewards);
				        //System.out.println(inputMarket);
				        MarketAllocation marketMaxMatchingAllocation = new MarketAllocation(inputMarket, maximumMatchingAllocation);
				        EnvyFreePricesSolutionLP VectorSol = new EnvyFreePricesVectorLP(marketMaxMatchingAllocation).Solve();
						if(VectorSol.getStatus() == "Optimal"){
							//System.out.print("======LP - Vector:======");
							//System.out.println("\nSeller Revenue = " + VectorSol.sellerRevenuePriceVector());
							//VectorSol.printPricesVector();
							//System.out.println("There were "+violations + " many violations");
							maxWEQRevenue.addValue(maxWEQ.Solve().getSellerRevenue());
							evpAppRevenue.addValue(evpApp.Solve().getSellerRevenue());
							lpRevenue.addValue(VectorSol.sellerRevenuePriceVector());
							lpWEViolations.addValue(VectorSol.computeWalrasianEqViolations());
							lpEFViolations.addValue(VectorSol.numberOfEnvyCampaigns());
							//System.out.println(maxWEQ.MaxWEQSellerRevenue() + "," + VectorSol.sellerRevenuePriceVector() + "," + violations);
						}
					}
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/gpfs/main/home/eareyanv/workspace/envy-free-prices/results/results-singleton.csv", true)))) {
						//out.println(i + "," + j + "," + prob + "," + MaxWEQRevenue.getMean() + "," + LPRevenue.getMean() + "," + (LPRevenue.getMean() / MaxWEQRevenue.getMean()) + "," + LPViolations.getMean());
						System.out.println(i + "," + j + "," + prob + "," + maxWEQRevenue.getMean() + "," +evpAppRevenue.getMean()+ "," + lpRevenue.getMean() + "," + lpWEViolations.getMean() + "," + lpEFViolations.getMean());
						//System.exit(-1);
					}catch (IOException e) {
					    //exception handling left as an exercise for the reader
					}
				}
			}
		}
	}
}
