package singleminded.algorithms;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;

import java.util.HashMap;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.exceptions.MarketAllocationException;
import algorithms.pricing.RestrictedEnvyFreePricesLP;

/**
 * Implements an LP specialized for the Single-Minded case. Given an allocation and a Single-Minded market, solves for a set of prices such that winners are
 * envy-free and losers are as envy-free as possible, while maximizing seller revenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleMindedPricingLP<M extends SingleMindedMarket<G, B>, G extends Goods, B extends Bidder<G>> extends RestrictedEnvyFreePricesLP<M, G, B> {

  /**
   * LoserSlack variables to be used in the LP.
   */
  private IloNumVar[] losersSlack;

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
  public SingleMindedPricingLP(MarketAllocation<M, G, B> allocatedMarket) throws IloException {
    super(allocatedMarket);

    this.bidderToSlackIndex = new HashMap<B, Integer>();
    for (int j = 0; j < this.allocatedMarket.getMarket().getNumberBidders(); j++) {
      this.bidderToSlackIndex.put(this.allocatedMarket.getMarket().getBidders().get(j), j);
    }
  }

  /**
   * This method generates the objective function to be maximized by the LP. The function is seller revenue.
   * 
   * @throws IloException
   * @throws MarketAllocationException
   */
  @Override
  public void generateObjectiveFunction() throws IloException, MarketAllocationException {
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
   * Initializes the LP variables.
   * 
   * @throws IloException
   */
  @Override
  protected void initVariables() throws IloException {
    super.initVariables();
    this.losersSlack = this.cplex.numVarArray(this.allocatedMarket.getMarket().getNumberBidders(), 0.0, Double.MAX_VALUE);
  }

  /**
   * Initializes the LP constraints.
   * 
   * @throws IloException
   * @throws MarketAllocationException
   */
  @Override
  protected void createConstraints() throws IloException, MarketAllocationException {
    // Create constraints.
    this.generateIndividualRationalityConditions();
    this.generateBoundConditions();
    if (this.marketClearanceConditions) {
      this.generateMarketClearanceConditions();
    }
    this.generateLosersConditions();
  }

}
