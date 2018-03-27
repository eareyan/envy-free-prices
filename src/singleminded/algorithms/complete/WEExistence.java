package singleminded.algorithms.complete;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

import java.util.HashMap;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;
import structures.factory.SingleMindedMarketFactory;
import util.Cplex;
import util.Printer;

/**
 * MIP to test the existence of WE.
 * 
 * @author Enrique Areyan Viqueira
 *
 */
public class WEExistence {

  public static void main(String[] args) throws IloException, GoodsCreationException, BidderCreationException, MarketCreationException {
    System.out.println("Single-minded decide WE existence");
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMinded();
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMinded0();
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMinded1();
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMinded2();
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMinded3();
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMinded4();
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMinded5();
    // SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMinded6();
    SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarketFactory.uniformIntegerRewardRandomSingleMindedMarket(25, 25, 15);
    //SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.bigSingleMindedMarket();
    //SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(3, 3, 2);
    //System.out.println(singleMindedMarket);
    final long startTime = System.currentTimeMillis();
    WEExistence.decideWE(singleMindedMarket, false);
    final long endTime = System.currentTimeMillis();
    double executionTime = (endTime - startTime);
    System.out.println("executionTime = " + executionTime);

    //System.out.println(singleMindedMarket.getEdgesStringRepresentation());
    //System.out.println(singleMindedMarket.getRewardStringRepresentation());
  }

  /**
   * A very naive CP model to decide the existence of WE in single-minded market, assuming bidders valuations are integers.
   * 
   * @param market
   * @throws IloException
   */
  public static boolean decideWE(SingleMindedMarket<Goods, Bidder<Goods>> market, boolean verbose) throws IloException {
    // Initialization structures.
    IloCplex cplex = Cplex.getCplex();
    //cp.setOut(null);

    HashMap<Goods, Integer> goodToCPLEXIndex = new HashMap<Goods, Integer>();
    HashMap<Bidder<Goods>, Integer> bidderToCPLEXIndex = new HashMap<Bidder<Goods>, Integer>();
    for (int i = 0; i < market.getNumberGoods(); i++) {
      goodToCPLEXIndex.put(market.getGoods().get(i), i);
    }
    for (int j = 0; j < market.getNumberBidders(); j++) {
      bidderToCPLEXIndex.put(market.getBidders().get(j), j);
    }
    // Variables
    // If we want the search to be over floats, uncomment the next line.
    // IloNumVar[] prices = cp.numVarArray(market.getNumberGoods(), 0.0, Double.MAX_VALUE);
    IloIntVar[] prices = cplex.intVarArray(market.getNumberGoods(), 0, (int) Math.ceil(market.getHighestReward()));
    IloIntVar[][] allocationMatrixVariable = new IloIntVar[market.getNumberGoods()][];
    for (Goods good : market.getGoods()) {
      allocationMatrixVariable[goodToCPLEXIndex.get(good)] = cplex.intVarArray(market.getNumberBidders(), 0, 1);
    }
    // Create constraints.
    for (Goods g : market.getGoods()) {
      IloLinearNumExpr allocaSumGoodk = cplex.linearNumExpr();
      for (Bidder<Goods> b : market.getBidders()) {
        if (!b.demandsGood(g)) {
          // A good not demanded by a bidder has corresponding zero allocation variable.
          cplex.addEq(0, allocationMatrixVariable[goodToCPLEXIndex.get(g)][bidderToCPLEXIndex.get(b)]);
        }
        // cp.addLe(0, allocationMatrixVariable[goodToCPLEXIndex.get(g)][bidderToCPLEXIndex.get(b)]);
        allocaSumGoodk.addTerm(1, allocationMatrixVariable[goodToCPLEXIndex.get(g)][bidderToCPLEXIndex.get(b)]);
      }
      // Capacity constraint: items are assumed to be in unit supply.
      cplex.addGe(1, allocaSumGoodk);
      // Walra's law: price of unallocated items is zero.
      cplex.add(cplex.ifThen(cplex.eq(0, allocaSumGoodk), cplex.eq(0, prices[goodToCPLEXIndex.get(g)])));
    }
    for (Bidder<Goods> b : market.getBidders()) {
      IloLinearNumExpr allocaSumBidderi = cplex.linearNumExpr();
      IloLinearNumExpr pricesSumBidderi = cplex.linearNumExpr();
      for (Goods g : market.getGoods()) {
        if (b.demandsGood(g)) {
          allocaSumBidderi.addTerm(1, allocationMatrixVariable[goodToCPLEXIndex.get(g)][bidderToCPLEXIndex.get(b)]);
          pricesSumBidderi.addTerm(1, prices[goodToCPLEXIndex.get(g)]);
        }
      }
      // If a bidder is allocated, it must be able to afford its bundle.
      cplex.add(cplex.ifThen(cplex.eq(b.getDemand(), allocaSumBidderi), cplex.ge(b.getReward(), pricesSumBidderi)));
      // If a bidder is not allocated, prices must be too expensive.
      cplex.add(cplex.ifThen(cplex.eq(0, allocaSumBidderi), cplex.le(b.getReward(), pricesSumBidderi)));
      // Helper constraint: allocation is either 0 or a bundle of exactly the demanded size.
      cplex.add(cplex.or(cplex.eq(0, allocaSumBidderi), cplex.eq(b.getDemand(), allocaSumBidderi)));
    }
    // Solve the model
    //if (cp.solve()) {
    //IloSearchPhase[] phases = new IloSearchPhase[1];
    //phases[0] = getMaxDomainMaxValue(cp, allocationMatrixVariable);
    //phases[1] = getMaxDomainMaxValue(cp, prices);
    
    //cplex.setParam(IloCplex.Param.MIP.Strategy.NodeSelect, IloCplex.NodeSelect.DFS);
    //cplex.setParam(IloCplex.Param.Threads, 1);
    //cplex.setParam(IloCplex.Param.MIP.Strategy.Search, IloCplex.MIPSearch.Traditional);
    //cplex.setParam(IloCplex.BooleanParam.PreInd, false);
    //cplex.setParam(IloCplex.IntParam.Reduce, 0);
    cplex.use(new Brancher());
    if (cplex.solve()) {
      System.out.println("Solved");
      if (verbose) {
        int[][] X = new int[market.getNumberGoods()][market.getNumberBidders()];
        int[] p = new int[market.getNumberGoods()];
        //double[] p = new double[market.getNumberGoods()];
        for (Bidder<Goods> bidder : market.getBidders()) {
          for (Goods good : market.getGoods()) {
            X[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = (int) cplex.getValue(allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
            p[goodToCPLEXIndex.get(good)] = (int) cplex.getValue(prices[goodToCPLEXIndex.get(good)]);
            //p[goodToCPLEXIndex.get(good)] = cp.getValue(prices[goodToCPLEXIndex.get(good)]);
          }
        }
        // Print result info.
        System.out.println("X = ");
        Printer.printMatrix(X);

        System.out.println("\nP = ");
        Printer.printVector(p);
      }
      return true;
    } else {
      System.out.println("There is no WE");
      return false;
    }

  }
   
  // TODO: search phases.
  
  /*public static IloIntVar[] flatten(IloIntVar[][] x) {
    int index = 0;
    IloIntVar[] y = new IloIntVar[x[0].length * x.length];
    for (int i = 0; i < x.length; i++)
      for (int j = 0; j < x[0].length; j++)
        y[index++] = x[i][j];
    return y;
  }
  
  public static IloSearchPhase getMaxDomainMaxValue(IloCP cp, IloIntVar[][] x) throws IloException {
    IloIntVar[] flatVars = flatten(x);
    IloVarSelector[] varSel = new IloVarSelector[1];
    varSel[0] = cp.selectLargest(cp.domainSize());
    //varSel[1] = cp.selectRandomVar();
    IloValueSelector valSel = cp.selectLargest(cp.value());
    IloSearchPhase minDomainMax = cp.searchPhase(flatVars, cp.intVarChooser(varSel), cp.intValueChooser(valSel));
    return minDomainMax;
  }*/

}
