package test;

import java.util.ArrayList;

import singleminded.SingleMindedApproxWE;
import singleminded.SingleMindedGreedyAllocation;
import singleminded.SingleMindedMarket;
import singleminded.SingleMindedPricingLP;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.comparators.BiddersComparatorBy1ToSqrtIRatio;
import structures.comparators.BiddersComparatorByRToSqrtIRatio;
import structures.factory.RandomMarketFactory;
import structures.factory.SingleMindedMarketFactory;
import util.Printer;
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.RestrictedEnvyFreePricesLPSolution;
import algorithms.pricing.reserveprices.RevMaxHeuristic;
import allocations.greedy.GreedyAllocation;
import allocations.greedy.GreedyMultiStepAllocation;
import allocations.interfaces.AllocationAlgo;
import allocations.objectivefunction.ConcaveObjectiveFunction;
import allocations.objectivefunction.ConvexObjectiveFunction;
import allocations.objectivefunction.EffectiveReachRatio;
import allocations.objectivefunction.IdentityObjectiveFunction;
import allocations.objectivefunction.interfaces.ObjectiveFunction;
import allocations.optimal.EgalitarianMaxAllocation;
import allocations.optimal.SingleStepWelfareMaxAllocationILP;
import experiments.LPWrapper;

/**
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
  
  public static void main(String[] args) throws Exception {
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.createUniformRewardRandomSingleMindedMarket(5, 5 , 3);
    //SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.createElitistRewardRandomSingleMindedMarket(5, 5, 2);
    System.out.println(market);
    
    // Run the LP using the allocation algorithm from before.
    //MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> greedyAlloc = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(new BiddersComparatorBy1ToSqrtIRatio<Goods, Bidder<Goods>>()).Solve(market);
    MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> greedyAlloc = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(new BiddersComparatorByRToSqrtIRatio<Goods, Bidder<Goods>>()).Solve(market);

    System.out.println("\nSingle-Minded LP = ");
    PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> smLP = new SingleMindedPricingLP<Goods, Bidder<Goods>>(greedyAlloc).getStatistics();
    smLP.getMarketOutcome().getMarketAllocation().printAllocation();
    smLP.getMarketOutcome().printPrices();
    System.out.println("\tEF = " + smLP.numberOfEnvyBidders());
    System.out.println("\tEF = " + smLP.listOfEnvyBidders());
    System.out.println("\t\tRatio Loss utility = " + smLP.getRatioLossUtility());
    
    
    System.out.println("\nHuangs = ");    
    SingleMindedApproxWE aw = new SingleMindedApproxWE(market);
    PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> approxWEResult = aw.Solve();
    approxWEResult.getMarketOutcome().getMarketAllocation().printAllocation();
    approxWEResult.getMarketOutcome().printPrices();
    System.out.println("\tEF = " + approxWEResult.numberOfEnvyBidders());
    System.out.println("\tEF = " + approxWEResult.listOfEnvyBidders());
    System.out.println("\t\tRatio Loss utility = " + approxWEResult.getRatioLossUtility());

    MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> efficientAlloc = new SingleStepWelfareMaxAllocationILP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(market);
    efficientAlloc.printAllocation();

  }
  
  public static void main3(String[] args) throws Exception {
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.createUniformRewardRandomSingleMindedMarket(7, 7, 2);
    System.out.println(market);
    
    SingleMindedApproxWE aw = new SingleMindedApproxWE(market);
    PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> approxWEResult = aw.Solve();
    approxWEResult.getMarketOutcome().getMarketAllocation().printAllocation();
    approxWEResult.getMarketOutcome().printPrices();
    System.out.println("# = " + approxWEResult.numberOfEnvyBidders());
    System.out.println("Who? = " + approxWEResult.listOfEnvyBidders());
    System.out.println("#MC = " + approxWEResult.getMarketClearanceViolations().getKey() + "," + approxWEResult.getMarketClearanceViolations().getValue());

    MarketAllocation<SingleMindedMarket<Goods,Bidder<Goods>>, Goods, Bidder<Goods>> smge = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods,Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(market);
    smge.printAllocation();
    
    System.out.println();

    MarketAllocation<SingleMindedMarket<Goods,Bidder<Goods>>, Goods, Bidder<Goods>> smge2 = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods,Bidder<Goods>>, Goods, Bidder<Goods>>(new BiddersComparatorBy1ToSqrtIRatio<Goods, Bidder<Goods>>()).Solve(market);
    smge2.printAllocation();
}
  
  
  public static void main2(String[] args) throws Exception {
    
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.createUniformRewardRandomSingleMindedMarket(5, 5, 2);
    System.out.println(market);

    System.out.println("Greedy Welfare");
    GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga2 = new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> greedyAlloc2 = ga2.Solve(market);
    greedyAlloc2.printAllocation();

    System.out.println("Greedy Egalitarian");
    GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(new BiddersComparatorBy1ToSqrtIRatio<Goods, Bidder<Goods>>());
    MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> greedyAlloc = ga.Solve(market);
    greedyAlloc.printAllocation();
    
    System.out.println("--- Welfare Max. Alloc");
    SingleStepWelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> sswm = new SingleStepWelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> optAlloc = sswm.Solve(market);
    optAlloc.printAllocation();

    System.out.println("--- Egalitarian Max. Alloc");
    EgalitarianMaxAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> em = new EgalitarianMaxAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> egaAlloc = em.Solve(market);
    egaAlloc.printAllocation();
    
    
    @SuppressWarnings("serial")
    ArrayList<LPWrapper.Allocations> allocs = new ArrayList<LPWrapper.Allocations>() {
      {
        add(LPWrapper.Allocations.GreedyWelfare);
        add(LPWrapper.Allocations.GreedyEgalitarian);
        add(LPWrapper.Allocations.OptimalWelfare);
        add(LPWrapper.Allocations.OptimalEgalitarian);
      }
    };
    for(LPWrapper.Allocations alloc : allocs) {
      PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> smlp = LPWrapper.getMarketPrices(market, alloc);
      System.out.println(alloc + " - Welfare = " + smlp.getWelfare());     
    }
    
    SingleMindedApproxWE aw = new SingleMindedApproxWE(market);
    PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> approxWEResult = aw.Solve();
    approxWEResult.getMarketOutcome().getMarketAllocation().printAllocation();
    approxWEResult.getMarketOutcome().printPrices();
    System.out.println("# = " + approxWEResult.numberOfEnvyBidders());
    System.out.println("Who? = " + approxWEResult.listOfEnvyBidders());
    System.out.println("#MC = " + approxWEResult.getMarketClearanceViolations().getKey() + "," + approxWEResult.getMarketClearanceViolations().getValue());


  }
  
  public static void main1(String[] args) throws Exception {
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.createUniformRewardRandomSingleMindedMarket(5, 5, 2);
    System.out.println(market);
    SingleMindedApproxWE aw = new SingleMindedApproxWE(market);
    PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> approxWEResult = aw.Solve();
    approxWEResult.getMarketOutcome().getMarketAllocation().printAllocation();
    approxWEResult.getMarketOutcome().printPrices();
    System.out.println("# = " + approxWEResult.numberOfEnvyBidders());
    System.out.println("Who? = " + approxWEResult.listOfEnvyBidders());
    System.out.println("#MC = " + approxWEResult.getMarketClearanceViolations().getKey() + "," + approxWEResult.getMarketClearanceViolations().getValue());
    
    
    //market = RandomMarketFactory.randomMarket(5, 5, 0.25);
    //System.out.println(market);
    RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    //RevMaxHeuristic rmh = new RevMaxHeuristic(market, new SingleStepWelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> revMaxOutcome = rmh.Solve();
    Printer.PrintOutcomeInfo(revMaxOutcome);
    System.out.println(revMaxOutcome);
    revMaxOutcome.getMarketAllocation().printAllocation();
    revMaxOutcome.printPrices();
    PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> psRevMax = new PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(revMaxOutcome);
    System.out.println("# = " + psRevMax.numberOfEnvyBidders());
    System.out.println("Who? = " + psRevMax.listOfEnvyBidders());
    System.out.println("#MC = " + psRevMax.getMarketClearanceViolations().getKey() + "," + psRevMax.getMarketClearanceViolations().getValue());    
  }
  
  /**
   * Testing all kinds of markets.
   * @throws Exception 
   */
  public static<O extends ObjectiveFunction> void main0(String[] args) throws Exception {

    ArrayList<Market<Goods,Bidder<Goods>>> marketList = new ArrayList<Market<Goods,Bidder<Goods>>>();
    marketList.add(SizeInterchangeableMarkets.market0());
    marketList.add(SizeInterchangeableMarkets.market1());
    marketList.add(SizeInterchangeableMarkets.market2());
    marketList.add(SizeInterchangeableMarkets.market3());
    marketList.add(SizeInterchangeableMarkets.market4());
    marketList.add(SizeInterchangeableMarkets.market6());
    marketList.add(SizeInterchangeableMarkets.market7());
    marketList.add(RandomMarketFactory.randomMarket(3, 5, 0.5));
    marketList.add(RandomMarketFactory.RandomKMarket(4, 4, 1.0, 2));
    marketList.add(SingleMindedMarketFactory.createUniformRewardRandomSingleMindedMarket(4, 3, 2));
    marketList.add(SingleMindedMarketFactory.createUniformRewardRandomSingleMindedMarket(3, 4, 2));
    marketList.add(SizeInterchangeableMarkets.market8());
    marketList.add(SingleMindedMarkets.singleMinded0());
    marketList.add(SingleMindedMarkets.singleMinded1());
    //marketList.add(RandomMarketFactory.randomMarket(20, 20, 0.25));
    
    // marketList.add(SingleMindedMarketFactory.discountSingleMindedMarket(SingleMindedMarkets.singleMinded1(), 20.0));
    
    //ArrayList<AllocationAlgoInterface<Market<Goods,Bidder<Goods>>,Goods,Bidder<Goods>,ObjectiveFunction>> algosList = new ArrayList<AllocationAlgoInterface<Market<Goods,Bidder<Goods>>,Goods,Bidder<Goods>,ObjectiveFunction>>();
    ArrayList<AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>> algosList = new ArrayList<AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>>();
    algosList.add(new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    algosList.add(new SingleStepWelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    // algosList.add(new GreedyMultiStepAllocation<O>(1, new IdentityObjectiveFunction()));
    // NOTE!!: GreedyMulti Step, using EffectiveReachRatio as objective function
    // fails for finding rev. maximizing prices (using reserve prices).
    algosList.add(new GreedyMultiStepAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(1, new EffectiveReachRatio()));
    algosList.add(new GreedyMultiStepAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(1, new IdentityObjectiveFunction()));
    algosList.add(new GreedyMultiStepAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(1, new ConcaveObjectiveFunction()));
    algosList.add(new GreedyMultiStepAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(1, new ConvexObjectiveFunction()));

    for (Market<Goods, Bidder<Goods>> M : marketList) {
      System.out.println("********/*************/************");
      Printer.PrintMarketInfo(M);      
      for(AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> algo : algosList){
        /* Allocate Market.  */
        MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> alloc = algo.Solve(M);
        System.out.println(algo + " Alloc");
        /* Price Market.  */
        RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> REFPLP = new RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(alloc);
        REFPLP.setMarketClearanceConditions(false);
        REFPLP.createLP();
        RestrictedEnvyFreePricesLPSolution<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> marketPrices = REFPLP.Solve();
        Printer.PrintOutcomeInfo(marketPrices);
        /* Revenue Max Heuristic*/
        if (algo.getObjectiveFunction().isSafeForReserve()) {
          RevMaxHeuristic rmh = new RevMaxHeuristic(M, algo);
          MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> rmhOutcome = rmh.Solve();
          System.out.println("Rev. Max Heuristic = " + rmhOutcome);
          rmhOutcome.getMarketAllocation().printAllocation();
          rmhOutcome.printPrices();
        }
      }
    }
  }
  
}
