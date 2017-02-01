package singleton.algorithms;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import algorithms.pricing.error.PrincingAlgoException;
import allocations.objectivefunction.SingleStepObjectiveFunction;
import singleton.structures.SingletonMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.UnitDemandMarketAllocationFactory;
import unitdemand.algorithms.EVPApproximation;
import unitdemand.structures.UnitDemandException;
import unitdemand.structures.UnitDemandMarketOutcome;

/**
 * This class is a wrapper to implement EVP Approximation in the special case of singleton markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingletonEVP {

  /**
   * Singleton Market
   */
  SingletonMarket<Goods, Bidder<Goods>> market;

  /**
   * Valuation Matrix
   */
  private final double[][] V;

  /**
   * Constructor.
   * 
   * @param market
   */
  public SingletonEVP(SingletonMarket<Goods, Bidder<Goods>> market) {
    this.market = market;
    this.V = UnitDemandMarketAllocationFactory.getValuationMatrixFromMarket(market);
  }

  /**
   * Solves for EVP prices. 
   * 
   * @return A prices statistics object.
   * @throws PrincingAlgoException
   * @throws UnitDemandException
   * @throws MarketAllocationException
   * @throws MarketOutcomeException
   */
  public PricesStatistics<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> Solve() throws PrincingAlgoException, UnitDemandException, MarketAllocationException, MarketOutcomeException {
    UnitDemandMarketOutcome evpUnitDemandOutcome = new EVPApproximation(this.V).Solve();
    int[][] evpAllocation = evpUnitDemandOutcome.getMarketAllocation().getAllocation();
    HashBasedTable<Goods, Bidder<Goods>, Integer> allocation = HashBasedTable.create();
    // Allocation
    int i = 0;
    for (Goods good : this.market.getGoods()) {
      int j = 0;
      for (Bidder<Goods> bidder : this.market.getBidders()) {
        allocation.put(good, bidder, evpAllocation[i][j]);
        j++;
      }
      i++;
    }
    double[] p = evpUnitDemandOutcome.getPrices();
    // Prices
    i = 0;
    Builder<Goods, Double> result = ImmutableMap.<Goods, Double> builder();
    for (Goods good : this.market.getGoods()) {
      result.put(good, p[i]);
      i++;
    }
    MarketOutcome<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> z = new MarketOutcome<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
        new MarketAllocation<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market, allocation,
            new SingleStepObjectiveFunction()),
        result.build());
    return new PricesStatistics<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(z);
  }

}
