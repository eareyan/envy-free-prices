package structures.comparators;

import java.util.Comparator;

import structures.Bidder;
import structures.Goods;

/**
 * This class implements a comparator to compare bidders by the ratio 1 to
 * demand (1 / I_j).
 * 
 * @author Enrique Areyan Viqueira
 */
public class BiddersComparatorBy1ToSqrtIRatio<G extends Goods, B extends Bidder<G>> implements Comparator<B> {

  @Override
  public int compare(B c1, B c2) {
    if (1 / Math.sqrt(c1.getDemand()) < 1 / Math.sqrt(c2.getDemand())) {
      return 1;
    } else if (1 / Math.sqrt(c1.getDemand()) > 1 / Math.sqrt(c2.getDemand())) {
      return -1;
    }
    return 0;
  }

}
