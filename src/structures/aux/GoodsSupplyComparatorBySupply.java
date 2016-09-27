package structures.aux;

import java.util.Comparator;

/**
 * This class implements a comparator to compare goods by their 
 * remaining supply.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GoodsSupplyComparatorBySupply implements Comparator<GoodSupply> {
  
  /**
   * order 1 means ASC and -1 means DESC, any other means no order
   */
  protected int Order = 0;
  
  /**
   * Constructor.
   * 
   * @param Order - the order in which to order goods.
   */
  public GoodsSupplyComparatorBySupply(int Order) {
    this.Order = Order;
  }

  /**
   * Constructor.
   */
  public GoodsSupplyComparatorBySupply() {

  }

  @Override
  public int compare(GoodSupply U1, GoodSupply U2) {
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
