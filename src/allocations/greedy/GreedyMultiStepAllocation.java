package allocations.greedy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.comparators.GoodsComparatorByRemainingSupply;
import structures.exceptions.AllocationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import allocations.error.AllocationAlgoErrorCodes;
import allocations.error.AllocationAlgoException;
import allocations.interfaces.AllocationAlgo;
import allocations.objectivefunction.interfaces.ObjectiveFunction;

import com.google.common.collect.HashBasedTable;

/**
 * This class implements greedy allocation with multi-steps.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyMultiStepAllocation<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> implements AllocationAlgo<M, G, B> {

  /**
   * stepSize. Impressions get allocated in multiples of this step only.
   */
  protected int stepSize;

  /**
   * objective function that indicates how good is to allocate one chunk of a
   * bidder.
   */
  protected ObjectiveFunction f;

  /**
   * Constructor.
   * 
   * @param stepSize
   *          - the number of copies to allocate at each iteration of the
   *          algorithm.
   * @param f
   *          - the objective function of bidders.
   * @throws AllocationAlgoException
   */
  public GreedyMultiStepAllocation(int stepSize, ObjectiveFunction f)
      throws AllocationAlgoException {
    if (stepSize <= 0) {
      throw new AllocationAlgoException(AllocationAlgoErrorCodes.STEP_NEGATIVE);
    }
    this.stepSize = stepSize;
    this.f = f;
  }

  /**
   * Constructor. Only takes step size and uses a SingleStepFunction as the
   * default objective function.
   * 
   * @param stepSize
   * @throws AllocationAlgoException
   */
  /*public GreedyMultiStepAllocation(int stepSize) throws AllocationAlgoException {
    this(stepSize, new SingleStepFunction());
  }*/

  /**
   * Solve method. Returns an allocation.
   * 
   * @param market
   *          - a Market object.
   * @return an allocation
   * @throws GoodsException 
   * @throws AllocationException 
   * @throws MarketAllocationException 
   */
  public MarketAllocation<M, G, B> Solve(M market) throws GoodsException, AllocationException, MarketAllocationException {
    HashBasedTable<G, B,Integer> allocation = HashBasedTable.create();
    for(G good : market.getGoods()){
      for(B bidder : market.getBidders()){
        allocation.put(good, bidder, 0);
      }
    }
    
    HashMap<B, Integer> currentAllocationToBidder = new HashMap<B, Integer>();
    /*
     * First, compute a queue of goods. A good's remaining supply is set to its
     * initial supply.
     */
    PriorityQueue<G> goodsQueue = new PriorityQueue<G>(new GoodsComparatorByRemainingSupply<G>(GoodsComparatorByRemainingSupply.Order.ascending));
    for (G good : market.getGoods()) {
      good.setRemainingSupply(good.getSupply());
      goodsQueue.add(good);
    }
    /*
     * Second, compute a queue of bidders. A bidder is added to the queue if it
     * has a positive value and has not been fully allocated.
     */
    PriorityQueue<bidderValue> biddersQueue = new PriorityQueue<bidderValue>(new BidderValueComparator());
    for (B bidder : market.getBidders()) {
      currentAllocationToBidder.put(bidder, 0); // Allocation to any bidder to start is zero.
      double value = this.f.getObjective(bidder.getReward(), bidder.getDemand(), currentAllocationToBidder.get(bidder) + this.stepSize)
                     - this.f.getObjective(bidder.getReward(), bidder.getDemand(), currentAllocationToBidder.get(bidder));
      if (currentAllocationToBidder.get(bidder) + this.stepSize <= bidder.getDemand() && value > 0) {
        biddersQueue.add(new bidderValue(bidder, value));
      }
    }
    boolean goodNotFound = true;
    bidderValue bidderValue;
    while ((bidderValue = biddersQueue.poll()) != null) {
      G good;
      B bidder = bidderValue.getBidder();
      ArrayList<G> auxGoodsSupplyList = new ArrayList<G>();
      // Looking for a good connected to this bidder with enough supply.
      while (goodNotFound && (good = goodsQueue.poll()) != null) {
        if (bidder.demandsGood(good) && good.getSupply() >= this.stepSize) {
          // This good is connected to this bidder and has at least s copies available.
          allocation.put(good, bidder, allocation.get(good, bidder) + this.stepSize);
          currentAllocationToBidder.put(bidder, currentAllocationToBidder.get(bidder) + this.stepSize);
          good.setRemainingSupply(good.getRemainingSupply() - this.stepSize);
          goodNotFound = false;
        }
        if (good.getRemainingSupply() >= this.stepSize) {
          // This good still has some to give. Put it in this auxiliary queue to
          // be put back in the goodsQueue later.
          auxGoodsSupplyList.add(good);
        }
      }
      // Add goods that were pulled out of the goodsQueue back to the queue.
      for (G g : auxGoodsSupplyList) {
        goodsQueue.add(g);
      }
      /*
       * If we did found a good connected to this bidder and the bidder still
       * requires some, then compute its value.
       */
      if (!goodNotFound && currentAllocationToBidder.get(bidder) + this.stepSize <= bidder.getDemand()) {
        double value = this.f.getObjective(bidder.getReward(), bidder.getDemand(), currentAllocationToBidder.get(bidder) + this.stepSize)
                       - this.f.getObjective(bidder.getReward(), bidder.getDemand(), currentAllocationToBidder.get(bidder));
        if (value > 0) {
          // If value of the bidder is still positive, then update value and add
          // to the queue.
          bidderValue.updateValue(value);
          biddersQueue.add(bidderValue);
        }
      }
      goodNotFound = true;
    }
    return new MarketAllocation<M, G, B>(market, allocation, this.f);
  }

  /**
   * This auxiliary class represents a bidder and its value.
   * 
   * @author Enrique Areyan Viqueira
   */
  public class bidderValue {

    /**
     * Bidder index.
     */
    final protected B bidder;

    /**
     * Bidder value.
     */
    protected double value;

    /**
     * Constructor.
     * 
     * @param bidder - a bidder object.
     * @param value - the bidder value.
     */
    public bidderValue(B bidder, double value) {
      this.bidder = bidder;
      this.value = value;
    }

    /**
     * Getter.
     * 
     * @return bidder index.
     */
    public B getBidder() {
      return this.bidder;
    }

    /**
     * Getter.
     * 
     * @return bidder value.
     */
    public double getValue() {
      return this.value;
    }

    /**
     * Updates the value of a bidder.
     * 
     * @param value - the new value of the bidder.
     */
    public void updateValue(double value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "(" + this.bidder + "," + this.value + ")";
    }
  }

  /**
   * Comparator to compare bidders by value.
   * 
   * @author Enrique Areyan Viqueira
   */
  public class BidderValueComparator implements Comparator<bidderValue> {
    @Override
    public int compare(bidderValue c1, bidderValue c2) {
      if (c1.getValue() < c2.getValue()) {
        return 1;
      } else if (c1.getValue() > c2.getValue()) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  /**
   * Implements AllocationAlgoInterface
   */
  @Override
  public ObjectiveFunction getObjectiveFunction() {
    return this.f;
  }
  
  @Override
  public String toString(){
    return "GreedyMultiStepAlgorithm using step " + this.stepSize + " and objective" + this.f;
  }

}