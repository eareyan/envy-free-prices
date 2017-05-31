package waterfall;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;

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
  
  /**
   * Helper method to print the allocation.
   * 
   * @param allocation
   */
  public void printAllocationTable() {
    for (Entry<B, Map<G, Integer>> z : this.allocation.columnMap().entrySet()) {
      System.out.print(z.getKey() + "\t\t");
    }
    System.out.println();
    for (Entry<G, Map<B, Integer>> x : this.allocation.rowMap().entrySet()) {
      System.out.print(String.format("%-38s \t", x.getKey()));
      for (Entry<B, Integer> y : x.getValue().entrySet()) {
        System.out.print(y.getValue() + "\t");
      }
      System.out.println("");
    }
  }

  /**
   * Helper method to print prices.
   * 
   * @param prices
   */
  public void printPricesTable() {
    DecimalFormat df = new DecimalFormat("#.00");
    for (Entry<B, Map<G, Double>> z : this.prices.columnMap().entrySet()) {
      System.out.print(z.getKey() + "\t\t");
    }
    System.out.println();
    for (Entry<G, Map<B, Double>> x : this.prices.rowMap().entrySet()) {
      System.out.print(String.format("%-38s \t", x.getKey()));
      for (Entry<B, Double> y : x.getValue().entrySet()) {
        System.out.print(df.format(y.getValue()) + "\t");
      }
      System.out.println("");
    }
  }

}
