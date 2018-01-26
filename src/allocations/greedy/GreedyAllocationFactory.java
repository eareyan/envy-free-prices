package allocations.greedy;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.comparators.BiddersComparatorBy1ToSqrtIRatio;
import structures.comparators.BiddersComparatorByRToSqrtIRatio;
import structures.comparators.BiddersComparatorByReward;
import structures.comparators.GoodsComparatorByRemainingSupply;

/**
 * This class implements logic to produce GreedyAllocation objects.
 * 
 * @author Enrique Areyan Viqueira
 *
 */
public class GreedyAllocationFactory<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> {

  /**
   * The classical allocation algorithm. Order bidders by R_j/sqrt(I_j) and goods by ascending order of remaining supply.
   * 
   * @return
   */
  public static <M extends Market<G, B>, G extends Goods, B extends Bidder<G>> GreedyAllocation<M, G, B> GreedyAllocation() {
    return new GreedyAllocation<M, G, B>(
        new BiddersComparatorByRToSqrtIRatio<G, B>(),
        new GoodsComparatorByRemainingSupply<G>(GoodsComparatorByRemainingSupply.Order.ascending), 
        Integer.MAX_VALUE);
  }
  
  /**
   * The egalitarian allocation algorithm. Order bidders by 1/sqrt(I) and goods by ascending order of remaining supply.
   * 
   * @return
   */
  public static <M extends Market<G, B>, G extends Goods, B extends Bidder<G>> GreedyAllocation<M, G, B> GreedyEgalitarianAllocation() {
    return new GreedyAllocation<M, G, B>(
        new BiddersComparatorBy1ToSqrtIRatio<G, B>(),
        new GoodsComparatorByRemainingSupply<G>(GoodsComparatorByRemainingSupply.Order.ascending), 
        Integer.MAX_VALUE);
  }
  
  /**
   * The algorithm that allocates only to the bidder with highest reward and goods by ascending order of remaining supply.
   * 
   * @return
   */
  public static <M extends Market<G, B>, G extends Goods, B extends Bidder<G>> GreedyAllocation<M, G, B> GreedyMaxBidderAllocation() {
    return new GreedyAllocation<M, G, B>(
        new BiddersComparatorByReward<G, B>(),
        new GoodsComparatorByRemainingSupply<G>(GoodsComparatorByRemainingSupply.Order.ascending), 
        1);    
  }

}
