package algorithms.pricing;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;

import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.exceptions.MarketAllocationException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * LP to find restricted envy-free prices. Implements Compact Condition and
 * Individual Rationality. Optionally, reserve prices can be given for goods. A
 * restricted Walrasian Equilibrium and a restricted Walrasian Equilibrium with
 * reserve can also be found by using the Market Clearance conditions.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RestrictedEnvyFreePricesLP {
  
  /**
   * Boolean to control whether or not to output information.
   */
  protected boolean verbose = false;
  
  /**
   * Market Allocation Object. Contains the market and the allocation.
   */
  protected MarketAllocation<Goods, Bidder<Goods>> allocatedMarket;
  
  /**
   * Contains all the linear constrains
   */
  protected ArrayList<IloRange> linearConstrains;
  
  /**
   * This map is used internally to map goods to indices for CPLEX variables.
   */
  private HashMap<Goods, Integer> goodToPriceIndex;
  
  /**
   * Objects needed to interface with CPlex Library.
   */
  protected IloNumVar[] prices;
  protected IloCplex cplex;
  
  /**
   * By default, set the market clearance conditions.
   */
  protected boolean marketClearanceConditions = true;

  /**
   * Constructor receives an allocated market and creates the IloCplex object.
   * 
   * @param allocatedMarket - a MarketAllocation object.
   * @throws IloException in case the LP failed.
   */
  public RestrictedEnvyFreePricesLP(MarketAllocation<Goods, Bidder<Goods>> allocatedMarket) throws IloException {
    this.allocatedMarket = allocatedMarket;
    this.cplex = new IloCplex();
  }
  
  /**
   * Constructor receives an allocated Market M, and a boolean to indicate if we
   * want to create the LP.
   * 
   * @param allocatedMarket - a MarketAllocation object.
   * @param createLP - a boolean to indicate if we want to create the LP.
   * @throws IloException in case the LP failed.
   * @throws MarketAllocationException 
   */
  public RestrictedEnvyFreePricesLP(MarketAllocation<Goods, Bidder<Goods>> allocatedMarket, boolean createLP) throws IloException, MarketAllocationException {
    this.allocatedMarket = allocatedMarket;
    this.cplex = new IloCplex();
    if (createLP) {
      this.createLP();
    }
  }
  
  /**
   * Constructor receives an allocated market and an IloCplex Object.
   * 
   * @param allocatedMarket - a MarketAllocation object.
   * @param iloObject - an IloCplex object.
   */
  public RestrictedEnvyFreePricesLP(MarketAllocation<Goods, Bidder<Goods>> allocatedMarket, IloCplex iloObject) {
    this.allocatedMarket = allocatedMarket;
    this.cplex = iloObject;
  }
  
  /**
   * Constructor receives an allocated Market M, and IloCplex Object, and a
   * boolean to indicate if we want to create the LP.
   * 
   * @param allocatedMarket - a MarketAllocation object.
   * @param iloObject - an IloCplex object.
   * @param createLP - a boolean to indicate if we want to create the LP.
   * @throws MarketAllocationException 
   */
  public RestrictedEnvyFreePricesLP(MarketAllocation<Goods, Bidder<Goods>> allocatedMarket, IloCplex iloObject, boolean createLP) throws MarketAllocationException {
    this.allocatedMarket = allocatedMarket;
    this.cplex = iloObject;
    if (createLP) {
      this.createLP();
    }
  }
  
  /**
   * Setter. Set/Unset market clearance conditions.
   * 
   * @param set - boolean. If true, set market clearance Conditions. Else, unset.
   */
  public void setMarketClearanceConditions(boolean set) {
    this.marketClearanceConditions = set;
  }
  
  /**
   * This method generate the compact conditions.
   * 
   * @throws IloException in case the LP failed.
   * @throws MarketAllocationException 
   */
  protected void generateCompactConditions() throws IloException, MarketAllocationException {
    if (this.verbose) {
      System.out.println("--- Start to generate Compact Conditions ---");
    }
    for (Goods good_i : this.allocatedMarket.getMarket().getGoods()) {
      for (Bidder<Goods> bidder : this.allocatedMarket.getMarket().getBidders()) {
        if (this.allocatedMarket.getAllocation(good_i, bidder) > 0) {
          //In this case we have to add a condition for the compact condition.
          for (Goods good_k : this.allocatedMarket.getMarket().getGoods()) {
            // If good k is connected to bidder j, and good k does not supply all of its items to bidder j.
            if (good_i != good_k && bidder.demandsGood(good_k) && this.allocatedMarket.getAllocation(good_k,bidder) < good_k.getSupply()) {
              if (this.verbose) {
                System.out.println("Add compact condition for good " + good_k
                    + " on bidder " + bidder + ", where x = "
                    + this.allocatedMarket.getAllocation(good_k,bidder));
                System.out.println("\t Price(" + good_i + ") <= Price(" + good_k + ")");
              }
              this.linearConstrains.add(this.cplex.addLe(this.cplex.sum(
                  this.cplex.prod(-1.0, this.prices[this.goodToPriceIndex.get(good_k)]),
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
   * This method generates the individual rationality conditions.
   * The individual rationality condition states that winners do not pay
   * more than their reward.
   * 
   * @throws IloException in case the LP failed.
   * @throws MarketAllocationException 
   */
  protected void generateIndividualRationalityConditions() throws IloException, MarketAllocationException {
    for (Bidder<Goods> bidder : this.allocatedMarket.getMarket().getBidders()) {
      if (!this.allocatedMarket.isBidderBundleZero(bidder)) {
        IloLinearNumExpr lhs = cplex.linearNumExpr();
        int counter = 0;
        for (Goods good : this.allocatedMarket.getMarket().getGoods()) {
          if (this.allocatedMarket.getAllocation(good, bidder) > 0) {
            lhs.addTerm(this.allocatedMarket.getAllocation(good, bidder), this.prices[this.goodToPriceIndex.get(good)]);
            counter += this.allocatedMarket.getAllocation(good, bidder);
          }
        }
        if (counter >= bidder.getDemand()) {
          this.linearConstrains.add(cplex.addLe(lhs, bidder.getReward()));
        } else {
          this.linearConstrains.add(cplex.addLe(lhs, 0));
        }
        /*System.out.println("----\n " + bidder);
        System.out.println(this.allocatedMarket.marginalValue(bidder));
        this.linearConstrains.add(cplex.addLe(lhs, this.allocatedMarket.marginalValue(bidder)));*/
      }
    }
  }
  
  /**
   * This method implements conditions so that the vector of prices is not
   * unbounded. We will simply constrain the price of a good to be that of the
   * highest reward of the market.
   * 
   * @throws IloException in case the LP failed
   */
  protected void generateBoundConditions() throws IloException {
    double highestReward = this.allocatedMarket.getMarket().getHighestReward();
    for (int i = 0; i < this.allocatedMarket.getMarket().getNumberGoods(); i++) {
      this.linearConstrains.add(this.cplex.addLe(this.prices[i], Math.ceil(highestReward)));
    }
  }
  
  /**
   * This method generates the market clearance conditions.
   * This conditions states that prices of unallocated classes must be zero.
   * 
   * @throws IloException - in case the LP failed.
   * @throws MarketAllocationException 
   */
  protected void generateMarketClearanceConditions() throws IloException, MarketAllocationException {
    for (Goods good : this.allocatedMarket.getMarket().getGoods()) {
      if (this.allocatedMarket.allocationFromGood(good) == 0) {
        this.linearConstrains.add(this.cplex.addLe(this.prices[this.goodToPriceIndex.get(good)], 0.0));
        this.linearConstrains.add(this.cplex.addGe(this.prices[this.goodToPriceIndex.get(good)], 0.0));
      }
    }
  }
  
  /**
   * This method generates the market clearance condition with reserve prices.
   * This conditions state that unallocated items must be priced at the reserve.
   * 
   * @param reserve - reserve price
   * @throws IloException in case the LP failed
   * @throws MarketAllocationException 
   */
  public void generateMarketClearanceConditionsWithReserve(double reserve) throws IloException, MarketAllocationException {
    for (Goods good : this.allocatedMarket.getMarket().getGoods()) {
      if (this.allocatedMarket.allocationFromGood(good) == 0) {
        this.linearConstrains.add(this.cplex.addLe(this.prices[this.goodToPriceIndex.get(good)], reserve));
        this.linearConstrains.add(this.cplex.addGe(this.prices[this.goodToPriceIndex.get(good)], reserve));
      }
    }
  }
  
  /**
   * Set reserve prices for all goods.
   * 
   * @param reservePrices - an array of reserve prices.
   */
  public void setReservePrices(double[] reservePrices) {
    if (reservePrices.length != this.allocatedMarket.getMarket().getNumberGoods()) {
      System.out.println("Reserve Prices vector must be of same length as number of goods");
      System.exit(-1);
    }
    try {
      for (int i = 0; i < reservePrices.length; i++) {
        // System.out.println("Set reserve price of good " + i + " to " +
        // reservePrices[i]);
        this.linearConstrains.add(this.cplex.addGe(this.prices[i], reservePrices[i]));
      }
    } catch (IloException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * Set Reserve price for good i.
   * 
   * @param i - good index.
   * @param reservePrice - reserve price.
   */
  public void setReservePriceForGood(int i, double reservePrice) {
    try {
      if (this.verbose) {
        System.out.println("Setting Reserve Price of " + reservePrice + " for good " + i);
      }
      this.linearConstrains.add(this.cplex.addGe(this.prices[i], reservePrice));
    } catch (IloException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * This method creates the LP.
   * @throws MarketAllocationException 
   *
   */
  public void createLP() throws MarketAllocationException {
    try {
      if (!this.verbose) {
        this.cplex.setOut(null);
      }
      // Create a map from goods to a numbers. This gives ordering of the goods.
      this.goodToPriceIndex = new HashMap<Goods, Integer>();
      for (int i = 0; i < this.allocatedMarket.getMarket().getNumberGoods(); i++) {
        goodToPriceIndex.put(this.allocatedMarket.getMarket().getGoods().get(i), i);
      }
      // Create Cplex variables.      
      this.linearConstrains = new ArrayList<IloRange>();
      cplex.setParam(IloCplex.BooleanParam.PreInd, false);
      // Create the variables. Vector of prices.
      this.prices = this.cplex.numVarArray(this.allocatedMarket.getMarket().getNumberGoods(), 0.0, Double.MAX_VALUE);
      // Create the objective function, i.e., the sum of all the prices.
      IloLinearNumExpr objective = cplex.linearNumExpr();
      for (Goods good : this.allocatedMarket.getMarket().getGoods()) {
        for (Bidder<Goods> bidder : this.allocatedMarket.getMarket().getBidders()) {
          objective.addTerm(this.allocatedMarket.getAllocation(good, bidder), this.prices[this.goodToPriceIndex.get(good)]);
        }
      }
      this.cplex.addMaximize(objective);
      this.generateCompactConditions();
      this.generateIndividualRationalityConditions();
      this.generateBoundConditions();
      if (this.marketClearanceConditions) {
        this.generateMarketClearanceConditions();
      }
    } catch (IloException e) {
      System.out.println("Exception: ==>");
      e.printStackTrace();
    }
  }
  
  /**
   * This method solves the LP.
   * 
   * @return an EnvyFreePricesSolutionLP object with the solution of the LP.
   */
  public RestrictedEnvyFreePricesLPSolution Solve() {
    RestrictedEnvyFreePricesLPSolution Solution = new RestrictedEnvyFreePricesLPSolution(this.allocatedMarket , null, "", -1);
    try {
      // Solve the LP.
      if (this.cplex.solve()) {
        //double[] LP_Prices = NumberMethods.roundPrices(this.cplex.getValues(this.prices));
        double[] LP_Prices = this.cplex.getValues(this.prices);
        Builder<Goods, Double> result = ImmutableMap.<Goods, Double>builder();
        for(Goods good : this.allocatedMarket.getMarket().getGoods()){
          result.put(good, LP_Prices[this.goodToPriceIndex.get(good)]);
        }
        Solution = new RestrictedEnvyFreePricesLPSolution(this.allocatedMarket, result.build(), this.cplex.getStatus().toString(), this.cplex.getObjValue());
      } else {
        Solution = new RestrictedEnvyFreePricesLPSolution(this.allocatedMarket, null, this.cplex.getStatus().toString(), -1);
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
  
}
