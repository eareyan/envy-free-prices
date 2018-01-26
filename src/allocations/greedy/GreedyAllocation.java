package allocations.greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import allocations.interfaces.AllocationAlgo;
import allocations.objectivefunction.SingleStepObjectiveFunction;

import com.google.common.collect.HashBasedTable;

/**
 * This class implements greedy allocation algorithm. The class is parameterized so that a comparator of bidders and goods is received as a parameter. An extra
 * parameter, maxNumberAllocatedBidders caps the number of allowable allocated bidders. This parameter is used to implement a variant where only the bidder with
 * highest value is allocated. This algorithm has an approximation ratio of m (number of bidders) w.r.t the optimal welfare.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyAllocation<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> implements AllocationAlgo<M, G, B> {

  /**
   * Bidder comparator.
   */
  private final Comparator<B> BidderComparator;

  /**
   * Goods comparator
   */
  private final Comparator<G> GoodsComparator;
  
  /**
   * Default cap on the number of allocated bidders.
   */
  private final int maxNumberAllocatedBidders;
  
  /**
   * Constructor.
   * 
   * @param BidderComparator
   * @param GoodsSupplyComparator
   * @param maxNumberAllocatedBidders
   */
  public GreedyAllocation(Comparator<B> BidderComparator, Comparator<G> GoodsSupplyComparator, int maxNumberAllocatedBidders) {
    this.BidderComparator = BidderComparator;
    this.GoodsComparator = GoodsSupplyComparator;
    this.maxNumberAllocatedBidders = maxNumberAllocatedBidders;
  }

  /**
   * Solve for the greedy allocation.
   * 
   * @param market
   *          - the market object to allocate.
   * @throws AllocationException
   * @throws GoodsException
   * @throws MarketAllocationException
   */
  public MarketAllocation<M, G, B> Solve(M market) throws AllocationException, GoodsException, MarketAllocationException {
    // MAKE SHALLOW COPY OF BIDDERS - that is OK, you get the pointers anyway,
    // which you can't change because they provide no mutable fields.
    ArrayList<B> bidders = new ArrayList<B>(market.getBidders());
    // Sort the copy of the list of market's bidders.
    Collections.sort(bidders, this.BidderComparator);
    // MAKE SHALLOW COPY OF GOODS.
    ArrayList<G> goods = new ArrayList<G>(market.getGoods());
    // Set the remaining supply of each good to be their initial supply.
    // This will be used to sort the users.
    for (G good : goods) {
      good.setRemainingSupply(good.getSupply());
    }
    // Make the ArrayList that will store the result of the algorithm.
    // The allocation is zero at the beginning.
    HashBasedTable<G, B, Integer> greedyAllocation = HashBasedTable.create();
    for (G good : market.getGoods()) {
      for (B bidder : market.getBidders()) {
        greedyAllocation.put(good, bidder, 0);
      }
    }
    int totalAllocatedBidders = 0;
    // Allocate each bidder, if possible, one at a time.
    for (B bidder : bidders) {
      int totalAvailableSupply = 0;
      // First, compute if there is enough supply of goods to satisfy this bidder.
      for (G good : goods) {
        if (bidder.demandsGood(good) && good.getRemainingSupply() > 0) {
          totalAvailableSupply += good.getRemainingSupply();
        }
      }
      // Check if there is enough supply to satisfy the bidder.
      if (totalAvailableSupply >= bidder.getDemand()) {
        // Order goods
        Collections.sort(goods, this.GoodsComparator);
        // Try to allocate goods to this bidder, one good at the time.
        int totalAllocationToBidderSoFar = 0;
        for (G good : goods) {
          // If the bidder is not completely allocated.
          if (totalAllocationToBidderSoFar < bidder.getDemand()) {
            // If good is in the bidder demand set, AND there is supply
            // remaining from this good.
            if (bidder.demandsGood(good) && good.getRemainingSupply() > 0) {
              int amount = Math.min(bidder.getDemand() - totalAllocationToBidderSoFar, good.getRemainingSupply());
              greedyAllocation.put(good, bidder, amount);
              good.setRemainingSupply(good.getRemainingSupply() - amount);
              totalAllocationToBidderSoFar += amount;
            }
          } else {
            // Optimization: If the bidder is already allocated, move on to the next bidder.
            break;
          }
        }
      }
      totalAllocatedBidders++;
      if(totalAllocatedBidders + 1 > this.maxNumberAllocatedBidders) {
        break;
      }
    }
    return new MarketAllocation<M, G, B>(market, greedyAllocation, this.getObjectiveFunction());
  }

  @Override
  public SingleStepObjectiveFunction getObjectiveFunction() {
    return new SingleStepObjectiveFunction();
  }

  @Override
  public String toString() {
    return "GreedyAllocation which always uses SingleStepFunction objective";
  }

}
