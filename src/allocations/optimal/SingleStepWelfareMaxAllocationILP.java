package allocations.optimal;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.MarketAllocationException;
import util.Printer;
import allocations.error.AllocationAlgoErrorCodes;
import allocations.error.AllocationAlgoException;
import allocations.interfaces.AllocationAlgo;
import allocations.objectivefunction.SingleStepFunction;

import com.google.common.collect.HashBasedTable;

/**
 * This class uses CPLEX to implement and solve a mixed-ILP to find a
 * single-step, welfare-maximizing allocation for an input market.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleStepWelfareMaxAllocationILP implements AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>, SingleStepFunction> {
  
  /**
   * Boolean to control whether or not to output.
   */
  protected boolean verbose = false;
  /**
   * How many solutions
   */
  protected static final int numSolutions = 1;
  
  /**
   * Constructor.
   */
  public SingleStepWelfareMaxAllocationILP() {

  }

  /**
   * Constructor.
   * @param verbose - a boolean, if true, then output information while running algorithm.
   */
  public SingleStepWelfareMaxAllocationILP(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * Solve method. Runs the algorithm.
   * @param market - a Market object.
   * @return a MarketAllocation object.
   * @throws AllocationException 
   * @throws MarketAllocationException 
   */
  public MarketAllocation<Goods, Bidder<Goods>, SingleStepFunction> Solve(Market<Goods, Bidder<Goods>> market) throws AllocationAlgoException, AllocationException, MarketAllocationException {
    try {
      IloCplex cplex = new IloCplex();
      if (!this.verbose){
        cplex.setOut(null);
      }
      /*
       * These two next parameters controls how many solutions we want to get.
       * The first parameter controls how far from the optimal we allow
       * solutions to be, the second parameter controls how many solutions we
       * will get in total.
       */
      cplex.setParam(IloCplex.DoubleParam.SolnPoolGap, 0.0);
      cplex.setParam(IloCplex.IntParam.PopulateLim, SingleStepWelfareMaxAllocationILP.numSolutions);
      /*
       * These next two maps point from a good (resp. a bidder) to a positive integer.
       * These maps are used to point from a bidder to its CPLEX variable.
       */
      HashMap<Goods, Integer> goodToCPLEXIndex = new HashMap<Goods, Integer>();
      HashMap<Bidder<Goods>, Integer> bidderToCPLEXIndex = new HashMap<Bidder<Goods>, Integer>();
      for (int i = 0; i < market.getNumberGoods(); i++) {
        goodToCPLEXIndex.put(market.getGoods().get(i), i);
      }
      for (int j = 0; j < market.getNumberBidders(); j++) {
        bidderToCPLEXIndex.put(market.getBidders().get(j), j);
      }
      // Variables
      IloNumVar[] indicatorVariable = cplex.boolVarArray(market.getNumberBidders());
      IloNumVar[][] allocationMatrixVariable = new IloNumVar[market.getNumberGoods()][];
      for (Goods good : market.getGoods()) {
        allocationMatrixVariable[goodToCPLEXIndex.get(good)] = cplex.intVarArray(market.getNumberBidders(), 0, Integer.MAX_VALUE);
      }
      // LP objective function. \sum_j R_j y_j
      IloLinearNumExpr obj = cplex.linearNumExpr();
      for (Bidder<Goods> bidder : market.getBidders()) {
        obj.addTerm(bidder.getReward(), indicatorVariable[bidderToCPLEXIndex.get(bidder)]);
      }
      // System.out.println(obj);
      cplex.addMaximize(obj);

      // Constraint (1). Allocation from a good not connected to a bidder is zero.
      // Constraint (2). Allocation satisfies bidder.
      for (Bidder<Goods> bidder : market.getBidders()) {
        double coeff = 1.0 / ((double) bidder.getDemand());
        IloLinearNumExpr expr = cplex.linearNumExpr();
        for (Goods good : market.getGoods()) {
          if (bidder.demandsGood(good)) {
            expr.addTerm(coeff, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
          } else {
            cplex.addEq(0, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
          }
        }
        cplex.addGe(expr, indicatorVariable[bidderToCPLEXIndex.get(bidder)]);
        cplex.addLe(expr, indicatorVariable[bidderToCPLEXIndex.get(bidder)]);
      }
 
      // Constrain (2). Allocation from goods can not be more than supply.
      for (Goods good : market.getGoods()) {
        IloLinearNumExpr expr = cplex.linearNumExpr();
        for (Bidder<Goods> bidder : market.getBidders()) {
          expr.addTerm(1.0, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
        }
        cplex.addLe(expr, good.getSupply());
      }

      // Solve the problem and get many solutions.
      cplex.solve();
      if (cplex.populate()) {
        int numsol = cplex.getSolnPoolNsolns();
        int numsolreplaced = cplex.getSolnPoolNreplaced();
        // Print some information
        if (verbose) {
          System.out.println("***********************************Populate");
          System.out.println("The solution pool contains " + numsol + " solutions.");
          System.out.println(numsolreplaced + " solutions were removed due to the " + "solution pool relative gap parameter.");
          System.out.println("In total, " + (numsol + numsolreplaced) + " solutions were generated.");
          System.out.println("Solution status = " + cplex.getStatus());
          System.out.println("Solution value  = " + cplex.getObjValue());
        }
        // Store all the solutions in an ArrayList.
        ArrayList<int[][]> Solutions = new ArrayList<>();
        for (int l = 0; l < numsol; l++) {
          /*
           * The solution should be a matrix of integers. However, CPLEX returns
           * a matrix of doubles. So we are going to have to cast this into
           * integers.
           */
          int[][] sol = new int[market.getNumberGoods()][market.getNumberBidders()];
          double[][] solDouble = new double[market.getNumberGoods()][market.getNumberBidders()];
          for (Goods good : market.getGoods()) {
            solDouble[goodToCPLEXIndex.get(good)] = cplex.getValues(allocationMatrixVariable[goodToCPLEXIndex.get(good)], l);
            /*
             * Unfortunately in Java the only way to cast your array is to
             * iterate through each element and cast them one by one
             */
            for (Bidder<Goods> bidder : market.getBidders()) {
              sol[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = (int) Math.round(solDouble[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
            }
          }
          Solutions.add(sol);
          if (verbose) {
            Printer.printMatrix(sol);
            System.out.println();
            for (Bidder<Goods> bidder : market.getBidders()) {
              System.out.println(cplex.getValue(indicatorVariable[bidderToCPLEXIndex.get(bidder)], l));
            }
          }
        }
        cplex.end();
        
        HashBasedTable<Goods,Bidder<Goods>,Integer> alloc = HashBasedTable.create();
        for(Goods good : market.getGoods()){
          for(Bidder<Goods> bidder : market.getBidders()){
            alloc.put(good, bidder , (int) Math.round(Solutions.get(0)[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]));    
          }
        }
        return new MarketAllocation<Goods, Bidder<Goods>, SingleStepFunction>(market, alloc, this.getObjectiveFunction());
      }
    } catch (IloException e) {
      // Report that CPLEX failed.
      e.printStackTrace();
      throw new AllocationAlgoException(AllocationAlgoErrorCodes.CPLEX_FAILED);
    }
    // If we ever do reach this point, then we don't really know what happened.
    throw new AllocationAlgoException(AllocationAlgoErrorCodes.UNKNOWN_ERROR);
  }

  /*
   * This algorithm optimizes a single step function.
   * 
   * @see allocations.interfaces.AllocationAlgoInterface#getObjectiveFunction()
   */
  @Override
  public SingleStepFunction getObjectiveFunction() {
    return new SingleStepFunction();
  }
  
  @Override
  public String toString(){
    return "SingleStepWelfareMaxAllocationILP which always uses SingleStepFunction objective";
  }

}
