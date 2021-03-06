package algorithms.pricing;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;

import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.MarketAllocationException;
import util.Cplex;
import algorithms.pricing.error.PrincingAlgoException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * LP to find restricted envy-free prices. Implements Compact Condition and Individual Rationality. A restricted Walrasian Equilibrium can found by using the
 * Market Clearance conditions.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RestrictedEnvyFreePricesLP<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> {

  /**
   * Boolean to control whether or not to output information.
   */
  protected boolean verbose = true;

  /**
   * Market Allocation Object. Contains the market and the allocation.
   */
  protected MarketAllocation<M, G, B> allocatedMarket;

  /**
   * Contains all the linear constrains
   */
  protected ArrayList<IloRange> linearConstrains;

  /**
   * This map is used internally to map goods to indices for CPLEX variables.
   */
  protected final HashMap<G, Integer> goodToPriceIndex;

  /**
   * Objects needed to interface with CPlex Library.
   */
  protected IloNumVar[] prices;
  protected IloCplex cplex;

  /**
   * By default, set the market clearance conditions.
   */
  protected boolean marketClearanceConditions = false;

  /**
   * Flag that indicates if the LP has been created.
   */
  protected boolean lpCreated = false;

  /**
   * Stores the Statistics. Gets populated once, i.e., implements singleton.
   */
  private PricesStatistics<M, G, B> statistics;

  /**
   * Constructor receives an allocated market and creates the IloCplex object.
   * 
   * @param allocatedMarket
   *          - a MarketAllocation object.
   * @throws IloException
   *           in case the LP failed.
   */
  public RestrictedEnvyFreePricesLP(MarketAllocation<M, G, B> allocatedMarket) throws IloException {
    this.allocatedMarket = allocatedMarket;
    //this.cplex = new IloCplex();
    this.cplex = Cplex.getCplex();
    // Create a map from goods to a numbers. This gives ordering of the goods.
    this.goodToPriceIndex = new HashMap<G, Integer>();
    for (int i = 0; i < this.allocatedMarket.getMarket().getNumberGoods(); i++) {
      this.goodToPriceIndex.put(this.allocatedMarket.getMarket().getGoods().get(i), i);
    }
  }

  /**
   * This method generates the objective function to be maximized by the LP. The function is seller revenue.
   * 
   * @throws IloException
   * @throws MarketAllocationException
   */
  protected void generateObjectiveFunction() throws IloException, MarketAllocationException {
    // Create the objective function, i.e., the sum of all the prices.
    IloLinearNumExpr objective = this.cplex.linearNumExpr();
    for (G good : this.allocatedMarket.getMarket().getGoods()) {
      for (B bidder : this.allocatedMarket.getMarket().getBidders()) {
        objective.addTerm(this.allocatedMarket.getAllocation(good, bidder), this.prices[this.goodToPriceIndex.get(good)]);
      }
    }
    this.cplex.addMaximize(objective);
  }

  /**
   * This method generate the compact conditions.
   * 
   * @throws IloException
   *           in case the LP failed.
   * @throws MarketAllocationException
   */
  protected void generateCompactConditions() throws IloException, MarketAllocationException {
    if (this.verbose) {
      System.out.println("--- Start to generate Compact Conditions ---");
    }
    for (G good_i : this.allocatedMarket.getMarket().getGoods()) {
      for (B bidder : this.allocatedMarket.getMarket().getBidders()) {
        if (this.allocatedMarket.getAllocation(good_i, bidder) > 0) {
          // In this case we have to add a condition for the compact condition.
          for (G good_k : this.allocatedMarket.getMarket().getGoods()) {
            // If good k is connected to bidder j, and good k does not supply all of its items to bidder j.
            if (good_i != good_k && bidder.demandsGood(good_k) && this.allocatedMarket.getAllocation(good_k, bidder) < good_k.getSupply()) {
              if (this.verbose) {
                System.out.println("Add compact condition for good " + good_k + " on bidder " + bidder + ", where x = "
                    + this.allocatedMarket.getAllocation(good_k, bidder));
                System.out.println("\t Price(" + good_i + ") <= Price(" + good_k + ")");
              }
              this.linearConstrains.add(this.cplex.addLe(
                  this.cplex.sum(this.cplex.prod(-1.0, this.prices[this.goodToPriceIndex.get(good_k)]),
                      this.cplex.prod(1.0, this.prices[this.goodToPriceIndex.get(good_i)])), 0.0));
            }
          }
        }
      }
    }
    if (this.verbose)
      System.out.println("--- End generate Compact Conditions ---");
  }

  /**
   * This method generates the individual rationality conditions. The individual rationality condition states that winners do not pay more than their reward.
   * 
   * @throws IloException
   *           in case the LP failed.
   * @throws MarketAllocationException
   */
  protected void generateIndividualRationalityConditions() throws IloException, MarketAllocationException {
    for (B bidder : this.allocatedMarket.getMarket().getBidders()) {
      // Check if this bidder received at least one copy of a good.
      if (!this.allocatedMarket.isBidderBundleZero(bidder)) {
        IloLinearNumExpr lhs = cplex.linearNumExpr();
        for (G good : this.allocatedMarket.getMarket().getGoods()) {
          if (this.allocatedMarket.getAllocation(good, bidder) > 0) {
            lhs.addTerm(this.allocatedMarket.getAllocation(good, bidder), this.prices[this.goodToPriceIndex.get(good)]);
          }
        }
        this.linearConstrains.add(cplex.addLe(lhs, this.allocatedMarket.marginalValue(bidder)));
      }
    }
  }

  /**
   * This method implements conditions so that the vector of prices is not unbounded. We will simply constrain the price of a good to be that of the highest
   * reward of the market.
   * 
   * @throws IloException
   *           in case the LP failed
   */
  protected void generateBoundConditions() throws IloException {
    double highestReward = this.allocatedMarket.getMarket().getHighestReward();
    for (G good : this.allocatedMarket.getMarket().getGoods()) {
      this.linearConstrains.add(this.cplex.addLe(this.prices[this.goodToPriceIndex.get(good)], Math.ceil(highestReward)));
    }
  }

  /**
   * Setter. Set/Unset market clearance conditions.
   * 
   * @param set
   *          - boolean. If true, set market clearance Conditions. Else, unset.
   */
  public void setMarketClearanceConditions(boolean set) {
    this.marketClearanceConditions = set;
  }

  /**
   * This method generates the market clearance conditions. This conditions states that prices of unallocated classes must be zero.
   * 
   * @throws IloException
   *           - in case the LP failed.
   * @throws MarketAllocationException
   */
  protected void generateMarketClearanceConditions() throws IloException, MarketAllocationException {
    System.out.println("In RestrictedEnvyFreePricesLP, generateMarketClearanceConditions = 0");
    for (G good : this.allocatedMarket.getMarket().getGoods()) {
      if (this.allocatedMarket.allocationFromGood(good) == 0) {
        this.linearConstrains.add(this.cplex.addLe(this.prices[this.goodToPriceIndex.get(good)], 0.0));
        this.linearConstrains.add(this.cplex.addGe(this.prices[this.goodToPriceIndex.get(good)], 0.0));
      }
    }
  }

  /**
   * Initializes the LP variables.
   * 
   * @throws IloException
   */
  protected void initVariables() throws IloException {
    // Create Cplex variables.
    this.linearConstrains = new ArrayList<IloRange>();
    this.cplex.setParam(IloCplex.BooleanParam.PreInd, false);
    // Create the variables. Vector of prices.
    this.prices = this.cplex.numVarArray(this.allocatedMarket.getMarket().getNumberGoods(), 0.0, Double.MAX_VALUE);
  }

  /**
   * Initializes the LP constraints.
   * 
   * @throws IloException
   * @throws MarketAllocationException
   */
  protected void createConstraints() throws IloException, MarketAllocationException {
    this.generateCompactConditions();
    this.generateIndividualRationalityConditions();
    this.generateBoundConditions();
    if (this.marketClearanceConditions) {
      this.generateMarketClearanceConditions();
    }
  }

  /**
   * This method creates the LP.
   * 
   * @throws MarketAllocationException
   * @throws IloException
   */
  public void createLP() throws MarketAllocationException, IloException {
    if (!this.lpCreated) {
      if (!this.verbose) {
        this.cplex.setOut(null);
      }
      // Initialize variables
      this.initVariables();
      // Create Objective Function.
      this.generateObjectiveFunction();
      // Create Constraints.
      this.createConstraints();
      this.lpCreated = true;
    }
  }

  /**
   * This method solves the LP.
   * 
   * @return an EnvyFreePricesSolutionLP object with the solution of the LP.
   * @throws PrincingAlgoException
   */
  public RestrictedEnvyFreePricesLPSolution<M, G, B> Solve() throws PrincingAlgoException {
    if (!this.lpCreated) {
      throw new PrincingAlgoException("To solve for restricted envy-free prices, you must first create the LP.");
    }
    RestrictedEnvyFreePricesLPSolution<M, G, B> Solution = new RestrictedEnvyFreePricesLPSolution<M, G, B>(this.allocatedMarket, null, "", -1);
    try {
      // Solve the LP.
      if (this.cplex.solve()) {
        double[] LP_Prices = this.cplex.getValues(this.prices);
        Builder<G, Double> result = ImmutableMap.<G, Double> builder();
        for (G good : this.allocatedMarket.getMarket().getGoods()) {
          result.put(good, LP_Prices[this.goodToPriceIndex.get(good)]);
        }
        Solution = new RestrictedEnvyFreePricesLPSolution<M, G, B>(this.allocatedMarket, result.build(), this.cplex.getStatus().toString(), this.cplex.getObjValue());
      } else {
        Solution = new RestrictedEnvyFreePricesLPSolution<M, G, B>(this.allocatedMarket, null, this.cplex.getStatus().toString(), -1);
      }
      if (this.verbose) {
        System.out.println("Solution status = " + this.cplex.getStatus());
        System.out.println("Solution value  = " + this.cplex.getObjValue());
      }
    } catch (IloException e) {
      System.out.println("Exception: ==>");
      e.printStackTrace();
    }
    return Solution;
  }

  /**
   * This is the only public method of this class. Upon calling this method, one gets a PricesStatistics object with the result of running the LP on the
   * provided market and allocation.
   * 
   * @return PricesStatistics object
   * @throws MarketAllocationException
   * @throws PrincingAlgoException
   * @throws IloException
   */
  public PricesStatistics<M, G, B> getStatistics(long time) throws MarketAllocationException, PrincingAlgoException, IloException {
    if (this.statistics == null) {
      this.createLP();
      this.statistics = new PricesStatistics<M, G, B>(this.Solve(), time);
    }
    return this.statistics;
  }

  /**
   * Wrapper method.
   * 
   * @return
   * @throws MarketAllocationException
   * @throws PrincingAlgoException
   * @throws IloException
   */
  public PricesStatistics<M, G, B> getStatistics() throws MarketAllocationException, PrincingAlgoException, IloException {
    return this.getStatistics(-1);
  }

}
