package algorithms.pricing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.comparators.BiddersComparatorByRewardToImpressionsRatio;
import structures.comparators.MarketOutcomeComparatorBySellerRevenue;
import structures.exceptions.MarketAllocationException;
import allocations.objectivefunction.SingleStepObjectiveFunction;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Implements a simple pricing scheme for size-interchangeable markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SimplePricing {

  /**
   * Input Market.
   */
  private final Market<Goods, Bidder<Goods>> market;

  /**
   * A list of ordered bidders.
   */
  private final List<Bidder<Goods>> orderedBidders;
  
  /**
   * Start time, for statistics purposes.
   */
  private final long startTime;

  /**
   * Constructor.
   * 
   * @param market
   */
  public SimplePricing(Market<Goods, Bidder<Goods>> market) {
    this.startTime = System.nanoTime();
    this.market = market;
    this.orderedBidders = new ArrayList<Bidder<Goods>>(this.market.getBidders());
    Collections.sort(this.orderedBidders, new BiddersComparatorByRewardToImpressionsRatio());
  }

  /**
   * Solves for a revenue-maximizing outcome.
   * 
   * @throws MarketAllocationException
   */
  public PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> Solve() throws MarketAllocationException {
    Set<Double> seenPrices = new HashSet<Double>();
    List<MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>> listOfOutcomes = new ArrayList<MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>>();
    for (Bidder<Goods> bidder : this.orderedBidders) {
      double price = bidder.getReward() / bidder.getDemand();
      if (!seenPrices.contains(price)) {
        listOfOutcomes.add(this.computeOutcome(price));
        seenPrices.add(price);
      }
    }
    Collections.sort(listOfOutcomes, new MarketOutcomeComparatorBySellerRevenue<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    return new PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(listOfOutcomes.get(0), System.nanoTime() - this.startTime);
  }

  /**
   * Given a price and a supply map, try to allocate each bidder of this market, in order. Returns a MarketOutcome.
   * 
   * @param price
   * @param supply
   * @return
   * @throws MarketAllocationException
   */
  public MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> computeOutcome(double price) throws MarketAllocationException {
    // Initialize Supply
    Map<Goods, Integer> supply = new HashMap<Goods, Integer>();
    for (Goods good : this.market.getGoods()) {
      supply.put(good, good.getSupply());
    }
    // Initialize Allocation.
    HashBasedTable<Goods, Bidder<Goods>, Integer> allocation = HashBasedTable.create();
    for (Goods good : this.market.getGoods()) {
      for (Bidder<Goods> bidder : this.market.getBidders()) {
        allocation.put(good, bidder, 0);
      }
    }
    // Allocate Bidders.
    for (Bidder<Goods> bidder : this.orderedBidders) {
      if (this.isSatisfiable(price, bidder, supply)) {
        int total = 0;
        for (Goods good : this.market.getGoods()) {
          if (bidder.demandsGood(good)) {
            int alloc = Math.min(supply.get(good), bidder.getDemand() - total);
            total += alloc;
            supply.put(good, supply.get(good) - alloc);
            allocation.put(good, bidder, alloc);
            // Optimization: break if this bidder is already satisfied.
            if (bidder.getDemand() - total == 0) {
              break;
            }
          }
        }
      }
    }
    // Prices
    Builder<Goods, Double> prices = ImmutableMap.<Goods, Double> builder();
    for (Goods good : this.market.getGoods()) {
      prices.put(good, price);
    }
    return new MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(new MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
        this.market, allocation, new SingleStepObjectiveFunction()), prices.build());
  }

  /**
   * Given a single price for all items and a bidder, returns true if the bidder can afford its bundle at the given price and there are enough impressions left
   * to satisfy this bidder
   * 
   * @param price
   * @param bidder
   * @return true if and only if the bidder can be allocated at the given prices and the remaining supply.
   */
  public boolean isSatisfiable(double price, Bidder<Goods> bidder, Map<Goods, Integer> supply) {
    // Numerical tolerance.
    if (price * bidder.getDemand() - bidder.getReward() > 0.000000000001) {
      return false;
    } else {
      int totalAvailableSupply = 0;
      for (Goods good : this.market.getGoods()) {
        if (bidder.demandsGood(good) && supply.get(good) > 0) {
          totalAvailableSupply += supply.get(good);
          if (totalAvailableSupply >= bidder.getDemand()) {
            return true;
          }
        }
      }
    }
    return false;
  }

}
