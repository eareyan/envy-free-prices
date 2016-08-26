package test;

//import ilog.concert.IloException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
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
		//Market M = Examples.typicalTACMarket();
		/*Market M = Examples.market7();
		Market M = Examples.typicalTACMarket();
		System.out.println(M);
		//MarketAllocation x = new SingleStepEfficientAllocationILP().Solve(M);
		//MarketAllocation x = new GreedyAllocation().Solve(M);
		MarketAllocation x = new GreedyMultiStepAllocation(1,new EffectiveReachRatio()).Solve(M);
		Printer.printMatrix(x.getAllocation());
		System.out.println("---");
		/*System.out.println(x.value());
		System.out.println(x.value(new ArrayList<Integer>(){{add(1);}}));
		System.out.println(x.value(new ArrayList<Integer>(){{add(0);add(1);}}));
		
		EnvyFreePricesVectorLP efpvlp = new EnvyFreePricesVectorLP(x,true);
		//efpvlp.setWalrasianConditions(false);
		EnvyFreePricesSolutionLP prices = efpvlp.Solve();
		
		/*Printer.printVector(prices.getPriceVector());
		System.out.println(prices.sellerRevenuePriceVector());
		System.out.println(prices.sellerRevenuePriceVector(new ArrayList<Integer>(){{add(0);}}));
		System.out.println(prices.sellerRevenuePriceVector(new ArrayList<Integer>(){{add(1);}}));
		System.out.println(prices.sellerRevenuePriceVector(new ArrayList<Integer>(){{add(2);}}));
		System.out.println(prices.sellerRevenuePriceVector(new ArrayList<Integer>(){{add(0);add(2);}}));*/
		
		Market M = Examples.market8();
		System.out.println(M);
		MarketAllocation MA = new SingleStepEfficientAllocationILP().Solve(M);
		Printer.printMatrix(MA.getAllocation());
		EnvyFreePricesVectorLP EFPVLP = new EnvyFreePricesVectorLP(MA);
		EFPVLP.setWalrasianConditions(false);
		EFPVLP.createLP();
		EnvyFreePricesSolutionLP Prices = EFPVLP.Solve();
		Printer.printVector(Prices.getPriceVector());
		
		
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
