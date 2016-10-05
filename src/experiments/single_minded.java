package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import singleminded.ApproxWE;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.SingleMindedMarketFactory;
import util.NumberMethods;
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.RestrictedEnvyFreePricesLPSolution;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyAllocation;
import allocations.objectivefunction.SingleStepFunction;
import allocations.optimal.SingleStepWelfareMaxAllocationILP;

public class single_minded extends Experiments{

	@Override
	public void runOneExperiment(	int numUsers, int numCampaigns, double prob,
									int b, SqlDB dbLogger) throws SQLException, IloException, AllocationAlgoException, BidderCreationException, MarketAllocationException, MarketOutcomeException, AllocationException, GoodsException, MarketCreationException {
	
	
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
				Market<Goods, Bidder<Goods>> M = SingleMindedMarketFactory.createRandomSingleMindedMarket(numUsers , numCampaigns);
				//System.out.println(M);
				
				/* Efficient Allocation */
				//System.out.println("===== Efficient Alloc ======");
				MarketAllocation<Goods, Bidder<Goods>, SingleStepFunction> efficientAlloc = new SingleStepWelfareMaxAllocationILP().Solve(M);
				//Printer.printMatrix(efficientAlloc.getAllocation());
				//System.out.println("efficientWelfare = " + efficientAlloc.value());
				double optimalWelfare = efficientAlloc.value();
				
				/* run approx WE algo */
				//System.out.println("===== ApproxWE ======");
				startTime = System.nanoTime();
				MarketOutcome<Goods, Bidder<Goods>, SingleStepFunction> approxWEResult = new ApproxWE(M).Solve();
				endTime = System.nanoTime();
				PricesStatistics<Goods, Bidder<Goods>, SingleStepFunction> psApprox = new PricesStatistics<Goods, Bidder<Goods>, SingleStepFunction>(approxWEResult);

				approxWelfare.addValue(NumberMethods.getRatio(approxWEResult.getMarketAllocation().value() , optimalWelfare));
				approxRevenue.addValue(NumberMethods.getRatio(approxWEResult.sellerRevenue() ,  optimalWelfare));
				approxEF.addValue((double) psApprox.numberOfEnvyBidders() / numCampaigns);
				approxWE.addValue((double) psApprox.computeMarketClearanceViolations()[0] / numUsers);
				approxTime.addValue(endTime - startTime);
				
				/* Single-Step Greedy + LP */
				//System.out.println("===== Greedy+LP ======");
				startTime = System.nanoTime();
				endTime = System.nanoTime();
				//--LP
				RestrictedEnvyFreePricesLP<SingleStepFunction> efp = new RestrictedEnvyFreePricesLP<SingleStepFunction>(new GreedyAllocation().Solve(M));
				efp.setMarketClearanceConditions(false);
				efp.createLP();
				RestrictedEnvyFreePricesLPSolution<SingleStepFunction> lpResult = efp.Solve();
				PricesStatistics<Goods, Bidder<Goods>, SingleStepFunction> psGreedy = new PricesStatistics<Goods, Bidder<Goods>, SingleStepFunction>(lpResult); 
				greedyWelfare.addValue(NumberMethods.getRatio(lpResult.getMarketAllocation().value() , optimalWelfare));
				greedyRevenue.addValue(NumberMethods.getRatio(lpResult.sellerRevenue() , optimalWelfare));
				greedyEF.addValue((double) psGreedy.numberOfEnvyBidders() / numCampaigns);
				greedyWE.addValue((double) psGreedy.computeMarketClearanceViolations()[0] / numUsers);
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
