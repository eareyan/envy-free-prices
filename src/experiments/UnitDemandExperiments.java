package experiments;

import java.sql.SQLException;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import log.SqlDB;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import unitdemand.EVPApproximation;
import unitdemand.HungarianAlgorithm;
import unitdemand.Matching;
import unitdemand.MaxWEQ;

/*
 * This class implements experiments in the case of unit demand.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitDemandExperiments extends Experiments{
	
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
	
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, SqlDB dbLogger) throws SQLException{
		/*
		 * This method test the single demand case.
		 * We test MaxWEQ, EVPApprox and LP.
		 * We record revenue and execution time in each case.
		 * For the LP, we record the number of violations to the Walrasian Equilibirum
		 * and the number of violations of the Envy Free condition in the general case.
		 */		
		if(!dbLogger.checkIfUnitDemandRowExists("unit_demand",numUsers, numCampaigns, prob)){
			System.out.println("Add data for -- n = " + numUsers + ", m = " + numCampaigns + ", prob = " + prob);
			DescriptiveStatistics maxWEQRevenue = new DescriptiveStatistics();
			//DescriptiveStatistics evpAppRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpWEViolations = new DescriptiveStatistics();
			DescriptiveStatistics lpEFViolations = new DescriptiveStatistics();
			
			DescriptiveStatistics maxWEQTime = new DescriptiveStatistics();
			//DescriptiveStatistics evpAppTime = new DescriptiveStatistics();
			DescriptiveStatistics lpTime = new DescriptiveStatistics();
			long startTime , endTime ;
			for(int t=0;t<RunParameters.numTrials;t++){
				/*
				 * Create random market. Then get cost matrix for the market. 
				 * Next, get the maximum matching allocation.
				 */
				Market market = MarketFactory.randomMarket(numUsers, numCampaigns, prob);
				double [][] costMatrix = UnitDemandExperiments.getCostMatrixFromMarket(market);
		        //Printer.printMatrix(costMatrix);
		        Matching M = getMaximumMatchingFromCostMatrix(costMatrix);
		        int[][] maximumMatchingAllocation = M.getMatching();
		        double[] rewards = M.getPrices();
		        //Printer.printMatrix(maximumMatchingAllocation);
		        MaxWEQ maxWEQ = new MaxWEQ(costMatrix);
		        //EVPApproximation evpApp = new EVPApproximation(costMatrix);
		        /* 
		         * This new market is a unit-demand and supply market with rewards given
		         * by the maximum matching allocation. This market is going to be used as input to our algorithm. 
		         */
		        Market inputMarket = MarketFactory.singletonMarket(market.getNumberUsers(), market.getNumberCampaigns(), market.getConnections(), rewards);
		        //System.out.println(inputMarket);
		        MarketAllocation marketMaxMatchingAllocation = new MarketAllocation(inputMarket, maximumMatchingAllocation);
				/*
				 * Measure lpApp
				 */
				startTime = System.nanoTime();
		        //EnvyFreePricesSolutionLP VectorSol = new EnvyFreePricesVectorLP(marketMaxMatchingAllocation).Solve();
				//lpRevenue.addValue(VectorSol.sellerRevenuePriceVector());
				lpRevenue.addValue(1);
				endTime = System.nanoTime();
				lpTime.addValue(endTime - startTime);
				/*
				 * Measure violations
				 */
				//lpWEViolations.addValue(VectorSol.computeWalrasianEqViolations());
				//lpEFViolations.addValue(VectorSol.numberOfEnvyCampaigns());
				lpWEViolations.addValue(1);
				lpEFViolations.addValue(1);
				/*
				 * Measure maxEQ
				 */
				startTime = System.nanoTime();
				maxWEQRevenue.addValue(maxWEQ.Solve().getSellerRevenue());
				endTime = System.nanoTime();
				maxWEQTime.addValue(endTime - startTime);
				/*
				 * Measure evpApp
				 
				startTime = System.nanoTime();
				evpAppRevenue.addValue(evpApp.Solve().getSellerRevenue());
				endTime = System.nanoTime();
				evpAppTime.addValue(endTime - startTime);
				 */
			}
			/* log results in database */
			dbLogger.saveUnitDemandData(numUsers, numCampaigns, prob, maxWEQRevenue.getMean(), maxWEQTime.getMean() / 1000000, lpRevenue.getMean(), lpTime.getMean() / 1000000 , lpWEViolations.getMean(), lpEFViolations.getMean());
		}else{
			System.out.println("Already have data for -- n = " + numUsers + ", m = " + numCampaigns + ", prob = " + prob);
		}
	}	
}
