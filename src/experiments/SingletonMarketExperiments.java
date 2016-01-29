package experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import singletonmarket.HungarianAlgorithm;
import singletonmarket.MaxWEQ;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import structures.MarketPrices;
import util.Printer;

public class SingletonMarketExperiments {
	
	
	protected static double[][] getCostMatrixFromMarket(Market market){
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
	
	
	public static void main(String[] args){
		int numUsers = 21 , startUsers = 14;
		int numCampaigns = 21;
		int numTrials = 100;
		
		for(int i=startUsers;i<numUsers;i++){
			for(int j=2;j<numCampaigns;j++){
				for(int p=0;p<4;p++){
					double prob = 0.25 + p*(0.25);
					System.out.println(" n = " + i + ", m = " + j + ", prob = " + prob);
					DescriptiveStatistics MaxWEQRevenue = new DescriptiveStatistics();
					DescriptiveStatistics LPRevenue = new DescriptiveStatistics();
					DescriptiveStatistics LPViolations = new DescriptiveStatistics();
					for(int t=0;t<numTrials;t++){
						/*
						 * Create a cost matrix for the market.
						 */
						Market market = MarketFactory.randomMarket(i, j, prob);
						double [][] costMatrix = SingletonMarketExperiments.getCostMatrixFromMarket(market);
				        //Printer.printMatrix(costMatrix);
				        HungarianAlgorithm H = new HungarianAlgorithm(costMatrix);
				        int[] result = H.execute();
				        /* Initialize allocation as all disconnected */
				        //boolean[][] hungarianAllocation = new boolean[market.getNumberUsers()][market.getNumberCampaigns()];
				        int[][] hungarianAllocationInteger = new int[market.getNumberUsers()][market.getNumberCampaigns()];
				        for(int i1=0;i1<market.getNumberUsers();i1++){
				        	for(int j1=0;j1<market.getNumberCampaigns();j1++){
				        		//hungarianAllocation[i1][j1] = false;
				        		hungarianAllocationInteger[i1][j1] = 0;
				        	}
				        }
				        double[] rewards = new double[market.getNumberCampaigns()];
				        for(int i1=0;i1<result.length;i1++){
				        	//If the assignment is possible and the user is actually connected to the campaigns
				        	if(result[i1] > -1 && market.isConnected(i1, result[i1])){
				            	rewards[result[i1]] = -1.0 * costMatrix[i1][result[i1]];        		
				            	//hungarianAllocation[i1][result[i1]] = true;
				            	hungarianAllocationInteger[i1][result[i1]] = 1;
				        	}
				        }
				        //System.out.println("Hungarian Allocation:");
				        //Printer.printMatrix(hungarianAllocation);
				        //Printer.printMatrix(hungarianAllocationInteger);
				        MaxWEQ maxWEQ = new MaxWEQ(costMatrix);
				        //System.out.println("maxWEQ Revenue = " + maxWEQ.MaxWEQSellerRevenue());
				        
				        Market inputMarket = MarketFactory.singletonMarket(market.getNumberUsers(), market.getNumberCampaigns(), market.getConnections(), rewards);
				        //System.out.println(inputMarket);
				
				        MarketAllocation marketHungarianAllocation = new MarketAllocation(inputMarket, hungarianAllocationInteger);
				        EnvyFreePricesSolutionLP VectorSol = new EnvyFreePricesVectorLP(marketHungarianAllocation).Solve();
						if(VectorSol.getStatus() == "Optimal"){
							//System.out.print("======LP - Vector:======");
							//System.out.println("\nSeller Revenue = " + VectorSol.sellerRevenuePriceVector());
							//VectorSol.printPricesVector();
							int violations = 0;
							for(int i1=0;i1<marketHungarianAllocation.getMarket().getNumberUsers();i1++){
								if(marketHungarianAllocation.allocationFromUser(i1) == 0 && VectorSol.getPriceVector(i1) > 0){
									violations++;
								}
							}
							//System.out.println("There were "+violations + " many violations");
							MaxWEQRevenue.addValue(maxWEQ.MaxWEQSellerRevenue());
							LPRevenue.addValue(VectorSol.sellerRevenuePriceVector());
							LPViolations.addValue(violations);
							//System.out.println(maxWEQ.MaxWEQSellerRevenue() + "," + VectorSol.sellerRevenuePriceVector() + "," + violations);
						}
					}
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/gpfs/main/home/eareyanv/workspace/envy-free-prices/results/results-singleton.csv", true)))) {
					    out.println(i + "," + j + "," + prob + "," + MaxWEQRevenue.getMean() + "," + LPRevenue.getMean() + "," + (LPRevenue.getMean() / MaxWEQRevenue.getMean()) + "," + LPViolations.getMean());
					}catch (IOException e) {
					    //exception handling left as an exercise for the reader
					}
				}
			}
		}
	}
}
