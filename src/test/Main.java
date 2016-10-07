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
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.RestrictedEnvyFreePricesLPSolution;
import algorithms.pricing.error.PrincingAlgoException;
import algorithms.pricing.reserveprices.RevMaxHeuristic;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyAllocation;
import allocations.greedy.GreedyMultiStepAllocation;
import allocations.interfaces.AllocationAlgo;
import allocations.objectivefunction.EffectiveReachRatio;
import allocations.objectivefunction.IdentityObjectiveFunction;
import allocations.objectivefunction.interfaces.ObjectiveFunction;
import allocations.optimal.SingleStepWelfareMaxAllocationILP;

/*
 * Main class. Use for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Main {


  /**
   * Testing all kinds of markets.
   * @throws MarketCreationException 
   * @throws PrincingAlgoException 
   */
  public static<O extends ObjectiveFunction> void main(String[] args) throws IloException, BidderCreationException, AllocationAlgoException, MarketAllocationException, MarketOutcomeException, AllocationException, GoodsException, MarketCreationException, PrincingAlgoException {

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
    
    // Talk to LUKE about this! Fail if a market does not contain at least one bidder and at least one good.
    // marketList.add(SingleMindedMarketFactory.discountSingleMindedMarket(SingleMindedMarkets.singleMinded1(), 20.0));
    
    //ArrayList<AllocationAlgoInterface<Market<Goods,Bidder<Goods>>,Goods,Bidder<Goods>,ObjectiveFunction>> algosList = new ArrayList<AllocationAlgoInterface<Market<Goods,Bidder<Goods>>,Goods,Bidder<Goods>,ObjectiveFunction>>();
    ArrayList<AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>> algosList = new ArrayList<AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>>();
    algosList.add(new GreedyAllocation());
    algosList.add(new SingleStepWelfareMaxAllocationILP());
    // algosList.add(new GreedyMultiStepAllocation<O>(1, new IdentityObjectiveFunction()));
    // NOTE!!: GreedyMulti Step, using EffectiveReachRatio as objective function
    // fails for finding rev. maximizing prices (using reserve prices).
    algosList.add(new GreedyMultiStepAllocation(1, new EffectiveReachRatio()));
    algosList.add(new GreedyMultiStepAllocation(1, new IdentityObjectiveFunction()));

    for (Market<Goods, Bidder<Goods>> M : marketList) {
      System.out.println("********/*************/************");
      Printer.PrintMarketInfo(M);      
      for(AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> algo : algosList){
        /* Allocate Market.  */
        MarketAllocation<Goods, Bidder<Goods>> alloc = algo.Solve(M);
        System.out.println(algo + " Alloc");
        /* Price Market.  */
        RestrictedEnvyFreePricesLP REFPLP = new RestrictedEnvyFreePricesLP(alloc);
        REFPLP.setMarketClearanceConditions(false);
        REFPLP.createLP();
        RestrictedEnvyFreePricesLPSolution marketPrices = REFPLP.Solve();
        Printer.PrintOutcomeInfo(marketPrices);
        /* Revenue Max Heuristic*/
        if (algo.getObjectiveFunction().isSafeForReserve()) {
          RevMaxHeuristic rmh = new RevMaxHeuristic(M, algo);
          MarketOutcome<Goods, Bidder<Goods>> rmhOutcome = rmh.Solve();
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
      MarketOutcome<Goods, Bidder<Goods>> x = approxWE.Solve();
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
    
    RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyAllocation());
    //RevMaxHeuristic rmh = new RevMaxHeuristic(market, new SingleStepWelfareMaxAllocationILP());
    //RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyMultiStepAllocation(1, new EffectiveReachRatio()));
    MarketOutcome<Goods, Bidder<Goods>> outcome = rmh.Solve();
    outcome.getMarketAllocation().printAllocation();
    outcome.printPrices();
    PricesStatistics<Goods, Bidder<Goods>> ps = new PricesStatistics<Goods, Bidder<Goods>>(outcome);
    System.out.println("#Envy = " + ps.numberOfEnvyBidders());
  }
  
  public static void main3(String[] args) throws BidderCreationException, MarketCreationException, AllocationAlgoException, GoodsException, AllocationException, MarketAllocationException, IloException, MarketOutcomeException, PrincingAlgoException{
    Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market7();
    Printer.PrintMarketInfo(market); 
    /* Allocate Market.  */
    //AllocationAlgoInterface<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>, SingleStepFunction> alloc = new GreedyAllocation();
    AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> alloc = new GreedyMultiStepAllocation(1, new EffectiveReachRatio());
    /* Price Market.  */
    //RestrictedEnvyFreePricesLP<SingleStepFunction> REFPLP = new RestrictedEnvyFreePricesLP<SingleStepFunction>(alloc.Solve(market));
    RestrictedEnvyFreePricesLP REFPLP = new RestrictedEnvyFreePricesLP(alloc.Solve(market));
    REFPLP.setMarketClearanceConditions(false);
    REFPLP.createLP();
    RestrictedEnvyFreePricesLPSolution marketPrices = REFPLP.Solve();
    Printer.PrintOutcomeInfo(marketPrices);
    
    //RevMaxHeuristic<SingleStepFunction> rmh = new RevMaxHeuristic<SingleStepFunction>(market, new GreedyAllocation());
    RevMaxHeuristic rmh = new RevMaxHeuristic(market, new GreedyMultiStepAllocation(100, new IdentityObjectiveFunction()));
    MarketOutcome<Goods, Bidder<Goods>> outcomeWR = rmh.Solve();
    Printer.PrintOutcomeInfo(outcomeWR);
  }

}
