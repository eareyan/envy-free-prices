package singleton.algorithms;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;

import java.util.HashMap;

import singleton.structures.SingletonMarket;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.exceptions.MarketAllocationException;
import algorithms.pricing.RestrictedEnvyFreePricesLP;

/**
 * This class implements an LP pricing for the case of singleton markets.
 * 
 * @author Enrique Areyan Viqueira
 *
 * @param <M>
 * @param <G>
 * @param <B>
 */
public class SingletonPricingLP<M extends SingletonMarket<G, B>, G extends Goods, B extends Bidder<G>> extends RestrictedEnvyFreePricesLP<M, G, B> {

  /**
   * LoserSlack variables to be used in the LP.
   */
  private IloNumVar[][] slack;

  /**
   * Map from bidders to indices.
   */
  private final HashMap<G, Integer> goodToSlackIndex;

  /**
   * Map from bidders to indices.
   */
  private final HashMap<B, Integer> bidderToSlackIndex;

  /**
   * Constructor.
   * 
   * @param allocatedMarket
   * @throws IloException
   */
  public SingletonPricingLP(MarketAllocation<M, G, B> allocatedMarket) throws IloException {
    super(allocatedMarket);

    this.goodToSlackIndex = new HashMap<G, Integer>();
    for (int i = 0; i < this.allocatedMarket.getMarket().getNumberGoods(); i++) {
      this.goodToSlackIndex.put(this.allocatedMarket.getMarket().getGoods().get(i), i);
    }
    this.bidderToSlackIndex = new HashMap<B, Integer>();
    for (int j = 0; j < this.allocatedMarket.getMarket().getNumberBidders(); j++) {
      this.bidderToSlackIndex.put(this.allocatedMarket.getMarket().getBidders().get(j), j);
    }
  }

  /**
   * This method generates the objective function to be maximized by the LP. The function is seller revenue minus the slack.
   * 
   * @throws IloException
   * @throws MarketAllocationException
   */
  @Override
  protected void generateObjectiveFunction() throws IloException, MarketAllocationException {
    // Create the objective function, i.e., the sum of all the prices.
    IloLinearNumExpr objective = this.cplex.linearNumExpr();
    for (G good : this.allocatedMarket.getMarket().getGoods()) {
      for (B bidder : this.allocatedMarket.getMarket().getBidders()) {
        objective.addTerm(this.allocatedMarket.getAllocation(good, bidder), this.prices[this.goodToPriceIndex.get(good)]);
        objective.addTerm(-1.0, this.slack[this.goodToSlackIndex.get(good)][this.bidderToSlackIndex.get(bidder)]);
      }
    }
    this.cplex.addMaximize(objective);
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
        int j = this.bidderToSlackIndex.get(bidder);
        for (G good : this.allocatedMarket.getMarket().getGoods()) {
          if (bidder.demandsGood(good)) {
            this.linearConstrains.add(this.cplex.addGe(
                this.cplex.sum(this.prices[this.goodToPriceIndex.get(good)], this.slack[this.goodToSlackIndex.get(good)][j]), bidder.getReward()));
          }
        }
      }
    }
  }

  /**
   * Initializes the LP variables.
   * 
   * @throws IloException
   */
  @Override
  protected void initVariables() throws IloException {
    super.initVariables();
    this.slack = new IloNumVar[this.allocatedMarket.getMarket().getNumberGoods()][];
    for (G good : this.allocatedMarket.getMarket().getGoods()) {
      this.slack[this.goodToSlackIndex.get(good)] = this.cplex.numVarArray(this.allocatedMarket.getMarket().getNumberBidders(), 0.0, Double.MAX_VALUE);
    }
  }

  /**
   * Initializes the LP constraints.
   * 
   * @throws IloException
   * @throws MarketAllocationException
   */
  @Override
  protected void createConstraints() throws IloException, MarketAllocationException {
    super.createConstraints();
    this.generateLosersConditions();
  }
}
