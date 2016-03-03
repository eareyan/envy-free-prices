package experiments;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.sql.SQLException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.Waterfall;
import algorithms.WaterfallMAXWEQ;
import algorithms.WaterfallPrices;
import algorithms.lp.GeneralApproximation;
import log.SqlDB;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;

/*
 * This class implements experiments in the general demand case.
 * It compares LP (using WF and Efficient Allocation) and WF MaxWEQ.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GeneralDemandExperiments extends Experiments{

	public void runOneExperiment(int numUsers,int numCampaigns, double prob, SqlDB dbLogger) throws SQLException, IloException{
		if(!dbLogger.checkIfUnitDemandRowExists("general_demand",numUsers, numCampaigns, prob)){
			System.out.println("\t Add data ");
			
			DescriptiveStatistics ratioEfficiency = new DescriptiveStatistics();
			
			DescriptiveStatistics statEffAllocValue = new DescriptiveStatistics();
			DescriptiveStatistics effAllocationRevenue = new DescriptiveStatistics();
			DescriptiveStatistics effAllocationTime = new DescriptiveStatistics();
			DescriptiveStatistics effAllocWEViolation = new DescriptiveStatistics();
			DescriptiveStatistics effAllocWERelativeViolations = new DescriptiveStatistics();
			DescriptiveStatistics effAllocEFViolation = new DescriptiveStatistics();
			
			DescriptiveStatistics wfAllocationValue = new DescriptiveStatistics();
			DescriptiveStatistics wfAllocationRevenue = new DescriptiveStatistics();
			DescriptiveStatistics wfAllocationTime = new DescriptiveStatistics();
			DescriptiveStatistics wfAllocWEViolation = new DescriptiveStatistics();
			DescriptiveStatistics wfAllocWERelativeViolations = new DescriptiveStatistics();
			DescriptiveStatistics wfAllocEFViolation = new DescriptiveStatistics();
			
			DescriptiveStatistics wfMaxWEQRevenue = new DescriptiveStatistics();
			DescriptiveStatistics wfMaxWEQTime = new DescriptiveStatistics();

			DescriptiveStatistics generalAppRevenue = new DescriptiveStatistics();
			DescriptiveStatistics generalAppTime = new DescriptiveStatistics();
			long startTime , endTime ;
			IloCplex iloObject0,iloObject1,iloObject2;
			for(int t=0;t<RunParameters.numTrials;t++){
				/* Initialize CPLEX objects here so that the time of the algorithm is not penalized */
				iloObject0 = new IloCplex();
				iloObject1 = new IloCplex();
				iloObject2 = new IloCplex();
				/* Create a random Market*/
				Market randomMarket = MarketFactory.randomMarket(numUsers, numCampaigns, prob);
				/* Find the efficient allocation*/
				startTime = System.nanoTime();
				int[][] efficientAllocation = new EfficientAllocationLP(randomMarket).Solve(iloObject0).get(0);
				MarketAllocation randomMarketEfficientAllocation = new MarketAllocation(randomMarket, efficientAllocation);
				double effAllocValue = randomMarketEfficientAllocation.value();
				statEffAllocValue.addValue(effAllocValue);
	
				/* Run LP Program*/
				EnvyFreePricesSolutionLP VectorSolEfficientAllocation = new EnvyFreePricesVectorLP(randomMarketEfficientAllocation,iloObject1).Solve();
				endTime = System.nanoTime();
				effAllocationTime.addValue(endTime - startTime);
				effAllocationRevenue.addValue(VectorSolEfficientAllocation.sellerRevenuePriceVector());
				double[] effAllocationWEViolations = VectorSolEfficientAllocation.computeWalrasianEqViolations();
				effAllocWEViolation.addValue(effAllocationWEViolations[0]);
				effAllocWERelativeViolations.addValue(effAllocationWEViolations[1]);
				effAllocEFViolation.addValue(VectorSolEfficientAllocation.numberOfEnvyCampaigns());
	
				/* Run Waterfall*/
				startTime = System.nanoTime();
				WaterfallPrices waterFallAllocationPrices = new Waterfall(randomMarket).Solve();
				EnvyFreePricesSolutionLP VectorSolWaterfallAllocation = new EnvyFreePricesVectorLP(waterFallAllocationPrices.getMarketAllocation(),iloObject2).Solve();
				endTime = System.nanoTime();
				double wfAllocValue = VectorSolWaterfallAllocation.getMarketAllocation().value();
				wfAllocationTime.addValue(endTime - startTime);
	
				wfAllocationValue.addValue(wfAllocValue);
				wfAllocationRevenue.addValue(VectorSolWaterfallAllocation.sellerRevenuePriceVector());
				double[] wfAllocationWEViolations = VectorSolEfficientAllocation.computeWalrasianEqViolations();
				wfAllocWEViolation.addValue(wfAllocationWEViolations[0]);
				wfAllocWERelativeViolations.addValue(wfAllocationWEViolations[1]);
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
				//wfMaxWEQ.Solve().numberOfEnvyCampaigns()
				endTime = System.nanoTime();
				wfMaxWEQTime.addValue(endTime - startTime);
				
				/* Run GeneralApproximation... , i.e., WaterfallMAXWEQ with dummy campaigns
				startTime = System.nanoTime();
				GeneralApproximation generalApp = new GeneralApproximation(randomMarket,efficientAllocation);
				generalAppRevenue.addValue(generalApp.Solve().sellerRevenuePriceVector());
				endTime = System.nanoTime();
				generalAppTime.addValue(endTime - startTime);*/
			}
			dbLogger.saveGeneralCaseData(numUsers, numCampaigns, prob, ratioEfficiency.getMean(), effAllocationRevenue.getMean(), effAllocationTime.getMean() / 1000000, effAllocWEViolation.getMean(),effAllocWERelativeViolations.getMean(), effAllocEFViolation.getMean(), wfAllocationRevenue.getMean(), wfAllocationTime.getMean() / 1000000, wfAllocWEViolation.getMean(),wfAllocWERelativeViolations.getMean(), wfAllocEFViolation.getMean(), wfMaxWEQRevenue.getMean(),wfMaxWEQTime.getMean() / 1000000);
			//System.exit(-1); /* stop execution... for debugging purposes */		
		}else{
			System.out.println("\t Already have data ");			
		}
	}
}
