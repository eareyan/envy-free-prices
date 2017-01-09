package singleminded;

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
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import algorithms.pricing.error.PrincingAlgoException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Implements an LP specialized for the Single-Minded case. Given an allocation
 * and a Single-Minded market, solves for a set of prices such that winners are
 * envy-free and losers are as envy-free as possible, while maximizing seller
 * revenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleMindedPricingLP<G extends Goods, B extends Bidder<G>> {

  /**
   * Verbose flag.
   */
  private boolean verbose = false;

  /**
   * MarketAllocation on which prices will be computed.
   */
  private final MarketAllocation<SingleMindedMarket<G, B>, G, B> allocatedMarket;

  /**
   * Cplex object.
   */
  private IloCplex cplex;

  /**
   * Linear constraints to be used in the LP.
   */
  private ArrayList<IloRange> linearConstrains;

  /**
   * Prices variables to be used in the LP.
   */
  private IloNumVar[] prices;

  /**
   * LoserSlack variables to be used in the LP.
   */
  private IloNumVar[] losersSlack;

  /**
   * Map from goods to indeces.
   */
  private final HashMap<G, Integer> goodToPriceIndex;

  /**
   * Map from bidders to indices.
   */
  private final HashMap<B, Integer> bidderToSlackIndex;

  /**
   * Stores the Statistics. Gets populated once, i.e., implements singleton.
   */
  private PricesStatistics<SingleMindedMarket<G, B>, G, B> statistics;

  /**
   * Constructor.
   * 
   * @param allocatedMarket
   * @throws IloException
   */
  public SingleMindedPricingLP(MarketAllocation<SingleMindedMarket<G, B>, G, B> allocatedMarket) throws IloException {
    this.allocatedMarket = allocatedMarket;
    this.cplex = new IloCplex();

    this.goodToPriceIndex = new HashMap<G, Integer>();
    for (int i = 0; i < this.allocatedMarket.getMarket().getNumberGoods(); i++) {
      this.goodToPriceIndex.put(this.allocatedMarket.getMarket().getGoods().get(i), i);
    }
    this.bidderToSlackIndex = new HashMap<B, Integer>();
    for (int j = 0; j < this.allocatedMarket.getMarket().getNumberBidders(); j++) {
      this.bidderToSlackIndex.put(this.allocatedMarket.getMarket().getBidders().get(j), j);
    }
  }

  /**
   * This method generates the individual rationality conditions. The individual
   * rationality condition states that winners do not pay more than their
   * reward.
   * 
   * @throws IloException
   *           in case the LP failed.
   * @throws MarketAllocationException
   */
  private void generateIndividualRationalityConditions() throws IloException, MarketAllocationException {
    for (B bidder : this.allocatedMarket.getMarket().getBidders()) {
      // Check if this bidder received at least one good.
      if (!this.allocatedMarket.isBidderBundleZero(bidder)) {
        IloLinearNumExpr lhs = this.cplex.linearNumExpr();
        for (G good : this.allocatedMarket.getMarket().getGoods()) {
          if (this.allocatedMarket.getAllocation(good, bidder) == 1) {
            lhs.addTerm(1.0, this.prices[this.goodToPriceIndex.get(good)]);
          }
        }
        this.linearConstrains.add(this.cplex.addLe(lhs, this.allocatedMarket.marginalValue(bidder)));
      }
    }
  }

  /**
   * Generates the losers conditions.
   * 
   * @throws MarketAllocationException
   * @throws IloException
   */
  private void generateLosersConditions() throws MarketAllocationException, IloException {
    for (B bidder : this.allocatedMarket.getMarket().getBidders()) {
      if (this.allocatedMarket.isBidderBundleZero(bidder)) {
        IloLinearNumExpr lhs = this.cplex.linearNumExpr();
        for (G good : this.allocatedMarket.getMarket().getGoods()) {
          if (bidder.demandsGood(good)) {
            lhs.addTerm(1.0, this.prices[this.goodToPriceIndex.get(good)]);
          }
        }
        lhs.addTerm(1.0, this.losersSlack[this.bidderToSlackIndex.get(bidder)]);
        this.linearConstrains.add(this.cplex.addGe(lhs, bidder.getReward()));
      }
    }
  }

  /**
   * This method generates the market clearance conditions. This conditions
   * states that prices of unallocated classes must be zero.
   * 
   * @throws IloException - in case the LP failed.
   * @throws MarketAllocationException
   */
  private void generateMarketClearanceConditions() throws IloException, MarketAllocationException {
    for (G good : this.allocatedMarket.getMarket().getGoods()) {
      if (this.allocatedMarket.allocationFromGood(good) == 0) {
        this.linearConstrains.add(this.cplex.addLe(this.prices[this.goodToPriceIndex.get(good)], 0.0));
        this.linearConstrains.add(this.cplex.addGe(this.prices[this.goodToPriceIndex.get(good)], 0.0));
      }
    }
  }

  /**
   * This method implements conditions so that the vector of prices is not
   * unbounded. We will simply constrain the price of a good to be that of the
   * highest reward of the market.
   * 
   * @throws IloException
   *           in case the LP failed
   */
  private void generateBoundConditions() throws IloException {
    double highestReward = this.allocatedMarket.getMarket().getHighestReward();
    for (G good : this.allocatedMarket.getMarket().getGoods()) {
      this.linearConstrains.add(this.cplex.addLe(this.prices[this.goodToPriceIndex.get(good)], Math.ceil(highestReward)));
    }
  }

  /**
   * This method generates the objective function to be maximized by the LP. The
   * function is seller revenue.
   * 
   * @throws IloException
   * @throws MarketAllocationException
   */
  private void generateObjectiveFunction() throws IloException, MarketAllocationException {
    // Create the objective function, i.e., the sum of all the prices.
    IloLinearNumExpr objective = this.cplex.linearNumExpr();
    for (B bidder : this.allocatedMarket.getMarket().getBidders()) {
      objective.addTerm(-1.0, this.losersSlack[this.bidderToSlackIndex.get(bidder)]);
      for (G good : this.allocatedMarket.getMarket().getGoods()) {
        objective.addTerm(this.allocatedMarket.getAllocation(good, bidder), this.prices[this.goodToPriceIndex.get(good)]);
      }
    }
    this.cplex.addMaximize(objective);
  }

  /**
   * This method creates the LP.
   * 
   * @throws MarketAllocationException
   *
   */
  private void createLP() throws MarketAllocationException {
    try {
      if (!this.verbose) {
        this.cplex.setOut(null);
      }
      // Create Cplex variables.
      this.linearConstrains = new ArrayList<IloRange>();
      cplex.setParam(IloCplex.BooleanParam.PreInd, false);
      // Create the variables. Vector of prices.
      this.prices = this.cplex.numVarArray(this.allocatedMarket.getMarket().getNumberGoods(), 0.0, Double.MAX_VALUE);
      this.losersSlack = this.cplex.numVarArray(this.allocatedMarket.getMarket().getNumberBidders(), 0.0, Double.MAX_VALUE);
      // Create Objective Function.
      this.generateObjectiveFunction();
      // Create constraints.
      this.generateIndividualRationalityConditions();
      this.generateLosersConditions();
      this.generateBoundConditions();
      this.generateMarketClearanceConditions();
    } catch (IloException e) {
      System.out.println("Exception: ==>");
      e.printStackTrace();
    }
  }

  /**
   * Solves the LP.
   * 
   * @return an immutable map, mapping goods to prices.
   * @throws PrincingAlgoException 
   */
  private ImmutableMap<G, Double> Solve() throws PrincingAlgoException {
    try {
      // Solve the LP.
      if (this.cplex.solve()) {
        double[] LP_Prices = this.cplex.getValues(this.prices);
        Builder<G, Double> result = ImmutableMap.<G, Double> builder();
        for (G good : this.allocatedMarket.getMarket().getGoods()) {
          result.put(good, LP_Prices[this.goodToPriceIndex.get(good)]);
        }
        //double[] Slacks = this.cplex.getValues(this.losersSlack);
        return result.build();
      }
      if (this.verbose) {
        System.out.println("Solution status = " + this.cplex.getStatus());
        System.out.println("Solution value  = " + this.cplex.getObjValue());
      }
    } catch (IloException e) {
      System.out.println("Exception: ==>");
      e.printStackTrace();
    }
    throw new PrincingAlgoException("Could not determine prices for Single-Minded Market.");
  }

  /**
   * This is the only public method of this class. Upon calling this method, one
   * gets a PricesStatistics object with the result of running the LP on the
   * provided market and allocation.
   * 
   * @return PricesStatistics object
   * @throws MarketAllocationException
   * @throws PrincingAlgoException 
   */
  public PricesStatistics<SingleMindedMarket<G, B>, G, B> getStatistics(long time) throws MarketAllocationException, PrincingAlgoException {
    if (this.statistics == null) {
      this.createLP();
      ImmutableMap<G, Double> prices = this.Solve();
      this.statistics = new PricesStatistics<SingleMindedMarket<G, B>, G, B>(
          new MarketOutcome<SingleMindedMarket<G, B>, G, B>(
              this.allocatedMarket, prices), time);
    }
    return this.statistics;
  }
  
  /**
   * Wrapper method. 
   * 
   * @return
   * @throws MarketAllocationException
   * @throws PrincingAlgoException
   */
  public PricesStatistics<SingleMindedMarket<G, B>, G, B> getStatistics() throws MarketAllocationException, PrincingAlgoException {
    return this.getStatistics(-1);
  }
    

}
