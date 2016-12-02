package test;

import ilog.concert.IloException;

import java.util.ArrayList;

import singleminded.ApproxWE;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.RandomMarketFactory;
import structures.factory.SingleMindedMarketFactory;
import util.Printer;
import algorithms.ascendingauction.AscendingAuction;
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.RestrictedEnvyFreePricesLPSolution;
import algorithms.pricing.error.PrincingAlgoException;
import algorithms.pricing.reserveprices.CriticalPoints;
import algorithms.pricing.reserveprices.RandomSearch;
import algorithms.pricing.reserveprices.RevMaxHeuristic;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyAllocation;
import allocations.greedy.GreedyMultiStepAllocation;
import allocations.interfaces.AllocationAlgo;
import allocations.objectivefunction.ConcaveObjectiveFunction;
import allocations.objectivefunction.ConvexObjectiveFunction;
import allocations.objectivefunction.EffectiveReachRatio;
import allocations.objectivefunction.IdentityObjectiveFunction;
import allocations.objectivefunction.interfaces.ObjectiveFunction;
import allocations.optimal.SingleStepWelfareMaxAllocationILP;

/**
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {
  
  public static void main(String[] args) throws BidderCreationException, MarketCreationException, PrincingAlgoException, IloException, AllocationAlgoException, MarketAllocationException, AllocationException, GoodsException, MarketOutcomeException {
    Market<Goods, Bidder<Goods>> market =  SingleMindedMarkets.singleMinded2();
    //Market<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.createRandomSingleMindedMarket(3,3);
    System.out.println(market);
    //SingleStepWelfareMaxAllocationILP sswm = new SingleStepWelfareMaxAllocationILP();
    //MarketAllocation z = sswm.Solve(market);
    //z.printAllocation();
    
    // Critical Points
    System.out.println("Critical Points");
    CriticalPoints cps = new CriticalPoints(market, new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    //CriticalPoints cps = new CriticalPoints(market, new SingleStepWelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> criPointsOutcome = cps.Solve();
    System.out.println(criPointsOutcome);
    criPointsOutcome.printPrices();
    criPointsOutcome.getMarketAllocation().printAllocation();
    System.out.println(criPointsOutcome.sellerRevenue());
    System.out.println(cps.getSetOfSolutions());
    
    System.out.println("Rev Max Heuristic");
    RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    //RevMaxHeuristic rmh = new RevMaxHeuristic(market, new SingleStepWelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> revMaxOutcome = rmh.Solve();
    System.out.println(revMaxOutcome);
    revMaxOutcome.printPrices();
    revMaxOutcome.getMarketAllocation().printAllocation();
    System.out.println(revMaxOutcome.sellerRevenue());
    System.out.println(rmh.getSetOfSolutions());


  }
  
  public static void main333(String[] args) throws GoodsCreationException, BidderCreationException, MarketCreationException, MarketAllocationException, MarketOutcomeException {
    System.out.println("Testing ascending auction");
    //Market<Goods, Bidder<Goods>> market =  SingleMindedMarkets.singleMinded1();
    Market<Goods, Bidder<Goods>> market =  SizeInterchangeableMarkets.market0();
    System.out.println(market);
    AscendingAuction au = new AscendingAuction(market);
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x = au.Solve();
    x.getMarketAllocation().printAllocation();
    x.printPrices();
  }
	
  public static void main222(String[] args) throws BidderCreationException, MarketCreationException, IloException, AllocationAlgoException, AllocationException, MarketAllocationException, GoodsException, MarketOutcomeException, PrincingAlgoException {
    Market<Goods, Bidder<Goods>> market = RandomMarketFactory.randomMarket(15, 15, 0.25);
    System.out.println(market);
    
    System.out.println("Rev Max Heuristic");
    RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x = rmh.Solve();
    System.out.println(x);
    x.printPrices();
    x.getMarketAllocation().printAllocation();
    //System.out.println(rmh.getSetOfSolutions());
    System.out.println("Local Search");
    RandomSearch ls = new RandomSearch(market, new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> y = ls.Solve();
    System.out.println(y);
    y.printPrices();
    y.getMarketAllocation().printAllocation();
    //System.out.println(ls.getSetOfSolutions());
    
  }

  public static void main111(String[] args) throws BidderCreationException, MarketCreationException, IloException, AllocationAlgoException, AllocationException, MarketAllocationException, GoodsException, MarketOutcomeException, PrincingAlgoException {
   //Market<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded1();
   Market<Goods, Bidder<Goods>> market = RandomMarketFactory.randomMarket(15, 15, 0.25);
   System.out.println(market);
   RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
   MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x = rmh.Solve();
   System.out.println("Best Solution for rev max: ");
   x.getMarketAllocation().printAllocation();
   x.printPrices();
   System.out.println(x.sellerRevenue());
   /*System.out.println(rmh.getSetOfSolutions());
   for(MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> y : rmh.getSetOfSolutions()) {
     y.getMarketAllocation().printAllocation();
     y.printPrices();
   }*/
   
   RandomSearch ls = new RandomSearch(market, new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
   MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> z = ls.Solve();
   System.out.println("Best Solution for local search: ");
   z.getMarketAllocation().printAllocation();
   z.printPrices();
   System.out.println(z.sellerRevenue());
   /*System.out.println(ls.getSetOfSolutions());
   for(MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> y : ls.getSetOfSolutions()) {
     y.getMarketAllocation().printAllocation();
     y.printPrices();
   }*/
  }
  
  /**
   * Testing all kinds of markets.
   * @throws MarketCreationException 
   * @throws PrincingAlgoException 
   */
  public static<O extends ObjectiveFunction> void main0(String[] args) throws IloException, BidderCreationException, AllocationAlgoException, MarketAllocationException, MarketOutcomeException, AllocationException, GoodsException, MarketCreationException, PrincingAlgoException {

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
    marketList.add(SingleMindedMarketFactory.createRandomSingleMindedMarket(4, 3));
    marketList.add(SingleMindedMarketFactory.createRandomSingleMindedMarket(3, 4));
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

  /**
   * Testing singleMindedMarkets.
   * @throws MarketOutcomeException 
   * @throws MarketCreationException 
   */
  public static void main1(String[] args) throws MarketAllocationException, GoodsCreationException, BidderCreationException, MarketOutcomeException, MarketCreationException{
    ArrayList<Market<Goods,Bidder<Goods>>> singleMindedMarketList = new ArrayList<Market<Goods,Bidder<Goods>>>();
    singleMindedMarketList.add(SingleMindedMarkets.singleMinded0());
    singleMindedMarketList.add(SingleMindedMarkets.singleMinded1());
    singleMindedMarketList.add(SingleMindedMarketFactory.createRandomSingleMindedMarket(5, 3));
    for (Market<Goods, Bidder<Goods>> singleMindedMarket : singleMindedMarketList) {
      Printer.PrintMarketInfo(singleMindedMarket);
      ApproxWE approxWE = new ApproxWE(singleMindedMarket);
      MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x = approxWE.Solve();
      Printer.PrintOutcomeInfo(x);
    }
  }
  
  public static void main2(String[] args) throws BidderCreationException, IloException, AllocationAlgoException, AllocationException, MarketAllocationException, GoodsException, MarketOutcomeException, MarketCreationException, PrincingAlgoException{
    
    //MarketWithReservePrice mwrp = new MarketWithReservePrice(SingleMindedMarkets.singleMinded0(), 4.0);
    /*MarketWithReservePrice mwrp = new MarketWithReservePrice(SizeInterchangeableMarkets.market7(), 2.0);
    Printer.PrintMarketInfo(mwrp.getMarket());
    Printer.PrintMarketInfo(mwrp.getMarketWithReservePrice());
    for(Bidder<Goods> bidder : mwrp.getMarket().getBidders()){
      System.out.println(bidder + " -> " + mwrp.getBidderToBidderMap().get(bidder));
    }*/
    
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market7();
    //Market<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded0();
    //Market<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.createRandomSingleMindedMarket(30, 40);
    Market<Goods, Bidder<Goods>> market = RandomMarketFactory.randomMarket(5, 5, 1.0);
    System.out.println(market);
    
    RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    //RevMaxHeuristic rmh = new RevMaxHeuristic(market, new SingleStepWelfareMaxAllocationILP());
    //RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyMultiStepAllocation(1, new EffectiveReachRatio()));
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> outcome = rmh.Solve();
    outcome.getMarketAllocation().printAllocation();
    outcome.printPrices();
    PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ps = new PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(outcome);
    System.out.println("#Envy = " + ps.numberOfEnvyBidders());
  }
  
  public static void main3(String[] args) throws BidderCreationException, MarketCreationException, AllocationAlgoException, GoodsException, AllocationException, MarketAllocationException, IloException, MarketOutcomeException, PrincingAlgoException{
    Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market7();
    Printer.PrintMarketInfo(market); 
    /* Allocate Market.  */
    //AllocationAlgoInterface<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>, SingleStepFunction> alloc = new GreedyAllocation();
    AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> alloc = new GreedyMultiStepAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(1, new EffectiveReachRatio());
    /* Price Market.  */
    //RestrictedEnvyFreePricesLP<SingleStepFunction> REFPLP = new RestrictedEnvyFreePricesLP<SingleStepFunction>(alloc.Solve(market));
    RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> REFPLP = new RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(alloc.Solve(market));
    REFPLP.setMarketClearanceConditions(false);
    REFPLP.createLP();
    RestrictedEnvyFreePricesLPSolution<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> marketPrices = REFPLP.Solve();
    Printer.PrintOutcomeInfo(marketPrices);
    
    //RevMaxHeuristic<SingleStepFunction> rmh = new RevMaxHeuristic<SingleStepFunction>(market, new GreedyAllocation());
    RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyMultiStepAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(100, new IdentityObjectiveFunction()));
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> outcomeWR = rmh.Solve();
    Printer.PrintOutcomeInfo(outcomeWR);
  }

}
