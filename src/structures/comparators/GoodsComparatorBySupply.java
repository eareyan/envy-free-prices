package structures.comparators;

import java.util.Comparator;

import structures.Goods;

/**
 * This class implements a comparator to compare goods by their 
 * remaining supply.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GoodsComparatorBySupply implements Comparator<Goods> {
  
  /**
   * order 1 means ASC and -1 means DESC, any other means no order
   */
  protected int Order = 0;
  
  /**
   * Constructor.
   * 
   * @param Order - the order in which to order goods.
   */
  public GoodsComparatorBySupply(int Order) {
    this.Order = Order;
  }

  /**
   * Constructor.
   */
  public GoodsComparatorBySupply() {

  }

  @Override
  public int compare(Goods U1, Goods U2) {
    if (this.Order == -1) {
      if (U1.getSupply() < U2.getSupply()){
        return 1;
      }else if (U1.getSupply() > U2.getSupply()){
        return -1;
      }
    } else if (this.Order == 1) {
      if (U1.getSupply() < U2.getSupply()){
        return -1;
      }else if (U1.getSupply() > U2.getSupply()){
        return 1;
      }
    }
    return 0;
  }
  
}
