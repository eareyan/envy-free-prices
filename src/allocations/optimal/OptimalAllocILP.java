package allocations.optimal;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
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

import com.google.common.collect.HashBasedTable;

/**
 * This class uses CPLEX to implement and solve a mixed-ILP to find a
 * single-step, welfare-maximizing allocation for an input market.
 * 
 * @author Enrique Areyan Viqueira
 */
public abstract class OptimalAllocILP <M extends Market<G, B>, G extends Goods, B extends Bidder<G>> implements AllocationAlgo<M, G, B> {
  
  /**
   * Boolean to control whether or not to output.
   */
  protected boolean verbose = false;
  
  /**
   * How many solutions
   */
  protected static final int numSolutions = 1;
  
  /**
   * Cplex object.
   */
  protected IloCplex cplex;
  
  /**
   * Maximum time allowable for the algorithm.
   */
  protected double timeLimit = -1.0;
  
  /**
   * Constructor.
   */
  public OptimalAllocILP() {

  }
  
  /**
   * Constructor.
   * @throws AllocationException 
   */
  public OptimalAllocILP(double timeLimit) throws AllocationException {
    if(timeLimit < 0) {
      throw new AllocationException("The time limit must be positive.");
    }
    this.timeLimit = timeLimit;
  }

  /**
   * Constructor.
   * @param verbose - a boolean, if true, then output information while running algorithm.
   */
  public OptimalAllocILP(boolean verbose) {
    this.verbose = verbose;
  }
  
  /**
   * Solve method. Runs the algorithm.
   * @param market - a Market object.
   * @return a MarketAllocation object.
   * @throws AllocationException 
   * @throws MarketAllocationException 
   */
  public MarketAllocation<M, G, B> Solve(M market) throws AllocationAlgoException, AllocationException, MarketAllocationException {
    try {
      // Create Cplex Object
      this.cplex = new IloCplex();
      if (!this.verbose){
        this.cplex.setOut(null);
      }
      // Set a time limit.
      if(this.timeLimit >= 0) {
        this.cplex.setParam(IloCplex.DoubleParam.TiLim, this.timeLimit);
      }
      /*
       * These two next parameters controls how many solutions we want to get.
       * The first parameter controls how far from the optimal we allow
       * solutions to be, the second parameter controls how many solutions we
       * will get in total.
       */
      this.cplex.setParam(IloCplex.DoubleParam.SolnPoolGap, 0.0);
      this.cplex.setParam(IloCplex.IntParam.PopulateLim, OptimalAllocILP.numSolutions);

      /*
       * These next two maps point from a good (resp. a bidder) to a positive integer.
       * These maps are used to point from a bidder to its CPLEX variable.
       */
      HashMap<G, Integer> goodToCPLEXIndex = new HashMap<G, Integer>();
      HashMap<B, Integer> bidderToCPLEXIndex = new HashMap<B, Integer>();
      for (int i = 0; i < market.getNumberGoods(); i++) {
        goodToCPLEXIndex.put(market.getGoods().get(i), i);
      }
      for (int j = 0; j < market.getNumberBidders(); j++) {
        bidderToCPLEXIndex.put(market.getBidders().get(j), j);
      }
      // Variables
      IloNumVar[] indicatorVariable = this.cplex.boolVarArray(market.getNumberBidders());
      IloNumVar[][] allocationMatrixVariable = new IloNumVar[market.getNumberGoods()][];
      for (G good : market.getGoods()) {
        allocationMatrixVariable[goodToCPLEXIndex.get(good)] = this.cplex.intVarArray(market.getNumberBidders(), 0, Integer.MAX_VALUE);
      }
     
      this.cplex.addMaximize(this.getILPObjective(market, indicatorVariable, bidderToCPLEXIndex));

      // Constraint (1). Allocation from a good not connected to a bidder is zero.
      // Constraint (2). Allocation satisfies bidder.
      for (B bidder : market.getBidders()) {
        double coeff = 1.0 / ((double) bidder.getDemand());
        IloLinearNumExpr expr = this.cplex.linearNumExpr();
        for (G good : market.getGoods()) {
          if (bidder.demandsGood(good)) {
            expr.addTerm(coeff, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
          } else {
            this.cplex.addEq(0, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
          }
        }
        this.cplex.addGe(expr, indicatorVariable[bidderToCPLEXIndex.get(bidder)]);
        this.cplex.addLe(expr, indicatorVariable[bidderToCPLEXIndex.get(bidder)]);
      }
 
      // Constrain (3). Allocation from goods can not be more than supply.
      for (G good : market.getGoods()) {
        IloLinearNumExpr expr = this.cplex.linearNumExpr();
        for (B bidder : market.getBidders()) {
          expr.addTerm(1.0, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
        }
        this.cplex.addLe(expr, good.getSupply());
      }

      // Solve the problem and get many solutions.
      this.cplex.solve();
      if (this.cplex.populate()) {
        int numsol = this.cplex.getSolnPoolNsolns();
        int numsolreplaced = this.cplex.getSolnPoolNreplaced();
        // Print some information
        if (verbose) {
          System.out.println("***********************************Populate");
          System.out.println("The solution pool contains " + numsol + " solutions.");
          System.out.println(numsolreplaced + " solutions were removed due to the " + "solution pool relative gap parameter.");
          System.out.println("In total, " + (numsol + numsolreplaced) + " solutions were generated.");
          System.out.println("Solution status = " + this.cplex.getStatus());
          System.out.println("Solution value  = " + this.cplex.getObjValue());
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
          for (G good : market.getGoods()) {
            solDouble[goodToCPLEXIndex.get(good)] = this.cplex.getValues(allocationMatrixVariable[goodToCPLEXIndex.get(good)], l);
            /*
             * Unfortunately in Java the only way to cast your array is to
             * iterate through each element and cast them one by one
             */
            for (B bidder : market.getBidders()) {
              sol[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = (int) Math.round(solDouble[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
            }
          }
          Solutions.add(sol);
          if (verbose) {
            Printer.printMatrix(sol);
            System.out.println();
            for (B bidder : market.getBidders()) {
              System.out.println(this.cplex.getValue(indicatorVariable[bidderToCPLEXIndex.get(bidder)], l));
            }
          }
        }
        this.cplex.end();
        
        HashBasedTable<G, B, Integer> alloc = HashBasedTable.create();
        for(G good : market.getGoods()){
          for(B bidder : market.getBidders()){
            alloc.put(good, bidder , (int) Math.round(Solutions.get(0)[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]));    
          }
        }
        return new MarketAllocation<M, G, B>(market, alloc, this.getObjectiveFunction());
      }
    } catch (IloException e) {
      // Report that CPLEX failed.
      e.printStackTrace();
      throw new AllocationAlgoException(AllocationAlgoErrorCodes.CPLEX_FAILED);
    }
    // If we ever do reach this point, then we don't really know what happened.
    throw new AllocationAlgoException(AllocationAlgoErrorCodes.UNKNOWN_ERROR);
  }
  
  /**
   * Changes the objective of the ILP.
   * 
   * @return an IloNumExpr with the ILP objective.
   * @throws IloException 
   */
  protected abstract IloNumExpr getILPObjective(M market, IloNumVar[] indicatorVariable, HashMap<B, Integer> bidderToCPLEXIndex) throws IloException;

}
