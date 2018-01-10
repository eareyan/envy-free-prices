package waterfall;

import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import allocations.error.AllocationAlgoException;
import allocations.interfaces.AllocationAlgo;
import allocations.objectivefunction.SingleStepObjectiveFunction;
import allocations.objectivefunction.interfaces.ObjectiveFunction;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * Implements the waterfall algorithm.
 * 
 * @author Enrique Areyan Viqueira
 *
 * @param <M>
 * @param <G>
 * @param <B>
 */
public class Waterfall<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> implements AllocationAlgo<M, G, B> {

  /**
   * Market object in which to run the Waterfall algorithm.
   */
  private final M market;

  /**
   * Comparator of bids by 2nd price.
   */
  private final ComparatorBidsBy2ndPrice<G> bidsComparatorBy2ndPrice = new ComparatorBidsBy2ndPrice<G>();
  
  /**
   * Reserve price.
   */
  private final double reserve;

  /**
   * Constructor. 
   * 
   * @param market
   * @param reserve
   */
  public Waterfall(M market, double reserve) {
    this.market = market;
    this.reserve = reserve;
  }
  
  /**
   * Constructor for reserve price 0.0.
   * 
   * @param market
   */
  public Waterfall(M market) {
    this.market = market;
    this.reserve = 0.0;
  }

  /**
   * Run method.
   * 
   * @throws GoodsException
   */
  public WaterfallSolution<M, G, B> run() throws GoodsException {

    // Output structures.
    Table<G, B, Integer> allocation = HashBasedTable.create();
    Table<G, B, Double> prices = HashBasedTable.create();

    // Initialize the total allocation of each bidder to be zero, the allocation to be zero, and prices to be zero.
    Map<B, Integer> totalAllocationToBidder = new HashMap<B, Integer>();
    for (B bidder : this.market.getBidders()) {
      totalAllocationToBidder.put(bidder, 0);
      for (G good : this.market.getGoods()) {
        allocation.put(good, bidder, 0);
        prices.put(good, bidder, Double.POSITIVE_INFINITY);
      }
    }

    // Initial set of goods.
    Set<G> goods = new HashSet<G>(this.market.getGoods());

    // Initialize the remaining supply of each good to be their initial supply.
    for (G good : this.market.getGoods()) {
      good.setRemainingSupply(good.getSupply());
      goods.add(good);
    }

    // Initial set of bidders.
    Set<B> bidders = new HashSet<B>(this.market.getBidders());
    // Compute the bidders that cannot afford the reserve price
    Set<B> biddersBelowReserve = new HashSet<B>();
    for(B bidder: bidders) {
      if(bidder.getReward() < this.reserve * bidder.getDemand()) {
        biddersBelowReserve.add(bidder);
      }
    }
    // Eliminate all bidders that cannot afford reserve
    bidders.removeAll(biddersBelowReserve);

    while (true) {
      // System.out.println("*************");
      // Construct a set of feasible bidders, i.e., bidders that can be completely satisfied with the remaining supply.
      // Also, compute the highest bid and the corresponding bidder.
      Set<B> feasibleBidders = new HashSet<B>();
      double highestBid = Double.NEGATIVE_INFINITY;
      B higestBidder = null;
      for (B bidder : bidders) {
        int totalAvailableSupply = 0;
        for (G good : goods) {
          if (bidder.demandsGood(good) && good.getRemainingSupply() > 0) {
            totalAvailableSupply += good.getRemainingSupply();
          }
        }
        // Check that the bidder can be satisfied with the remaining supply.
        if (totalAvailableSupply >= bidder.getDemand() - totalAllocationToBidder.get(bidder)) {
          feasibleBidders.add(bidder);
          double bid = bidder.getReward() / (double) bidder.getDemand();
          // System.out.println("Candidate bid = " + bid);
          if (highestBid < bid) {
            highestBid = bid;
            higestBidder = bidder;
          }
        }
      }
      // If there are no more bids, we are done.
      if (highestBid == Double.NEGATIVE_INFINITY) {
        break;
      }
      // System.out.println(feasibleBidders);
      // System.out.println("highestBid = " + highestBid);
      // System.out.println("higestBidder = " + higestBidder);
      // Construct a list of bids for goods (still in supply) that are in the demand set of the highest bidder.
      List<Bids<G>> listOfBids = new ArrayList<Bids<G>>();
      for (G good : goods) {
        if (higestBidder.demandsGood(good)) {
          List<Double> bids = new ArrayList<Double>();
          for (B bidder : feasibleBidders) {
            if (bidder.demandsGood(good)) {
              bids.add(bidder.getReward() / (double) bidder.getDemand());
              bids.add(this.reserve);
            }
          }
          listOfBids.add(new Bids<G>(good, bids));
        }
      }
      // Order the list of bids in ascending order (lowest to highest) of second price
      Collections.sort(listOfBids, this.bidsComparatorBy2ndPrice);
      // System.out.println("listOfBids = " + listOfBids);
      G good = listOfBids.get(0).getGood();
      double price = listOfBids.get(0).get2ndHighest();

      // System.out.println("good = " + good);
      // System.out.println("price = " + price);
      // Allocate goods to the highest bidder at the computed price.
      allocation.put(good, higestBidder, Math.min(higestBidder.getDemand() - totalAllocationToBidder.get(higestBidder), good.getRemainingSupply()));
      prices.put(good, higestBidder, price);
      // System.out.println("Allocation = " + allocation);
      // System.out.println("Prices = " + prices);

      // Keep track of the total given to the bidder.
      totalAllocationToBidder.put(higestBidder, totalAllocationToBidder.get(higestBidder) + allocation.get(good, higestBidder));
      // System.out.println("totalAllocationToBidder = " + totalAllocationToBidder);

      // Check if the highest bidder is satisfied and deleted it.
      if (totalAllocationToBidder.get(higestBidder) == higestBidder.getDemand()) {
        bidders.remove(higestBidder);
      }

      // Decrement goods supply.
      good.setRemainingSupply(good.getRemainingSupply() - allocation.get(good, higestBidder));
      // System.out.println("good = " + good);

      // Check if the good is exhausted and delete it.
      if (good.getRemainingSupply() == 0) {
        goods.remove(good);
      }
    }
    WaterfallSolution<M, G, B> waterfallSolution = new WaterfallSolution<M, G, B>(this.market, allocation, prices);
    //waterfallSolution.printAllocationTable();
    //waterfallSolution.printPricesTable();
    return waterfallSolution;
  }

  @Override
  public MarketAllocation<M, G, B> Solve(M market) throws IloException, AllocationAlgoException, BidderCreationException, GoodsCreationException, AllocationException, GoodsException, MarketAllocationException {
    return this.run().getAllocation();
  }

  @Override
  public ObjectiveFunction getObjectiveFunction() {
    return new SingleStepObjectiveFunction();
  }

}
