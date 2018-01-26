package structures.comparators;

import java.util.Comparator;

import structures.Bidder;
import structures.Goods;

/**
 * This class implements a comparator to compare bidders by the ratio 1 to demand (1 / I_j). This is used for the egalitarian allocation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class BiddersComparatorBy1ToSqrtIRatio<G extends Goods, B extends Bidder<G>> implements Comparator<B> {

  @Override
  public int compare(B b1, B b2) {
    if (1 / Math.sqrt(b1.getDemand()) < 1 / Math.sqrt(b2.getDemand())) {
      return 1;
    } else if (1 / Math.sqrt(b1.getDemand()) > 1 / Math.sqrt(b2.getDemand())) {
      return -1;
    }
    return 0;
  }

}
