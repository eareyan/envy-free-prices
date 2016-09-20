package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import singleminded.ApproxWE;
import statistics.PricesStatistics;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.exceptions.CampaignCreationException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketPricesException;
import structures.factory.SingleMindedMarketFactory;
import util.NumberMethods;
import algorithms.pricing.EnvyFreePricesSolutionLP;
import algorithms.pricing.EnvyFreePricesVectorLP;
import allocations.error.AllocationException;
import allocations.greedy.GreedyAllocation;
import allocations.objectivefunction.SingleStepFunction;
import allocations.optimal.SingleStepEfficientAllocationILP;

public class single_minded extends Experiments{

	@Override
	public void runOneExperiment(	int numUsers, int numCampaigns, double prob,
									int b, SqlDB dbLogger) throws SQLException, IloException, AllocationException, CampaignCreationException, MarketAllocationException, MarketPricesException {
	
	
		if(!dbLogger.checkIfSingleMindedRowExists("singleminded", numUsers, numCampaigns)){
			System.out.println("\t Add data (" + numUsers + "," + numCampaigns + ")");
			long startTime , endTime ;
			DescriptiveStatistics approxRevenue = new DescriptiveStatistics();
			DescriptiveStatistics approxWelfare = new DescriptiveStatistics();
			DescriptiveStatistics approxEF = new DescriptiveStatistics();
			DescriptiveStatistics approxWE = new DescriptiveStatistics();
			DescriptiveStatistics approxTime = new DescriptiveStatistics();
			
			DescriptiveStatistics greedyRevenue = new DescriptiveStatistics();
			DescriptiveStatistics greedyWelfare = new DescriptiveStatistics();
			DescriptiveStatistics greedyEF = new DescriptiveStatistics();
			DescriptiveStatistics greedyWE = new DescriptiveStatistics();
			DescriptiveStatistics greedyTime = new DescriptiveStatistics();
			
			for(int k = 0; k < RunParameters.numTrials; k ++){
				/* Generate Single-minded random market */
				Market M = SingleMindedMarketFactory.createSingleMindedMarket(numUsers , numCampaigns);
				//System.out.println(M);
				
				/* Efficient Allocation */
				//System.out.println("===== Efficient Alloc ======");
				MarketAllocation efficientAlloc = new MarketAllocation(M, new SingleStepEfficientAllocationILP().Solve(M).getAllocation(), new SingleStepFunction());
				//Printer.printMatrix(efficientAlloc.getAllocation());
				//System.out.println("efficientWelfare = " + efficientAlloc.value());
				double optimalWelfare = efficientAlloc.value();
				
				/* run approx WE algo */
				//System.out.println("===== ApproxWE ======");
				startTime = System.nanoTime();
				MarketPrices approxWEResult = new ApproxWE(M).Solve();
				endTime = System.nanoTime();
				//Printer.printMatrix(approxWEResult.getMarketAllocation().getAllocation());
				//Printer.printVector(y.getPriceVector());
				PricesStatistics psApprox = new PricesStatistics(approxWEResult);
				//numberOfEnvy = ps.numberOfEnvyCampaigns();
				//System.out.println("Number of Envy Campaigns " + numberOfEnvy);
				//Printer.printVector(ps.computeWalrasianViolations());
				//System.out.println("Seller revenue " + y.sellerRevenuePriceVector());
				//System.out.println("approx welfare = " + y.getMarketAllocation().value());
				approxWelfare.addValue(NumberMethods.getRatio(approxWEResult.getMarketAllocation().value() , optimalWelfare));
				approxRevenue.addValue(NumberMethods.getRatio(approxWEResult.sellerRevenuePriceVector() ,  optimalWelfare));
				approxEF.addValue((double) psApprox.numberOfEnvyCampaigns() / numCampaigns);
				approxWE.addValue((double) psApprox.computeWalrasianViolations()[0] / numUsers);
				approxTime.addValue(endTime - startTime);
				
				/* Single-Step Greedy + LP */
				//System.out.println("===== Greedy+LP ======");
				startTime = System.nanoTime();
				int[][] greedyAlloc = new GreedyAllocation().Solve(M).getAllocation();
				endTime = System.nanoTime();
				//--LP
				EnvyFreePricesVectorLP efp = new EnvyFreePricesVectorLP(new MarketAllocation(M,greedyAlloc));
				efp.setWalrasianConditions(false);
				efp.createLP();
				EnvyFreePricesSolutionLP lpResult = efp.Solve();
				MarketPrices greedyResult = new MarketPrices(new MarketAllocation(M, greedyAlloc, new SingleStepFunction()),lpResult.getPriceVector());
				PricesStatistics psGreedy = new PricesStatistics(greedyResult);
				//Printer.printMatrix(greedyAlloc);
				//numberOfEnvy = ps2.numberOfEnvyCampaigns();
				//Printer.printVector(z.getPriceVector());
				//System.out.println("Number of Envy Campaigns " + numberOfEnvy);
				//Printer.printVector(ps2.computeWalrasianViolations());
				//System.out.println("Seller revenue " + w.sellerRevenuePriceVector());
				//System.out.println("greedy welfare = " + greedyResult.getMarketAllocation().value());
				greedyWelfare.addValue(NumberMethods.getRatio(greedyResult.getMarketAllocation().value() , optimalWelfare));
				greedyRevenue.addValue(NumberMethods.getRatio(greedyResult.sellerRevenuePriceVector() , optimalWelfare));
				greedyEF.addValue((double) psGreedy.numberOfEnvyCampaigns() / numCampaigns);
				greedyWE.addValue((double) psGreedy.computeWalrasianViolations()[0] / numUsers);
				greedyTime.addValue(endTime - startTime);
			}
			dbLogger.saveSingleMinded("singleminded", numUsers, numCampaigns, 
					approxWelfare.getMean(),approxRevenue.getMean(),approxEF.getMean(),approxWE.getMean(),approxTime.getMean() / 1000000,
					greedyWelfare.getMean(),greedyRevenue.getMean(),greedyEF.getMean(),greedyWE.getMean(),greedyTime.getMean() / 1000000
					);
		} else {
			System.out.println("\t Already have data ");			
		}
	}	
}
