package algorithms.ascendingauction;

import java.util.Comparator;

/**
 * Compares a GoodPrice object by price ascending.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GoodPriceComparatorByPrice implements Comparator<GoodPrice> {

  @Override
  public int compare(GoodPrice o1, GoodPrice o2) {
    if(o1.price > o2.price) {
      return 1;
    } else if (o1.price < o2.price) {
      return -1;
    } else {
      return 0;
    }
  }

}
