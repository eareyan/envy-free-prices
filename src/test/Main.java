package test;

//import ilog.concert.IloException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import singleminded.ApproxWE;
import statistics.PricesStatistics;
import structures.Campaign;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.User;
import structures.exceptions.CampaignCreationException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketPricesException;
import structures.factory.MarketAllocationFactory;
import structures.factory.MarketFactory;
import structures.factory.RandomMarketFactory;
import structures.factory.UnitMarketFactory;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;
import util.NumberMethods;
import util.Printer;
import algorithms.ascendingauction.AscendingAuction;
import algorithms.ascendingauction.AscendingAuctionModified;
import algorithms.pricing.EnvyFreePricesSolutionLP;
import algorithms.pricing.EnvyFreePricesVectorLP;
import algorithms.pricing.lp.reserveprices.EfficientAlloc;
import algorithms.pricing.lp.reserveprices.GreedyAlloc;
import algorithms.pricing.lp.reserveprices.LPReservePrices;
import algorithms.pricing.lp.reserveprices.WFAlloc;
import algorithms.waterfall.Waterfall;
import algorithms.waterfall.WaterfallPrices;
import allocations.error.AllocationException;
import allocations.greedy.GreedyAllocation;
import allocations.greedy.multistep.GreedyMultiStepAllocation;
import allocations.greedy.multistep.GreedyMultiStepAllocation;
import allocations.objectivefunction.EffectiveReachRatio;
import allocations.objectivefunction.IdentityObjectiveFunction;
import allocations.objectivefunction.SingleStepFunction;
import allocations.optimal.MultiStepEfficientAllocationILP;
import allocations.optimal.SingleStepEfficientAllocationILP;
import experiments.RunParameters;
/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
@SuppressWarnings("unused")
public class Main {
	
	public static void main(String args[]) throws AllocationException, CampaignCreationException, MarketAllocationException, IloException, MarketPricesException{
		
		int numberOfEnvy = 0;
		long startTime , endTime ;
		for(int i = 1; i < 10 ; i ++){
			for(int j = 1 ; j < 10; j++){
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
				
				for(int k = 0; k <100; k ++){
					/* Generate Single-minded random market */
					Market M = RandomMarketFactory.createSingleMindedMarket(i,j);
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
					approxEF.addValue((double) psApprox.numberOfEnvyCampaigns() / j);
					approxWE.addValue((double) psApprox.computeWalrasianViolations()[0] / i);
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
					greedyEF.addValue((double) psGreedy.numberOfEnvyCampaigns() / j);
					greedyWE.addValue((double) psGreedy.computeWalrasianViolations()[0] / i);
					greedyTime.addValue(endTime - startTime);
				}
				System.out.println(String.format("%s %20s %20s %20s %20s %20s %20s %20s %20s %20s", 
													"(" + i  + "," + j + ") = (" , 
													approxWelfare.getMean() + "," , 
													approxRevenue.getMean() + "," , 
													approxEF.getMean() + "," ,
													approxWE.getMean() + "," ,
													approxTime.getMean() / 1000000 + ") - (" ,  
													greedyWelfare.getMean() + "," , 
													greedyRevenue.getMean() + "," , 
													greedyEF.getMean() + "," , 
													greedyWE.getMean() + "," + 
													greedyTime.getMean() / 1000000 + ")"));
			}
		}
	}
	
	public static void main2(String args[]) throws IloException, AllocationException, CampaignCreationException{
		//Market M = Examples.market2();
		Market M = Examples.typicalTACMarket();
		System.out.println(M);
		/* Single-Step Efficient */
		System.out.println("Single-Step Efficient Allocation");
		Printer.printMatrix(new SingleStepEfficientAllocationILP().Solve(M).getAllocation());
		/* Single-Step Greedy */
		System.out.println("Single-Step Greedy Allocation");
		Printer.printMatrix(new GreedyAllocation().Solve(M).getAllocation());
		int stepSize = 1;
		/* Multi-Step Efficient 
		System.out.println("Multi-Step Efficient Allocation");
		Printer.printMatrix(new MultiStepEfficientAllocationILP(M,stepSize,new IdentityObjectiveFunction()).Solve().get(0));
		/* Multi-Step Greedy */
		System.out.println("Multi-Step Greedy Allocation");
		MarketAllocation x = new GreedyMultiStepAllocation(stepSize,new EffectiveReachRatio()).Solve(M);
		Printer.printMatrix(x.getAllocation());
		System.out.println("*** new market ");
		System.out.println(x.getMarket());
	}	
}
