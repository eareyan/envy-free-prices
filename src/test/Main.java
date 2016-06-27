package test;

//import ilog.concert.IloException;
import java.util.ArrayList;
import java.util.Collections;

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
	
	
	public static void main(String args[]) throws IloException, AllocationException, CampaignCreationException{
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
		Printer.printMatrix(new GreedyMultiStepAllocation(stepSize,new EffectiveReachRatio()).Solve(M).getAllocation());
	}
}
