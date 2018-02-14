package singleminded.algorithms;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.exceptions.MarketAllocationException;
import structures.factory.SingleMindedMarketFactory;
import structures.rewardfunctions.UniformRewardFunction;
import util.Cplex;

import com.google.common.collect.HashBasedTable;

public class SingleMindedTrueEFTests {
  
  public static void main(String[] args) throws Exception {
    System.out.println("SingleMindedTrueEFTests");
    for (int i = 1; i < 100; i++) {
      for (int n = 1; n < 10; n++) {
        for (int m = 1; m < 10; m++) {
          for (int k = 1; k <= 10; k++) {
            System.out.println("(n,m,k) = (" + n + "," + m + "," + (k * .1) + ")");
            TrueEF(n, m, k * .1);
          }
        }
      }
    }
  }

  /**
   * CP object.
   */

  public static void TrueEF(int n, int m, double p) throws Exception {

    // Create a random single minded market
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.createRandomParametrizedSingleMindedMarket(n, m, p, UniformRewardFunction.singletonInstance);
    System.out.println(market);

    // Solve for each feasible allocation

    // IloCP cp = Cplex.getCP();
    IloCplex cplex = Cplex.getCplex();
    cplex.setParam(IloCplex.DoubleParam.SolnPoolGap, 0.0);
    cplex.setParam(IloCplex.IntParam.PopulateLim, 10000);
    cplex.setParam(IloCplex.IntParam.SolnPoolIntensity, 4);
    // cplex.setParam(IloCP.IntParam.Workers, 1);
    // These next two maps point from a good (resp. a bidder) to a positive integer.
    // These maps are used to point from a bidder to its CPLEX variable.
    HashMap<Goods, Integer> goodToCPLEXIndex = new HashMap<Goods, Integer>();
    HashMap<Bidder<Goods>, Integer> bidderToCPLEXIndex = new HashMap<Bidder<Goods>, Integer>();
    for (int i = 0; i < market.getNumberGoods(); i++) {
      goodToCPLEXIndex.put(market.getGoods().get(i), i);
    }
    for (int j = 0; j < market.getNumberBidders(); j++) {
      bidderToCPLEXIndex.put(market.getBidders().get(j), j);
    }

    // Allocation matrix variable
    IloNumVar[][] allocationMatrixVariable = new IloNumVar[market.getNumberGoods()][];
    for (Goods good : market.getGoods()) {
      allocationMatrixVariable[goodToCPLEXIndex.get(good)] = cplex.intVarArray(market.getNumberBidders(), 0, good.getSupply());
    }
    // Partition constraint
    // (1) Capacity constraint.In this case, a partition, i.e., all items are allocated.
    for (Goods good : market.getGoods()) {
      IloLinearNumExpr expr = cplex.linearNumExpr();
      for (Bidder<Goods> bidder : market.getBidders()) {
        expr.addTerm(1.0, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
      }
      cplex.addEq(expr, good.getSupply());
    }

    IloLinearNumExpr obj = cplex.linearNumExpr();
    for (Goods good : market.getGoods()) {
      for (Bidder<Goods> bidder : market.getBidders()) {
        obj.addTerm(1.0, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
      }
    }

    cplex.addMaximize(obj);

    // cplex.startNewSearch();
    cplex.solve();
    // while (cplex.next()) {
    ArrayList<MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>> listOfAllocations = new ArrayList<MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>>();
    if (cplex.populate()) {
      int numsol = cplex.getSolnPoolNsolns();
      System.out.println("numsol = " + numsol);
      for (int l = 0; l < numsol; l++) {
        // if (cp.solve()) {
        //System.out.println("Getting solution #" + l);
        HashBasedTable<Goods, Bidder<Goods>, Integer> alloc = HashBasedTable.create();
        double[][] solDouble = new double[market.getNumberGoods()][market.getNumberBidders()];
        for (Goods good : market.getGoods()) {
          solDouble[goodToCPLEXIndex.get(good)] = cplex.getValues(allocationMatrixVariable[goodToCPLEXIndex.get(good)], l);
          for (Bidder<Goods> bidder : market.getBidders()) {
            alloc.put(good, bidder, (int) Math.round(solDouble[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]));
          }
        }
        listOfAllocations.add(new MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market, alloc, null));
      }
    }
    // For each allocation, run the pricing algo.
    for (MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocation : listOfAllocations) {
      SingleMindedTrueEFTests.pricing(allocation);
    }

    // Report if no solution was found.
  }

  public static void pricing(MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocatedMarket) throws IloException, MarketAllocationException {
    IloCplex cplex = Cplex.getCplex();
    cplex.setOut(null);
    //allocatedMarket.printAllocation();
    // Create a map from goods to numbers. This gives ordering of the goods.
    HashMap<Goods, Integer> goodToPriceIndex = new HashMap<Goods, Integer>();
    for (int i = 0; i < allocatedMarket.getMarket().getNumberGoods(); i++) {
      goodToPriceIndex.put(allocatedMarket.getMarket().getGoods().get(i), i);
    }
    // Create prices variable
    IloNumVar[] prices = cplex.numVarArray(allocatedMarket.getMarket().getNumberGoods(), 0.0, Double.MAX_VALUE);
    // Create objective function: revenue, i.e., sum of prices. We assume all items are sold
    IloLinearNumExpr objective = cplex.linearNumExpr();
    for (Goods good : allocatedMarket.getMarket().getGoods()) {
      objective.addTerm(1.0, prices[goodToPriceIndex.get(good)]);
    }
    cplex.addMaximize(objective);

    HashSet<Bidder<Goods>> satisfiedBidders = new HashSet<Bidder<Goods>>(allocatedMarket.getMarket().getBidders());
    // Create EF constraints.
    outer: for (Bidder<Goods> bidder : allocatedMarket.getMarket().getBidders()) {
      // Check if the bidder was satisfied
      for (Goods good : allocatedMarket.getMarket().getGoods()) {
        if (bidder.demandsGood(good) && allocatedMarket.getAllocation(good, bidder) != 1) {
          satisfiedBidders.remove(bidder);
          continue outer;
        }
      }
    }

    for (Bidder<Goods> bidder1 : allocatedMarket.getMarket().getBidders()) {
      // System.out.println(SingleMindedTrueEFTests.getBidderPriceBundle(cplex, bidder1, allocatedMarket, goodToPriceIndex, prices));
      // Individual rationality.
      double bidder1Value = satisfiedBidders.contains(bidder1) ? bidder1.getReward() : 0.0;
      cplex.addLe(SingleMindedTrueEFTests.getBidderPriceBundle(cplex, bidder1, allocatedMarket, goodToPriceIndex, prices), bidder1Value);
      for (Bidder<Goods> bidder2 : allocatedMarket.getMarket().getBidders()) {
        if (bidder1 != bidder2) {
          // E.F. ToDo.
          cplex.addLe(
              cplex.sum(SingleMindedTrueEFTests.getBidder1ActivationUnderBidder2(bidder1, bidder2, allocatedMarket) * bidder1Value, cplex.prod(-1.0, SingleMindedTrueEFTests.getBidderPriceBundle(cplex, bidder2, allocatedMarket, goodToPriceIndex, prices))), 
              cplex.sum(bidder1Value, cplex.prod(-1.0, SingleMindedTrueEFTests.getBidderPriceBundle(cplex, bidder1, allocatedMarket, goodToPriceIndex, prices))));
        }
      }
    }

    try {
      // Solve the LP.
      if (cplex.solve()) {
        //System.out.println("THERE IS A SOLUTION: " + cplex.getStatus());
        //double[] LP_Prices = cplex.getValues(prices);
        //Printer.printVector(LP_Prices);
      } else {
        System.out.println("********** NO SOLUTION!*******: ->" + cplex.getStatus());
        System.exit(-1);
      }
    } catch (IloException e) {
      System.out.println("Exception: ==>");
      e.printStackTrace();
    }
  }

  public static IloLinearNumExpr getBidderPriceBundle(IloCplex cplex, Bidder<Goods> bidder,
      MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocatedMarket, HashMap<Goods, Integer> goodToPriceIndex,
      IloNumVar[] prices) throws IloException, MarketAllocationException {
    IloLinearNumExpr expr = cplex.linearNumExpr();
    for (Goods good : allocatedMarket.getMarket().getGoods()) {
      expr.addTerm(allocatedMarket.getAllocation(good, bidder), prices[goodToPriceIndex.get(good)]);
    }
    return expr;
  }

  public static int getBidder1ActivationUnderBidder2(Bidder<Goods> bidder1, Bidder<Goods> bidder2, MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocatedMarket) throws MarketAllocationException {
    // Go through bidder1's demand set.
    for (Goods good : bidder1.getDemandSet()) {
      // Check if good was not allocated to bidder2.
      if (allocatedMarket.getAllocation(good, bidder2) != 1) {
        return 0;
      }
    }
    // This means that all items in the demand set of of bidder1 were allocated to bidder2.
    return 1;
  }
}
