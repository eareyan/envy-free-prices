package singleminded.algorithms;

import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.comparators.BiddersComparatorByRToSqrtIRatio;
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

/**
 * This class implements a lightweight version of the greedy allocation for
 * single-minded markets. @see allocations.greedy.GreedyAllocation
 * 
 * @author Enrique Areyan Viqueira.
 *
 * @param <M>
 * @param <G>
 * @param <B>
 */
public class SingleMindedGreedyAllocation<M extends SingleMindedMarket<G, B>, G extends Goods, B extends Bidder<G>> implements AllocationAlgo<SingleMindedMarket<G, B>, G, B> {
  /**
   * Bidder comparator.
   */
  protected Comparator<B> BidderComparator;
  
  /**
   * Constructor.
   * Uses the default bidder comparator. 
   */
  public SingleMindedGreedyAllocation() {
    this.BidderComparator = new BiddersComparatorByRToSqrtIRatio<G, B>();
  }
  
  /**
   * Constructor.
   * Takes in a bidder comparator.
   * 
   * @param BidderComparator
   */
  public SingleMindedGreedyAllocation(Comparator<B> BidderComparator) {
    this.BidderComparator = BidderComparator;
  }
  
  
  @Override
  public MarketAllocation<SingleMindedMarket<G, B>, G, B> Solve(SingleMindedMarket<G, B> market) throws IloException, AllocationAlgoException, BidderCreationException, GoodsCreationException, AllocationException, GoodsException, MarketAllocationException {
    // The allocation is zero at the beginning. 
    HashBasedTable<G, B, Integer> greedyAllocation = HashBasedTable.create();
    for(G good : market.getGoods()){
      for(B bidder : market.getBidders()){
        greedyAllocation.put(good, bidder, 0);
      }
    }
    // Make an ArrayList of BidderReward so that we can order the rewards.
    ArrayList<B> listOfBidders = new ArrayList<B>(market.getBidders());
    Collections.sort(listOfBidders, this.BidderComparator);
    HashSet<B> blockedBidders = new HashSet<B>();
    // For each bidder, in order
    for (B bidder : listOfBidders) {
      //System.out.println(bidder + " - " + bidder.getReward() / Math.sqrt(bidder.getDemand()));
      if (!blockedBidders.contains(bidder)) {
        // Allocate this bidder
        for (G good : market.getGoods()) {
          if (bidder.demandsGood(good)) {
            greedyAllocation.put(good, bidder, 1);
            // Remove all bidders (other than the current bidder) that wanted this item.
            for (B otherBidder : listOfBidders) {
              if (otherBidder != bidder && otherBidder.demandsGood(good)) {
                blockedBidders.add(otherBidder);
              }
            }
          }
        }
      }
    }
    return new MarketAllocation<SingleMindedMarket<G, B>, G, B>(market, greedyAllocation, this.getObjectiveFunction());
  }

  @Override
  public ObjectiveFunction getObjectiveFunction() {
    return new SingleStepObjectiveFunction();
  }

}
