package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import statistics.PricesStatistics;
import structures.Market;
import structures.MarketPrices;
import structures.factory.MarketAllocationFactory;
import structures.factory.UnitMarketFactory;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;
import util.NumberMethods;

/*
 * This class implements experiments in the case of unit-uniform demand.
 * It compares EVPApp with LPReservePrices.
 * 
 * @author Enrique Areyan Viqueira
 */
public class unit_uniform_demand extends Experiments{
	
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, int b, SqlDB dbLogger) throws SQLException, IloException{
		if(!dbLogger.checkIfUnitDemandRowExists("unit_uniform_demand",numUsers, numCampaigns, prob)){
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

			DescriptiveStatistics lpEfficiency = new DescriptiveStatistics();
			DescriptiveStatistics lpRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpTime = new DescriptiveStatistics();
			DescriptiveStatistics lpWE1 = new DescriptiveStatistics();
			DescriptiveStatistics lpWE2 = new DescriptiveStatistics();

			long startTime , endTime ;
			for(int t=0;t<RunParameters.numTrials;t++){
				Market market = UnitMarketFactory.randomUnitDemandMarket(numUsers, numCampaigns, prob);
				double [][] valuationMatrix = MarketAllocationFactory.getValuationMatrixFromMarket(market);
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
				ckRevenue.addValue(NumberMethods.getRatio(ckSol.getSellerRevenue() , valueOptAllocaction));
				ckTime.addValue(endTime - startTime);
				ckWE1.addValue((double) ckSol.numberOfEnvyCampaigns() / numCampaigns);
				ckWE2.addValue((double) ckSol.computeWalrasianViolations() / numUsers);
				/*
				 * Measure evpApp
				 */
		        EVPApproximation evp = new EVPApproximation(valuationMatrix , new AllConnectedDummies(valuationMatrix));
				startTime = System.nanoTime();
				Matching evpSol = evp.Solve();
				endTime = System.nanoTime();
				
				evpEfficiency.addValue(NumberMethods.getRatio(evpSol.getValueOfMatching() , valueOptAllocaction));
				evpRevenue.addValue(NumberMethods.getRatio(evpSol.getSellerRevenue() , valueOptAllocaction));
				evpTime.addValue(endTime - startTime);
				evpWE1.addValue((double) evpSol.numberOfEnvyCampaigns() / numCampaigns);
				evpWE2.addValue((double) evpSol.computeWalrasianViolations() / numUsers);
				/*
				 * Measure maxEQ
				 */
		        MaxWEQ maxWEQ = new MaxWEQ(valuationMatrix);				
				startTime = System.nanoTime();
				Matching mweqSol = maxWEQ.Solve();
				endTime = System.nanoTime();
				
				mweqEfficiency.addValue(NumberMethods.getRatio(mweqSol.getValueOfMatching() , valueOptAllocaction));
				mweqRevenue.addValue(NumberMethods.getRatio(mweqSol.getSellerRevenue() , valueOptAllocaction));
				mweqTime.addValue(endTime - startTime);
				mweqWE1.addValue((double) mweqSol.numberOfEnvyCampaigns() / numCampaigns);
				mweqWE2.addValue((double) mweqSol.computeWalrasianViolations() / numUsers);
				/*
				 * Measure LP
				 */
				unitdemand.lp.UnitLPReservePrices lp = new unitdemand.lp.UnitLPReservePrices(market);
				startTime = System.nanoTime();
				MarketPrices lpSol = lp.Solve();
				endTime = System.nanoTime();
				
				lpEfficiency.addValue(NumberMethods.getRatio(lpSol.getMarketAllocation().value(), valueOptAllocaction));
				lpRevenue.addValue(NumberMethods.getRatio(lpSol.sellerRevenuePriceVector() , valueOptAllocaction));
				lpTime.addValue(endTime - startTime);
				PricesStatistics p = new PricesStatistics(lpSol);
				lpWE1.addValue((double) p.numberOfEnvyCampaigns() / numCampaigns);
				lpWE2.addValue((double) p.computeWalrasianViolations()[0] / numUsers);

			}
			/* log results in database */
			dbLogger.save_unit_uniform_demand(numUsers, numCampaigns, prob, 
					ckEfficiency.getMean(),  ckRevenue.getMean(),  ckTime.getMean()/ 1000000, ckWE1.getMean(), ckWE2.getMean(),
					evpEfficiency.getMean(), evpRevenue.getMean(), evpTime.getMean()/ 1000000, evpWE1.getMean(), evpWE2.getMean(),
					mweqEfficiency.getMean(),mweqRevenue.getMean(),mweqTime.getMean()/ 1000000, mweqWE1.getMean(), mweqWE2.getMean(),
					lpEfficiency.getMean(),  lpRevenue.getMean(),  lpTime.getMean()/ 1000000, lpWE1.getMean(), lpWE2.getMean());			
		}else{
			System.out.println("\t Already have data");
		}
	}
}
