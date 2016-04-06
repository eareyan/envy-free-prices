package experiments;

import java.sql.SQLException;
import java.util.Random;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import unitdemand.Matching;
import unitdemand.MaxWEQ;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;
import util.NumberMethods;

/*
 * This class implements experiments in the unit-NON-uniform case.
 * It compares MawWEQ against EVPApp.
 * 
 * @author Enrique Areyan Viqueira
 */
public class unit_demand extends Experiments{
	
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
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, int b, SqlDB dbLogger) throws SQLException{
		if(!dbLogger.checkIfUnitDemandRowExists("unit_demand",numUsers, numCampaigns, prob)){
			System.out.println("\t Adding data ");		
			
			DescriptiveStatistics ckEfficiency = new DescriptiveStatistics();
			DescriptiveStatistics ckRevenue = new DescriptiveStatistics();
			DescriptiveStatistics ckTime = new DescriptiveStatistics();
			DescriptiveStatistics ckWE1 = new DescriptiveStatistics();
			DescriptiveStatistics ckWE2 = new DescriptiveStatistics();
			
			DescriptiveStatistics evpEfficiency = new DescriptiveStatistics();
			DescriptiveStatistics evpRevenue = new DescriptiveStatistics();
			DescriptiveStatistics evpTime = new DescriptiveStatistics();
			DescriptiveStatistics evpWE1 = new DescriptiveStatistics();
			DescriptiveStatistics evpWE2 = new DescriptiveStatistics();
			
			DescriptiveStatistics mweqEfficiency = new DescriptiveStatistics();
			DescriptiveStatistics mweqRevenue = new DescriptiveStatistics();
			DescriptiveStatistics mweqTime = new DescriptiveStatistics();
			DescriptiveStatistics mweqWE1 = new DescriptiveStatistics();
			DescriptiveStatistics mweqWE2 = new DescriptiveStatistics();

			long startTime , endTime ;
			for(int t=0;t<RunParameters.numTrials;t++){
				double[][] valuationMatrix = unit_demand.getValuationMatrix(numUsers, numCampaigns, prob) ;
				/*
				 * Compute the efficient allocation, in this case, the maximum weight matching.
				 */
				double valueOptAllocaction = Matching.computeMaximumWeightMatchingValue(valuationMatrix).getValueOfMatching();
				/*
				 * Measure CK
				 */
				unitdemand.ascendingauction.AscendingAuction ck = new unitdemand.ascendingauction.AscendingAuction(valuationMatrix);
				startTime = System.nanoTime();
				Matching ckSol = ck.Solve();
				endTime = System.nanoTime();

				ckEfficiency.addValue(NumberMethods.getRatio(ckSol.getValueOfMatching() , valueOptAllocaction));
				ckRevenue.addValue(ckSol.getSellerRevenue());
				ckTime.addValue(endTime - startTime);
				ckWE1.addValue(ckSol.numberOfEnvyCampaigns());
				ckWE2.addValue(ckSol.computeWalrasianViolations());
				/*
				 * Measure evpApp
				 */
		        EVPApproximation evp = new EVPApproximation(valuationMatrix , new AllConnectedDummies(valuationMatrix));
				startTime = System.nanoTime();
				Matching evpSol = evp.Solve();
				endTime = System.nanoTime();
				
				evpEfficiency.addValue(NumberMethods.getRatio(evpSol.getValueOfMatching() , valueOptAllocaction));
				evpRevenue.addValue(evpSol.getSellerRevenue());
				evpTime.addValue(endTime - startTime);
				evpWE1.addValue(evpSol.numberOfEnvyCampaigns());
				evpWE2.addValue(evpSol.computeWalrasianViolations());
				/*
				 * Measure maxEQ
				 */
		        MaxWEQ maxWEQ = new MaxWEQ(valuationMatrix);				
				startTime = System.nanoTime();
				Matching mweqSol = maxWEQ.Solve();
				endTime = System.nanoTime();
				
				mweqEfficiency.addValue(NumberMethods.getRatio(mweqSol.getValueOfMatching() , valueOptAllocaction));
				mweqRevenue.addValue(mweqSol.getSellerRevenue());
				mweqTime.addValue(endTime - startTime);
				mweqWE1.addValue(mweqSol.numberOfEnvyCampaigns());
				mweqWE2.addValue(mweqSol.computeWalrasianViolations());				
			}
			/* log results in database */
			dbLogger.save_unit_demand(numUsers, numCampaigns, prob, 
					ckEfficiency.getMean(), ckRevenue.getMean(), ckTime.getMean() / 1000000, ckWE1.getMean() , ckWE2.getMean() , 
					evpEfficiency.getMean(),evpRevenue.getMean() , evpTime.getMean() / 1000000, evpWE1.getMean() , evpWE2.getMean(), 
					mweqEfficiency.getMean() , mweqRevenue.getMean() , mweqTime.getMean() / 1000000 , mweqWE1.getMean(), mweqWE2.getMean());			
		}else{
			System.out.println("\t Already have data");
		}
	}
}