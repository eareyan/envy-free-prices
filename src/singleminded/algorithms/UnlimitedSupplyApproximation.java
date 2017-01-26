package singleminded.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import singleminded.structures.SingleMindedMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.comparators.BiddersComparatorByRewardToImpressionsRatio;
import structures.comparators.MarketOutcomeComparatorBySellerRevenue;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;
import allocations.objectivefunction.SingleStepObjectiveFunction;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This class implements the logarithmic approximation for items in Unlimited Supply as states in Guruswami et.al. Note that in the case of limited supply, this
 * algorithm does not produces an envy-free pricing.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnlimitedSupplyApproximation {

  /**
   * Input market. This needs to be a singleton market.
   */
  private final SingleMindedMarket<Goods, Bidder<Goods>> market;

  /**
   * A list of ordered bidders.
   */
  private final List<Bidder<Goods>> orderedBidders;

  /**
   * Constructor.
   * 
   * @param market
   */
  public UnlimitedSupplyApproximation(SingleMindedMarket<Goods, Bidder<Goods>> market) {
    this.market = market;
    this.orderedBidders = new ArrayList<Bidder<Goods>>(this.market.getBidders());
    Collections.sort(this.orderedBidders, new BiddersComparatorByRewardToImpressionsRatio());

  }

  /**
   * Runs the algorithm
   * 
   * @return
   * @throws MarketAllocationException
   * @throws MarketOutcomeException
   */
  public PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> Solve() throws MarketAllocationException, MarketOutcomeException {
    Set<Double> seenPrices = new HashSet<Double>();
    List<MarketOutcome<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>> outcomes = new ArrayList<MarketOutcome<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>>();
    for (Bidder<Goods> bidder : this.orderedBidders) {
      double price = bidder.getReward() / bidder.getDemand();
      if (!seenPrices.contains(price)) {
        outcomes.add(this.computeOutcome(price));
        seenPrices.add(price);
      }
    }
    Collections.sort(outcomes, new MarketOutcomeComparatorBySellerRevenue<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    return new PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(outcomes.get(0));
  }

  /**
   * Given a price, computes an outcome where all items are prices at the price.
   * 
   * @param price
   * @return
   * @throws MarketAllocationException
   * @throws MarketOutcomeException
   */
  public MarketOutcome<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> computeOutcome(double price) throws MarketAllocationException, MarketOutcomeException {
    int[][] alloc = new int[this.market.getNumberGoods()][this.market.getNumberBidders()];
    for (Bidder<Goods> bidder : this.orderedBidders) {
      // Numerical tolerance.
      if (Math.abs(price * bidder.getDemand() - bidder.getReward()) <= 0.000000000001) {
        boolean demandSetAvailable = true;
        goodsLoop: for (Goods good : this.market.getGoods()) {
          if (bidder.demandsGood(good)) {
            for (Bidder<Goods> otherBidder : this.market.getBidders()) {
              if (alloc[this.market.getGoodIndex(good)][this.market.getBidderIndex(otherBidder)] == 1) {
                demandSetAvailable = false;
                break goodsLoop;
              }
            }
          }
        }
        if (demandSetAvailable) {
          for (Goods good : bidder.getDemandSet()) {
            alloc[this.market.getGoodIndex(good)][this.market.getBidderIndex(bidder)] = 1;
          }
        }
      }
    }
    /*
     * Create appropriate structures to return the outcome of the market.
     */
    // Allocation
    HashBasedTable<Goods, Bidder<Goods>, Integer> allocation = HashBasedTable.create();
    for (Goods good : this.market.getGoods()) {
      for (Bidder<Goods> bidder : this.market.getBidders()) {
        allocation.put(good, bidder, alloc[this.market.getGoodIndex(good)][this.market.getBidderIndex(bidder)]);
      }
    }
    // Prices
    Builder<Goods, Double> prices = ImmutableMap.<Goods, Double> builder();
    for (Goods good : this.market.getGoods()) {
      prices.put(good, price);
    }
    return new MarketOutcome<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
        new MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(this.market, allocation, new SingleStepObjectiveFunction()),
        prices.build());
  }

}
