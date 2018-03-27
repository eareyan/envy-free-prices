package singleminded.algorithms.complete;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.HashMap;
import java.util.HashSet;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.factory.SingleMindedMarketFactory;
import test.SingleMindedMarkets;
import util.Cplex;
import allocations.error.AllocationAlgoException;
import allocations.optimal.WelfareMaxAllocationILP;

public class WEExistenceGivenAlloc {

  public static void main2(String[] args) throws GoodsCreationException, BidderCreationException, MarketCreationException, AllocationAlgoException, AllocationException, MarketAllocationException, IloException {
    System.out.println("WEExistenceGivenAlloc");

    SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMindedWithTies();
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarketFactory.uniformIntegerRewardRandomSingleMindedMarket(3, 10, 2);
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(10, 12, 1);

    System.out.println(singleMindedMarket);
    WelfareMaxAllocationILP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> welfareMax = new WelfareMaxAllocationILP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    welfareMax.setNumSolutions(2100000000);
    // welfareMax.setTimeLimit(10.0);
    // welfareMax.setStoreExtraSolutions(true);
    // welfareMax.setVerbose(true);
    MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocation = welfareMax.Solve(singleMindedMarket);
    allocation.printAllocation();
    System.out.println("value = " + allocation.getValue());
    System.out.println("Number of extra allocations: " + allocation.getNumExtraAllocations());
    for (MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x : allocation.getExtraAllocations()) {
      System.out.println("value = " + x.getValue());
      x.printAllocation();
      if (!WEExistenceGivenAlloc.WEExistence(x)) {
        System.out.println("DOES NOT EXISTS!!!!!!");
      }
    }
  }

  public static void main(String[] args) throws GoodsCreationException, BidderCreationException, MarketCreationException, AllocationAlgoException,
      AllocationException, MarketAllocationException, IloException {
    for (int n = 1; n < 10; n++) {
      for (int m = 1; m < 10; m++) {
        for (int k = 1; k < n; k++) {
          System.out.println("(n, m, k) = (" + n + "," + m + "," + k + ")");
          for (int t = 0; t < 100; t++) {
            //SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(n, m, k);
            SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarketFactory.uniformIntegerRewardRandomSingleMindedMarket(n, m, k);
            if (WEExistenceGivenAlloc.checkWelfareMaxButNoWE(singleMindedMarket)) {
              System.out.println("Save this example!");
              System.out.println(singleMindedMarket);
              System.exit(0);
            }
          }
        }
      }
    }
  }

  /**
   * This functions checks the following: a single-minded market that has a WE but also has a welfare-max allocation that does not support a WE.
   * 
   * @return
   * @throws MarketAllocationException
   * @throws AllocationException
   * @throws AllocationAlgoException
   * @throws IloException
   */
  public static boolean checkWelfareMaxButNoWE(SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket) throws AllocationAlgoException, AllocationException, MarketAllocationException, IloException {
    // Find all welfare maximizing solutions.
    WelfareMaxAllocationILP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> welfareMax = new WelfareMaxAllocationILP<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    welfareMax.setNumSolutions(2100000000);
    MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocation = welfareMax.Solve(singleMindedMarket);
    boolean[] allocsSupportWE = new boolean[allocation.getNumExtraAllocations() + 1];
    allocsSupportWE[0] = WEExistenceGivenAlloc.WEExistence(allocation);
    int index = 1;
    for (MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x : allocation.getExtraAllocations()) {
      allocsSupportWE[index] = WEExistence(x);
      index++;
    }
    // Print some info
    if (allocation.getNumExtraAllocations() > 1) {
      System.out.println("Number of Allocations: " + (allocation.getNumExtraAllocations() + 1));
      System.out.println(singleMindedMarket);
      for (MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x : allocation.getExtraAllocations()) {
        System.out.println("---");
        x.printAllocation();
      }
    }
    //Printer.printVector(allocsSupportWE);

    for (int i = 0; i < allocsSupportWE.length; i++) {
      for (int j = i + 1; j < allocsSupportWE.length; j++) {
        if ((allocsSupportWE[i] && !allocsSupportWE[j]) || (!allocsSupportWE[i] && allocsSupportWE[j])) {
          System.out.println("Condition met");
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Given a single-minded allocation, runs a linear program to check the existence of a WE
   * 
   * @param marketAllocation
   * @return
   * @throws IloException
   * @throws MarketAllocationException
   */
  public static boolean WEExistence(MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> marketAllocation) throws IloException,
      MarketAllocationException {
    // Initialization structures.
    IloCplex cplex = Cplex.getCplex();

    // Create a map from goods to a numbers. This gives ordering of the goods.
    HashMap<Goods, Integer> goodToPriceIndex = new HashMap<Goods, Integer>();
    for (int i = 0; i < marketAllocation.getMarket().getNumberGoods(); i++) {
      goodToPriceIndex.put(marketAllocation.getMarket().getGoods().get(i), i);
    }
    // Cplex Variables
    IloNumVar[] prices = cplex.numVarArray(marketAllocation.getMarket().getNumberGoods(), 0.0, Double.MAX_VALUE);

    // Get the set of winners.
    HashSet<Bidder<Goods>> winners = marketAllocation.getWinnerSet();

    // Add a constraint for each bidder.
    for (Bidder<Goods> bidder : marketAllocation.getMarket().getBidders()) {
      IloLinearNumExpr bidderPrices = cplex.linearNumExpr();
      for (Goods good : marketAllocation.getMarket().getGoods()) {
        if (bidder.demandsGood(good)) {
          bidderPrices.addTerm(1.0, prices[goodToPriceIndex.get(good)]);
        }
      }
      if (winners.contains(bidder)) {
        // For each winner, make sure it is individually rational.
        cplex.addGe(bidder.getReward(), bidderPrices);
      } else {
        // For each losers, make sure it is ok with prices.
        cplex.addLe(bidder.getReward(), bidderPrices);
      }
    }
    // Price of unallocated items is zero
    for (Goods good : marketAllocation.getMarket().getGoods()) {
      if (marketAllocation.allocationFromGood(good) == 0) {
        cplex.addEq(0.0, prices[goodToPriceIndex.get(good)]);
      }
    }
    if (cplex.solve()) {
      // double[] LP_Prices = cplex.getValues(prices);
      // Printer.printVector(LP_Prices);
      return true;
    } else {
      return false;
    }

  }

}
