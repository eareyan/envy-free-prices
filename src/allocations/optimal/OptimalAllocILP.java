package allocations.optimal;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.MarketAllocationException;
import util.Cplex;
import util.Printer;
import allocations.error.AllocationAlgoErrorCodes;
import allocations.error.AllocationAlgoException;
import allocations.interfaces.AllocationAlgo;

import com.google.common.collect.HashBasedTable;

/**
 * This class uses CPLEX to implement and solve a mixed-ILP to find a single-step, welfare-maximizing allocation for an input market.
 * The class implements several parameters to control the search (how many solutions, time limit, etc).
 * 
 * @author Enrique Areyan Viqueira
 */
public abstract class OptimalAllocILP<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> implements AllocationAlgo<M, G, B> {

  /**
   * Cplex object.
   */
  protected IloCplex cplex;

  /**
   * Boolean to control whether or not to output.
   */
  protected boolean verbose = false;

  /**
   * How many solutions
   */
  protected int numSolutions = 1;

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
   * Set the verbose option.
   * 
   * @param verbose
   *          - a boolean, if true, then output information while running algorithm.
   */
  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * Set number of solutions. A negative or zero value indicates no limit.
   * 
   * @param numSolutions
   */
  public void setNumSolutions(int numSolutions) {
    this.numSolutions = numSolutions;
  }

  /**
   * Set time limit.
   * 
   * @throws AllocationException
   */
  public void setTimeLimit(double timeLimit) throws AllocationException {
    if (timeLimit <= 0) {
      throw new AllocationException("The time limit must be positive.");
    }
    this.timeLimit = timeLimit;
  }

  /**
   * Solve method. Runs the algorithm.
   * 
   * @param market
   *          - a Market object.
   * @return a MarketAllocation object.
   * @throws AllocationException
   * @throws MarketAllocationException
   */
  public MarketAllocation<M, G, B> Solve(M market) throws AllocationAlgoException, AllocationException, MarketAllocationException {
    try {
      this.cplex = Cplex.getCplex();
      if (!this.verbose) {
        this.cplex.setOut(null);
      }
      // Set a time limit.
      if (this.timeLimit > 0) {
        this.cplex.setParam(IloCplex.DoubleParam.TiLim, this.timeLimit);
      }
      // These two next parameters controls how many solutions we want to get.
      // The first parameter controls how far from the optimal we allow solutions to be,
      // the second parameter controls how many solutions we will get in total.
      this.cplex.setParam(IloCplex.DoubleParam.SolnPoolGap, 0.0);
      if (this.numSolutions > 0) {
        this.cplex.setParam(IloCplex.IntParam.PopulateLim, this.numSolutions);
        this.cplex.setParam(IloCplex.IntParam.SolnPoolIntensity, 4);
      }
      if (this.verbose) {
        System.out.println("**** Running OptimalAllocILP with the following parameters:");
        System.out.println("\t numSolutions = " + this.numSolutions);
        System.out.println("\t timeLimit = " + this.timeLimit);
      }

      // These next two maps point from a good (resp. a bidder) to a positive integer. These maps are used to point from a bidder to its CPLEX variable.
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
        if (this.verbose) {
          System.out.println("**************** Populate ****************");
          System.out.println("The solution pool contains " + numsol + " solutions.");
          System.out.println(numsolreplaced + " solutions were removed due to the " + "solution pool relative gap parameter.");
          System.out.println("In total, " + (numsol + numsolreplaced) + " solutions were generated.");
          System.out.println("Solution status = " + this.cplex.getStatus());
          System.out.println("Solution value  = " + this.cplex.getObjValue());
        }
        // Weed out sub-optimal solutions.
        HashSet<Integer> optIndeces = new HashSet<Integer>();
        double optValue = Double.NEGATIVE_INFINITY;
        for (int l = 0; l < numsol; l++) {
          double currentOptValue = this.cplex.getObjValue(l);
          // Note that this assumes a maximization objective!!
          if (currentOptValue > optValue) {
            optValue = currentOptValue;
            optIndeces.clear();
            optIndeces.add(l);
          } else if (currentOptValue == optValue) {
            optIndeces.add(l);
          }
        }
        // Store all the solutions in an ArrayList.
        ArrayList<int[][]> solutions = new ArrayList<>();
        for (int l : optIndeces) {
          // The solution should be a matrix of integers. However, CPLEX returns a matrix of doubles. So we are going to have to cast this into integers.
          int[][] sol = new int[market.getNumberGoods()][market.getNumberBidders()];
          double[][] solDouble = new double[market.getNumberGoods()][market.getNumberBidders()];
          for (G good : market.getGoods()) {
            solDouble[goodToCPLEXIndex.get(good)] = this.cplex.getValues(allocationMatrixVariable[goodToCPLEXIndex.get(good)], l);
            // Unfortunately in Java the only way to cast your array is to iterate through each element and cast them one by one
            for (B bidder : market.getBidders()) {
              sol[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = (int) Math.round(solDouble[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
            }
          }
          solutions.add(sol);
          if (this.verbose) {
            System.out.println("Solution #" + l);
            Printer.printMatrix(sol);
            System.out.println();
            for (B bidder : market.getBidders()) {
              System.out.println(this.cplex.getValue(indicatorVariable[bidderToCPLEXIndex.get(bidder)], l));
            }
          }
        }
        // First, create a baseSolution. This is the first optimal solution (i.e., at index 0 of array solutions).
        MarketAllocation<M, G, B> baseSolution = this.createMarketAllocation(market, solutions.get(0), goodToCPLEXIndex, bidderToCPLEXIndex);
        // If there are more solutions, add all of them (but the base solution itself) to the base solution.
        if (numsol > 1) {
          for (int l = 1; l < optIndeces.size(); l++) {
              baseSolution.addAllocation(this.createMarketAllocation(market, solutions.get(l), goodToCPLEXIndex, bidderToCPLEXIndex));
          }
        }
        if (this.verbose) {
          System.out.println("**************** End Optimal Alloc ILP ****************");
        }
        return baseSolution;
      }
    } catch (IloException e) {
      // Report that CPLEX failed.
      e.printStackTrace();
      throw new AllocationAlgoException(AllocationAlgoErrorCodes.CPLEX_FAILED);
    }
    // If we ever do reach this point, then we don't really know what happened.
    throw new AllocationAlgoException(AllocationAlgoErrorCodes.UNKNOWN_ERROR);
  }

  private MarketAllocation<M, G, B> createMarketAllocation(M market, int[][] solution, HashMap<G, Integer> goodToCPLEXIndex,
      HashMap<B, Integer> bidderToCPLEXIndex) throws MarketAllocationException {
    HashBasedTable<G, B, Integer> alloc = HashBasedTable.create();
    for (G good : market.getGoods()) {
      for (B bidder : market.getBidders()) {
        alloc.put(good, bidder, (int) Math.round(solution[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]));
      }
    }
    return new MarketAllocation<M, G, B>(market, alloc, this.getObjectiveFunction());
  }

  /**
   * Changes the objective of the ILP.
   * 
   * @return an IloNumExpr with the ILP objective.
   * @throws IloException
   */
  protected abstract IloNumExpr getILPObjective(M market, IloNumVar[] indicatorVariable, HashMap<B, Integer> bidderToCPLEXIndex) throws IloException;

}
