package allocations.optimal;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import allocations.error.AllocationErrorCodes;
import allocations.error.AllocationException;
import allocations.interfaces.AllocationAlgoInterface;
import allocations.objectivefunction.ObjectiveFunction;
import structures.Market;
import structures.MarketAllocation;
import util.Printer;

/**
 * This class uses CPLEX to implement and solve a mixed-ILP to find 
 * an efficient allocation for an input market. 
 * In this case the allocation is done in "chunks" or steps.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MultiStepEfficientAllocationILP implements AllocationAlgoInterface {
  
  /**
   * The step size
   */
  protected int stepSize;
  
  /**
   * The function with which to compute the partial rewards.
   */
  protected ObjectiveFunction objectiveFunction;
  
  /**
   * Objects needed to interface with CPlex Library.
   */
  protected IloCplex cplex;
  
  /**
   * Boolean to control whether or not to output.
   */
  protected boolean verbose = false;
  
  /**
   * How many solutions
   */
  protected static int numSolutions = 1;
  
  /**
   * Constructor receives a market, step size and objective function.
   * @param stepSize - the step in which copies of users are allocated to campaigns.
   * @param objectiveFunction - the objective function to be used for campaigns.
   * @throws IloException in case the LP failed
   * @throws AllocationException in case the allocation algorithm failed.
   */
  public MultiStepEfficientAllocationILP(int stepSize, ObjectiveFunction objectiveFunction) throws IloException, AllocationException {
    this.stepSize = stepSize;
    if (this.stepSize <= 0) {
      throw new AllocationException(AllocationErrorCodes.STEP_NEGATIVE);
    }
    this.objectiveFunction = objectiveFunction;
    this.cplex = new IloCplex();
  }

  /**
   * Solve function. Creates the ILP and returns all the solutions found in an
   * array of integer matrices.
   * @param market - a Market object
   * @return a MarketAllocation object.
   */
  public MarketAllocation Solve(Market market) throws AllocationException {
    try {
      if (!this.verbose) {
        cplex.setOut(null);
      } else {
        System.out.println("market = " + market);
        System.out.println("stepSize = " + stepSize);
        System.out.println("objectiveFunction = " + objectiveFunction);
      }
      /*
       * These two next parameters controls how many solutions we want to get.
       * The first parameter controls how far from the optimal we allow
       * solutions to be, The second parameter controls how many solutions we
       * will get in total.
       */
      this.cplex.setParam(IloCplex.DoubleParam.SolnPoolGap, 0.0);
      this.cplex.setParam(IloCplex.IntParam.PopulateLim,
          MultiStepEfficientAllocationILP.numSolutions);
      /*
       * Initialize indicator variables and twins. We need, per campaign, as
       * many as the number of steps.
       */
      ArrayList<IloNumVar[]> indicators = new ArrayList<IloNumVar[]>();
      ArrayList<IloNumVar[]> twinIndicators = new ArrayList<IloNumVar[]>();
      for (int j = 0; j < market.getNumberCampaigns(); j++) {
        indicators.add(j, cplex.boolVarArray(Math.floorDiv(market.getCampaign(j).getDemand(), stepSize)));
        twinIndicators.add(j, cplex.boolVarArray(Math.floorDiv(market.getCampaign(j).getDemand(), stepSize)));
      }
      /*
       * Initialize allocation variables. This is a matrix from which we are
       * going to get the allocation.
       */
      IloNumVar[][] allocationMatrixVariable = new IloNumVar[market.getNumberUsers()][];
      for (int i = 0; i < market.getNumberUsers(); i++) {
        allocationMatrixVariable[i] = cplex.intVarArray(
            market.getNumberCampaigns(), 0, Integer.MAX_VALUE);
      }
      /* Objective function */
      IloLinearNumExpr obj = this.cplex.linearNumExpr();
      for (int j = 0; j < market.getNumberCampaigns(); j++) {
        int currentStep = this.stepSize;
        for (int k = 0; k < indicators.get(j).length; k++) {
          obj.addTerm(this.objectiveFunction.getObjective(market.getCampaign(j).getReward(), market.getCampaign(j).getDemand(), currentStep) - market.getCampaign(j).getReserve() * currentStep, indicators.get(j)[k]);
          currentStep += this.stepSize;
        }
      }
      this.cplex.addMaximize(obj);
      // Constraint (1). Allocation from user cannot be more than supply.
      for (int i = 0; i < market.getNumberUsers(); i++) {
        IloLinearNumExpr expr = cplex.linearNumExpr();
        for (int j = 0; j < market.getNumberCampaigns(); j++) {
          expr.addTerm(1.0, allocationMatrixVariable[i][j]);
        }
        this.cplex.addLe(expr, market.getUser(i).getSupply());
      }
      // Constraint (2). Allocation from users not connected to campaigns should be zero.
      for (int i = 0; i < market.getNumberUsers(); i++) {
        for (int j = 0; j < market.getNumberCampaigns(); j++) {
          if (!market.isConnected(i, j)) {
            this.cplex.addEq(0, allocationMatrixVariable[i][j]);
          }
        }
      }
      // Constraint (3). Rewards according to step size.
      for (int j = 0; j < market.getNumberCampaigns(); j++) {
        int currentStep = this.stepSize;
        for (int k = 0; k < indicators.get(j).length; k++) {
          /*
           * Control constraint: if the indicator y_j^k=1, then its twin must be
           * zero. Otherwise, the twin is free.
           */
          this.cplex.addLe(
              indicators.get(j)[k],
              this.cplex.sum(1.0,
                  this.cplex.prod(-1.0, twinIndicators.get(j)[k])));
          /* Compute the total allocation to campaign j from all users. */
          double coeff = 1.0 / (double) currentStep;
          IloLinearNumExpr sumOfAlloc = cplex.linearNumExpr();
          for (int i = 0; i < market.getNumberUsers(); i++) {
            if (market.isConnected(i, j)) {
              sumOfAlloc.addTerm(coeff, allocationMatrixVariable[i][j]);
            }
          }
          /*
           * The allocation must be at least as much as the step, where the step
           * indicator is 1
           */
          this.cplex.addGe(sumOfAlloc, indicators.get(j)[k]);
          /*
           * If y_j^k = 1 then upper bound allocation to ensure the allocation
           * is exactly that given by the step.
           */
          this.cplex.addLe(
              sumOfAlloc,
              this.cplex.sum(
                  indicators.get(j)[k],
                  this.cplex.prod(
                      Integer.MAX_VALUE,
                      this.cplex.sum(1.0,
                          this.cplex.prod(-1.0, indicators.get(j)[k])))));
          /* Control for the twin variable indicator */
          for (int l = 0; l < indicators.get(j).length; l++) {
            /*
             * Here we implement the constraint that if an indicator for a step
             * of a campaign is 1, say y_j^k=1, Then, all other step indicators
             * for that campaign must be 0, .i.e., y_j^l=0, for l\nek
             */
            if (l != k) {
              this.cplex.addLe(this.cplex.prod(-1.0, indicators.get(j)[l]),
                  this.cplex.prod(Integer.MAX_VALUE, twinIndicators.get(j)[k]));
              this.cplex.addLe(
                  indicators.get(j)[l],
                  this.cplex.prod(1.0 * Integer.MAX_VALUE,
                      twinIndicators.get(j)[k]));
            }
          }
          currentStep += this.stepSize;
        }
      }
      // Constraint (4). The allocation is zero if all the indicators are zero.
      for (int j = 0; j < market.getNumberCampaigns(); j++) {
        IloLinearNumExpr sumOfAlloc = cplex.linearNumExpr();
        /* Allocation constraint */
        for (int i = 0; i < market.getNumberUsers(); i++) {
          if (market.isConnected(i, j)) {
            sumOfAlloc.addTerm(1.0, allocationMatrixVariable[i][j]);
          }
        }
        IloLinearNumExpr sumOfIndicators = cplex.linearNumExpr();
        for (int k = 0; k < indicators.get(j).length; k++) {
          sumOfIndicators.addTerm(1.0, indicators.get(j)[k]);
        }
        this.cplex.addLe(sumOfAlloc,
            this.cplex.prod(Double.MAX_VALUE, sumOfIndicators));
      }
      // Constraint (5). Campaigns can only see a number of users according to their level.
      for (int i = 0; i < market.getNumberUsers(); i++) {
        for (int j = 0; j < market.getNumberCampaigns(); j++) {
          this.cplex.addLe(
              allocationMatrixVariable[i][j],
              Math.floor(market.getCampaign(j).getLevel() * market.getUser(i).getSupply()));
        }
      }

      // Solve the program. 
      this.cplex.solve();
      if (cplex.populate()) {
        int numsol = cplex.getSolnPoolNsolns();
        int numsolreplaced = cplex.getSolnPoolNreplaced();
        // Print some information. 
        if (this.verbose) {
          System.out.println("***********************************Populate");
          System.out.println("The solution pool contains " + numsol
              + " solutions.");
          System.out.println(numsolreplaced
              + " solutions were removed due to the "
              + "solution pool relative gap parameter.");
          System.out.println("In total, " + (numsol + numsolreplaced)
              + " solutions were generated.");
          System.out.println("Solution status = " + this.cplex.getStatus());
          System.out.println("Solution value  = " + this.cplex.getObjValue());
          for (int j = 0; j < market.getNumberCampaigns(); j++) {
            System.out.println("Indicators for campaign " + j);
            double[] indicatorSol = this.cplex.getValues(indicators.get(j));
            for (int i = 0; i < indicatorSol.length; i++) {
              System.out
                  .println("I[" + j + "][" + i + "] = " + indicatorSol[i]);
            }
            double[] twinIndicatorSol = this.cplex.getValues(twinIndicators
                .get(j));
            for (int i = 0; i < twinIndicatorSol.length; i++) {
              System.out.println("Itwin[" + j + "][" + i + "] = "
                  + twinIndicatorSol[i]);
            }
          }
        }
        // Store all the solutions in an ArrayList.
        ArrayList<int[][]> Solutions = new ArrayList<>();
        for (int l = 0; l < numsol; l++) {
          /*
           * The solution should be a matrix of integers. However, CPLEX returns
           * a matrix of doubles. So we are going to have to cast this into
           * integers.
           */
          int[][] sol = new int[market.getNumberUsers()][market.getNumberCampaigns()];
          double[][] solDouble = new double[market.getNumberUsers()][market.getNumberCampaigns()];
          for (int i = 0; i < market.getNumberUsers(); i++) {
            solDouble[i] = this.cplex.getValues(allocationMatrixVariable[i], l);
            /*
             * Unfortunately in Java the only way to cast your array is to
             * iterate through each element and cast them one by one
             */
            for (int j = 0; j < market.getNumberCampaigns(); j++) {
              sol[i][j] = (int) Math.round(solDouble[i][j]);
            }
          }
          Solutions.add(sol);
          if (this.verbose) {
            Printer.printMatrix(sol);
            System.out.println();
          }
        }
        this.cplex.end();
        return new MarketAllocation(market, Solutions.get(0));
      }
    } catch (IloException e) {
      // Report that CPLEX failed.
      e.printStackTrace();
      throw new AllocationException(AllocationErrorCodes.CPLEX_FAILED);
    }
    // If we ever do reach this point, then we don't really know what happened.
    throw new AllocationException(AllocationErrorCodes.UNKNOWN_ERROR);
  }

  @Override
  public ObjectiveFunction getObjectiveFunction() {
    // TODO Auto-generated method stub
    return null;
  }

}
