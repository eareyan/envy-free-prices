package experiments;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.sql.SQLException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import log.SqlDB;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import unitdemand.EVPApproximation;
import unitdemand.MWBMatchingAlgorithm;
import unitdemand.MaxWEQ;
import util.Printer;

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
	public static double[][] getValuationMatrixFromMarket(Market market){
		double[][] costMatrix = new double[market.getNumberUsers()][market.getNumberCampaigns()];        
        for(int i=0;i<market.getNumberUsers();i++){
        	for(int j=0;j<market.getNumberCampaigns();j++){
        		if(market.isConnected(i, j)){
        			costMatrix[i][j] = market.getCampaign(j).getReward();
        		}else{
        			costMatrix[i][j] = Double.NEGATIVE_INFINITY;
        		}
        	}
        }
        return costMatrix;
	}
	
	/*
	 * Given a cost matrix, run the Hungarian algorithm and return the max weight matching.
	 */
	public static int[][] getMaximumMatchingFromValuationMatrix(double[][] valuationMatrix){
		int[] result  = new MWBMatchingAlgorithm(valuationMatrix).getMatching();
        /* Initialize allocation as all disconnected */
        int[][] allocationMatrix = new int[valuationMatrix.length][valuationMatrix[0].length];
        for(int i=0;i<result.length;i++){
        	//If the assignment is possible and the user is actually connected to the campaigns
        	if(result[i] > -1 && valuationMatrix[i][result[i]] > Double.NEGATIVE_INFINITY){
            	allocationMatrix[i][result[i]] = 1;
        	}
        }
        return allocationMatrix;
	}
	
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, SqlDB dbLogger) throws SQLException{
		/*
		 * This method test the single demand case.
		 * We test MaxWEQ, EVPApprox and LP.
		 * We record revenue and execution time in each case.
		 * For the LP, we record the number of violations to the Walrasian Equilibrium
		 * and the number of violations of the Envy Free condition in the general case.
		 */
		if(!dbLogger.checkIfUnitDemandRowExists("unit_demand",numUsers, numCampaigns, prob)){
			System.out.println("\t Adding data ");
			DescriptiveStatistics maxWEQRevenue = new DescriptiveStatistics();
			DescriptiveStatistics evpAppRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpWEViolations = new DescriptiveStatistics();
			DescriptiveStatistics lpWERelativeViolations = new DescriptiveStatistics();
			DescriptiveStatistics lpEFViolations = new DescriptiveStatistics();
			
			DescriptiveStatistics maxWEQTime = new DescriptiveStatistics();
			DescriptiveStatistics evpAppTime = new DescriptiveStatistics();
			DescriptiveStatistics lpTime = new DescriptiveStatistics();
			long startTime , endTime ;
			for(int t=0;t<RunParameters.numTrials;t++){
				/* Create random unitDemand market. Then get valuation matrix associated to the market. */
				Market market = MarketFactory.randomUnitDemandMarket(numUsers, numCampaigns, prob);
				double [][] valuationMatrix = UnitDemandExperiments.getValuationMatrixFromMarket(market);
				/*
				 * Measure maxEQ
				 */
		        MaxWEQ maxWEQ = new MaxWEQ(valuationMatrix);				
				startTime = System.nanoTime();
				maxWEQRevenue.addValue(maxWEQ.Solve().getSellerRevenue());
				endTime = System.nanoTime();
				maxWEQTime.addValue(endTime - startTime);
				/*
				 * Measure evpApp
				 */
		        EVPApproximation evpApp = new EVPApproximation(valuationMatrix);
				startTime = System.nanoTime();
				evpAppRevenue.addValue(evpApp.Solve().getSellerRevenue());
				endTime = System.nanoTime();
				evpAppTime.addValue(endTime - startTime);
				/*
				 * Measure lp with optimal allocation. The optimal allocation in this case is just the maximum weight matching
				 */
				/* First, get the maximum matching allocation.*/
		        int[][] maximumMatchingAllocation = getMaximumMatchingFromValuationMatrix(valuationMatrix);
		        MarketAllocation marketMaxMatchingAllocation = new MarketAllocation(market, maximumMatchingAllocation);
				
		        IloCplex iloObject;
				try {
					iloObject = new IloCplex();
					startTime = System.nanoTime();
			        EnvyFreePricesSolutionLP VectorSol = new EnvyFreePricesVectorLP(marketMaxMatchingAllocation,iloObject).Solve();
					lpRevenue.addValue(VectorSol.sellerRevenuePriceVector());
					endTime = System.nanoTime();
					lpTime.addValue(endTime - startTime);
					/* * Measure violations */
					double[] WEViolations = VectorSol.computeWalrasianEqViolations();
					lpWEViolations.addValue(WEViolations[0]);
					lpWERelativeViolations.addValue(WEViolations[1]);
					lpEFViolations.addValue(VectorSol.computeEFViolationUnitDemand());
				} catch (IloException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/* Prints... for debugging purposes*/
				//System.out.println(market);
		        //Printer.printMatrix(valuationMatrix);
				//Printer.printMatrix(maximumMatchingAllocation);
				//Printer.printVector(VectorSol.getPriceVector());
				//Printer.printVector(maxWeqMatch.getPrices());
			}
			/* log results in database */
			dbLogger.saveUnitDemandData(numUsers, numCampaigns, prob, maxWEQRevenue.getMean(), maxWEQTime.getMean() / 1000000,evpAppRevenue.getMean(),evpAppTime.getMean() / 1000000, lpRevenue.getMean(), lpTime.getMean() / 1000000 , lpWEViolations.getMean(),lpWERelativeViolations.getMean(), lpEFViolations.getMean());
		}else{
			System.out.println("\t Already have data");
		}
	}	
}
