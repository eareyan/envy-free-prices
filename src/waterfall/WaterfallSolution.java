package waterfall;

import structures.Bidder;
import structures.Goods;
import structures.Market;

import com.google.common.collect.Table;

/**
 * Solution of the waterfall algorithm. Contains and allocation and a matrix of prices.
 * 
 * @author Enrique Areyan Viqueira
 *
 * @param <M>
 * @param <G>
 * @param <B>
 */
public class WaterfallSolution<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> {

  /**
   * Allocation.
   */
  private final Table<G, B, Integer> allocation;

  /**
   * Prices.
   */
  private final Table<G, B, Double> prices;

  public WaterfallSolution(Table<G, B, Integer> allocation, Table<G, B, Double> prices) {
    this.allocation = allocation;
    this.prices = prices;
  }

  /**
   * Returns the number of goods allocated to the bidder.
   * 
   * @param good
   * @param bidder
   * @return
   */
  public int getAllocation(G good, B bidder) {
    return this.allocation.get(good, bidder);
  }

  /**
   * Returns the price of per good quoted to the bidder.
   * 
   * @param good
   * @param bidder
   * @return
   */
  public double getPrice(G good, B bidder) {
    return this.prices.get(good, bidder);
  }

}
