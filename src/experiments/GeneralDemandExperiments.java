package experiments;

import java.sql.SQLException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.Waterfall;
import algorithms.WaterfallMAXWEQ;
import algorithms.WaterfallPrices;
import log.SqlDB;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;

/*
 * This class implements experiments between LP (using WF and Efficient Allocation) and WF MaxWEQ.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GeneralDemandExperiments extends Experiments{

	public void runOneExperiment(int numUsers,int numCampaigns, double prob, SqlDB dbLogger) throws SQLException{
		if(!dbLogger.checkIfUnitDemandRowExists("general_demand",numUsers, numCampaigns, prob)){
			System.out.println("Add data for -- n = " + numUsers + ", m = " + numCampaigns + ", prob = " + prob);
			
			DescriptiveStatistics ratioEfficiency = new DescriptiveStatistics();
			
			DescriptiveStatistics statEffAllocValue = new DescriptiveStatistics();
			DescriptiveStatistics effAllocationRevenue = new DescriptiveStatistics();
			DescriptiveStatistics effAllocationTime = new DescriptiveStatistics();
			DescriptiveStatistics effAllocWEViolation = new DescriptiveStatistics();
			DescriptiveStatistics effAllocEFViolation = new DescriptiveStatistics();
			
			DescriptiveStatistics wfAllocationValue = new DescriptiveStatistics();
			DescriptiveStatistics wfAllocationRevenue = new DescriptiveStatistics();
			DescriptiveStatistics wfAllocationTime = new DescriptiveStatistics();
			DescriptiveStatistics wfAllocWEViolation = new DescriptiveStatistics();
			DescriptiveStatistics wfAllocEFViolation = new DescriptiveStatistics();
			
			DescriptiveStatistics wfMaxWEQRevenue = new DescriptiveStatistics();
			DescriptiveStatistics wfMaxWEQTime = new DescriptiveStatistics();
			long startTime , endTime ;
			for(int t=0;t<RunParameters.numTrials;t++){
				/* Create a random Market*/
				Market randomMarket = MarketFactory.randomMarket(numUsers, numCampaigns, prob);
				/* Find the efficient allocation*/
				startTime = System.nanoTime();
				int[][] efficientAllocation = new EfficientAllocationLP(randomMarket).Solve().get(0);
				MarketAllocation randomMarketEfficientAllocation = new MarketAllocation(randomMarket, efficientAllocation);
				double effAllocValue = randomMarketEfficientAllocation.value();
				statEffAllocValue.addValue(effAllocValue);
	
				/* Run LP Program*/
				EnvyFreePricesSolutionLP VectorSolEfficientAllocation = new EnvyFreePricesVectorLP(randomMarketEfficientAllocation).Solve();
				endTime = System.nanoTime();
				effAllocationTime.addValue(endTime - startTime);
				effAllocationRevenue.addValue(VectorSolEfficientAllocation.sellerRevenuePriceVector());
				effAllocWEViolation.addValue(VectorSolEfficientAllocation.computeWalrasianEqViolations());
				effAllocEFViolation.addValue(VectorSolEfficientAllocation.numberOfEnvyCampaigns());
	
				/* Run Waterfall*/
				startTime = System.nanoTime();
				WaterfallPrices waterFallAllocationPrices = new Waterfall(randomMarket).Solve();
				EnvyFreePricesSolutionLP VectorSolWaterfallAllocation = new EnvyFreePricesVectorLP(waterFallAllocationPrices.getMarketAllocation()).Solve();
				endTime = System.nanoTime();
				double wfAllocValue = VectorSolWaterfallAllocation.getMarketAllocation().value();
				wfAllocationTime.addValue(endTime - startTime);
	
				wfAllocationValue.addValue(wfAllocValue);
				wfAllocationRevenue.addValue(VectorSolWaterfallAllocation.sellerRevenuePriceVector());
				wfAllocWEViolation.addValue(VectorSolWaterfallAllocation.computeWalrasianEqViolations());
				wfAllocEFViolation.addValue(VectorSolWaterfallAllocation.numberOfEnvyCampaigns());
				if(effAllocValue == 0.0 && wfAllocValue == 0.0){
					ratioEfficiency.addValue(1.0);
				}else{
					ratioEfficiency.addValue(wfAllocValue/effAllocValue);
				}
				
				/* Run WaterfallMAXWEQ */
				startTime = System.nanoTime();
				WaterfallMAXWEQ wfMaxWEQ = new WaterfallMAXWEQ(randomMarket);
				wfMaxWEQRevenue.addValue(wfMaxWEQ.Solve().sellerRevenuePriceVector());
				endTime = System.nanoTime();
				wfMaxWEQTime.addValue(endTime - startTime);
			}
			dbLogger.saveGeneralCaseData(numUsers, numCampaigns, prob, ratioEfficiency.getMean(), effAllocationRevenue.getMean(), effAllocationTime.getMean() / 1000000, effAllocWEViolation.getMean(), effAllocEFViolation.getMean(), wfAllocationRevenue.getMean(), wfAllocationTime.getMean() / 1000000, wfAllocWEViolation.getMean(), wfAllocEFViolation.getMean(), wfMaxWEQRevenue.getMean(),wfMaxWEQTime.getMean() / 1000000);
			//System.exit(-1); /* stop execution... for debugging purposes */		
		}else{
			System.out.println("Already have data for -- n = " + numUsers + ", m = " + numCampaigns + ", prob = " + prob);			
		}
	}
}
