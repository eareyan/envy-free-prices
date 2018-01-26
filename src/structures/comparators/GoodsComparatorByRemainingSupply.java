package structures.comparators;

import java.util.Comparator;

import structures.Goods;

/**
 * This class implements a comparator to compare goods by their remaining supply.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GoodsComparatorByRemainingSupply<G extends Goods> implements Comparator<G> {

  /**
   * Define ordering enum.
   */
  public static enum Order {
    ascending, descending
  }

  /**
   * The actual ordering used in an instance of this class.
   */
  private final Order order;

  /**
   * Constructor.
   * 
   * @param Order - the order in which to order goods.
   */
  public GoodsComparatorByRemainingSupply(Order order) {
    this.order = order;
  }

  @Override
  public int compare(G g1, G g2) {
    // Descending order of remaining supply.
    if (this.order == Order.descending) {
      if (g1.getRemainingSupply() < g2.getRemainingSupply()) {
        return 1;
      } else if (g1.getRemainingSupply() > g2.getRemainingSupply()) {
        return -1;
      }
    } else if (this.order == Order.ascending) {
      // Ascending order of remaining supply.
      if (g1.getRemainingSupply() < g2.getRemainingSupply()) {
        return -1;
      } else if (g1.getRemainingSupply() > g2.getRemainingSupply()) {
        return 1;
      }
    }
    // Does not order.
    return 0;
  }

}
