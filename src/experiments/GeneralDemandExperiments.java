package experiments;

import java.io.PrintWriter;
import java.sql.SQLException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import postgresql.JdbcPostgresqlConnection;
import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.Waterfall;
import algorithms.WaterfallMAXWEQ;
import algorithms.WaterfallPrices;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;

/*
 * This class implements experiments between LP (using WF and Efficient Allocation) and WF MaxWEQ.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GeneralDemandExperiments {
	
	public static void main(String[] args) throws InterruptedException, SQLException{
		
		int numUsers = 21;
		int numCampaigns = 21;
		int numTrials = 100;
		JdbcPostgresqlConnection dbLogger = new JdbcPostgresqlConnection(args[0],args[1],args[2]);
		for(int i=2;i<numUsers;i++){
			for(int j=2;j<numCampaigns;j++){
				for(int p=0;p<4;p++){
					double prob = 0.25 + p*(0.25);
					System.out.println(" n = " + i + ", m = " + j + ", prob = " + prob);
					if(dbLogger.checkIfUnitDemandRowExists("general_demand",i, j, prob)) continue;
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
					for(int t=0;t<numTrials;t++){
						/* Create a random Market*/
						Market randomMarket = MarketFactory.randomMarket(i, j, prob);
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
						endTime = System.nanoTime();
						EnvyFreePricesSolutionLP VectorSolWaterfallAllocation = new EnvyFreePricesVectorLP(waterFallAllocationPrices.getMarketAllocation()).Solve();
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
					dbLogger.saveGeneralCaseData(i, j, prob, ratioEfficiency.getMean(), effAllocationRevenue.getMean(), effAllocationTime.getMean() / 1000000, effAllocWEViolation.getMean(), effAllocEFViolation.getMean(), wfAllocationRevenue.getMean(), wfAllocationTime.getMean() / 1000000, wfAllocWEViolation.getMean(), wfAllocEFViolation.getMean(), wfMaxWEQRevenue.getMean(),wfMaxWEQTime.getMean() / 1000000);
					//System.exit(-1); /* stop execution... for debugging purposes */
				}
			}
		}
	}
}
