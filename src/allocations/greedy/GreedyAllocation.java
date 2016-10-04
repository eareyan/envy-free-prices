package allocations.greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.comparators.BiddersComparatorByRToSqrtIRatio;
import structures.comparators.GoodsComparatorByRemainingSupply;
import structures.exceptions.AllocationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import allocations.interfaces.AllocationAlgoInterface;
import allocations.objectivefunction.ObjectiveFunction;
import allocations.objectivefunction.SingleStepFunction;

import com.google.common.collect.HashBasedTable;

/**
 * This class implements greedy allocation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyAllocation implements AllocationAlgoInterface<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> {

  /**
   * Bidder comparator.
   */
  protected Comparator<Bidder<Goods>> BidderComparator;

  /**
   * Goods comparator
   */
  protected Comparator<Goods> GoodsComparator;

  /**
   * Constructor.
   * 
   * @param BidderComparator - a comparator to order bidders.
   * @param GoodsSupplyComparator - a comparator to order goods.
   */
  public GreedyAllocation(Comparator<Bidder<Goods>> BidderComparator, Comparator<Goods> GoodsSupplyComparator) {
    this.BidderComparator = BidderComparator;
    this.GoodsComparator = GoodsSupplyComparator;
  }

  /**
   * Constructor.
   * 
   * @param goodsOrder - order of remaining good supply. is 1 means ASC and -1 means
   *          DESC, any other means no order
   */
  public GreedyAllocation(int goodsOrder) {
    this.BidderComparator = new BiddersComparatorByRToSqrtIRatio();
    this.GoodsComparator = new GoodsComparatorByRemainingSupply(goodsOrder);
  }
  
  /**
   * Constructor.
   * Default ordering of bidders is by reward to square root of demand ratio.
   * Default ordering of goods is by ascending order of remaining supply.
   */
  public GreedyAllocation() {
    this(1);
  }
  
  /**
   * Solve for the greedy allocation.
   * 
   * @param market - the market object to allocate.
   * @throws AllocationException 
   * @throws GoodsException 
   * @throws MarketAllocationException 
   */
  public MarketAllocation<Goods, Bidder<Goods>> Solve(Market<Goods, Bidder<Goods>> market) throws AllocationException, GoodsException, MarketAllocationException {
    // MAKE SHALLOW COPY OF BIDDERS - that is OK, you get the pointers anyway,
    // which you can't change up because they provide no mutable fields.
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>(market.getBidders());
    // Sort the copy of the list of market's bidders.
    Collections.sort(bidders, this.BidderComparator);
    // MAKE SHALLOW COPY OF GOODS.
    ArrayList<Goods> goods = new ArrayList<Goods>(market.getGoods());
    // Set the remaining supply of each good to be their initial supply.
    // This will be used to sort the users.
    for(Goods good : goods){
      good.setRemainingSupply(good.getSupply());
    }
    // Make the ArrayList that will store the result of the algorithm.
    // The allocation is zero at the beginning. 
    HashBasedTable<Goods,Bidder<Goods>,Integer> greedyAllocation = HashBasedTable.create();
    for(Goods good : market.getGoods()){
      for(Bidder<Goods> bidder : market.getBidders()){
        greedyAllocation.put(good, bidder, 0);
      }
    }
    
    // Allocate each bidder, if possible, one at a time.
    for (Bidder<Goods> bidder : bidders) {
      int totalAvailableSupply = 0;
      // First, compute if there is enough supply of goods to satisfy this bidder.
      for (Goods good : goods) {
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
        for (Goods good : goods) {
          // If the good is in the bidder demand set, AND there is supply 
          // remaining from this good, AND the bidder is not completely allocated. 
          if (bidder.demandsGood(good) && good.getRemainingSupply() > 0 && totalAllocationToBidderSoFar < bidder.getDemand()) {
            int amount = Math.min(bidder.getDemand() - totalAllocationToBidderSoFar, good.getRemainingSupply());
            greedyAllocation.put(good, bidder, amount);
            good.setRemainingSupply(good.getRemainingSupply() - amount);
            totalAllocationToBidderSoFar += amount;
          }
        }
      }
    }
    return new MarketAllocation<Goods, Bidder<Goods>>(market, greedyAllocation, this.getObjectiveFunction());
  }

  @Override
  public ObjectiveFunction getObjectiveFunction() {
    return new SingleStepFunction();
  }

}
