package experiments;

import java.sql.SQLException;
import java.util.Random;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import unitdemand.EVPApproximation;
import unitdemand.MaxWEQ;
import util.Printer;

public class UnitDemandComparison extends Experiments{
	
	public static double[][] getValuationMatrix(int n,int m, double prob) {
		Random generator = new Random();
		double[][] valuationMatrix = new double[n][m];
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
				if(generator.nextDouble() <= prob){
					valuationMatrix[i][j] = generator.nextDouble() * (100.0 - 1.0) + 1.0;
				}else{
					valuationMatrix[i][j] = Double.NEGATIVE_INFINITY;
				}
			}
		}
		return valuationMatrix;
	}
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, SqlDB dbLogger) throws SQLException{
		if(!dbLogger.checkIfUnitDemandRowExists("unit_comparison",numUsers, numCampaigns, prob)){
			System.out.println("\t Adding data ");		
			DescriptiveStatistics maxWEQRevenue = new DescriptiveStatistics();
			DescriptiveStatistics evpAppRevenue = new DescriptiveStatistics();
			
			DescriptiveStatistics maxWEQTime = new DescriptiveStatistics();
			DescriptiveStatistics evpAppTime = new DescriptiveStatistics();
			long startTime , endTime ;
			for(int t=0;t<RunParameters.numTrials;t++){
				double[][] valuationMatrix = UnitDemandComparison.getValuationMatrix(numUsers, numCampaigns, prob) ;
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
			}
			/* log results in database */
			dbLogger.saveUnitComparisonData(numUsers, numCampaigns, prob, maxWEQRevenue.getMean(), maxWEQTime.getMean() / 1000000,evpAppRevenue.getMean(),evpAppTime.getMean() / 1000000);			
		}else{
			System.out.println("\t Already have data");
		}
	}
}
